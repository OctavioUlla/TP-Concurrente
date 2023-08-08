package Importadores;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Main.Rdp;
import Main.Temporizacion;

public class ImportadorPetrinator implements IImportador {

    @Override
    public Rdp importar(String filename) {
        File xmlFile = new File(filename);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc;

        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(xmlFile);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Plazas tienen id y label, pueden ser distintas
        Map<String, String> plazas = getPlazas(doc);
        Map<String, String> transiciones = getTransiciones(doc);
        SortedMap<String, Temporizacion> transicionesTemporizadas = getTransicionesTemporizadas(doc);

        SortedMap<String, SortedMap<String, Integer>> matrizIncidencia = new TreeMap<String, SortedMap<String, Integer>>();

        rellenarMatriz(doc, matrizIncidencia, plazas, transiciones);

        Map<String, Integer> estadoInicial = getEstadoInicial(doc);

        return new Rdp(matrizIncidencia, estadoInicial, transicionesTemporizadas);
    }

    private Map<String, String> getPlazas(Document doc) {
        Map<String, String> plazas = new HashMap<String, String>();

        NodeList plazasNodes = doc.getElementsByTagName("place");

        for (int i = 0; i < plazasNodes.getLength(); i++) {

            Element plaza = (Element) plazasNodes.item(i);

            String plazaId = plaza.getElementsByTagName("id")
                    .item(0)
                    .getTextContent();

            String plazaLabel = plaza.getElementsByTagName("label")
                    .item(0)
                    .getTextContent();

            plazas.put(plazaId, plazaLabel);
        }

        return plazas;
    }

    private Map<String, Integer> getEstadoInicial(Document doc) {
        Map<String, Integer> estado = new HashMap<String, Integer>();

        NodeList plazasNodes = doc.getElementsByTagName("place");

        for (int i = 0; i < plazasNodes.getLength(); i++) {

            Element plaza = (Element) plazasNodes.item(i);

            String plazaName = plaza.getElementsByTagName("label")
                    .item(0)
                    .getTextContent();

            int tokens = Integer.parseInt(plaza.getElementsByTagName("tokens")
                    .item(0)
                    .getTextContent());

            estado.put(plazaName, tokens);
        }

        return estado;
    }

    private Map<String, String> getTransiciones(Document doc) {
        Map<String, String> transiciones = new HashMap<String, String>();

        NodeList transicionesNodes = doc.getElementsByTagName("transition");

        for (int i = 0; i < transicionesNodes.getLength(); i++) {

            Element transicion = (Element) transicionesNodes.item(i);

            String transicionId = transicion.getElementsByTagName("id")
                    .item(0)
                    .getTextContent();

            String transicionLabel = transicion.getElementsByTagName("label")
                    .item(0)
                    .getTextContent();

            transiciones.put(transicionId, transicionLabel);
        }

        return transiciones;
    }

    private SortedMap<String, Temporizacion> getTransicionesTemporizadas(Document doc) {
        SortedMap<String, Temporizacion> transicionesTemp = new TreeMap<String, Temporizacion>();

        NodeList transicionesNodes = doc.getElementsByTagName("transition");

        for (int i = 0; i < transicionesNodes.getLength(); i++) {

            Element transicion = (Element) transicionesNodes.item(i);

            String transicionLabel = transicion.getElementsByTagName("label")
                    .item(0)
                    .getTextContent();

            Boolean temporal = Boolean.parseBoolean(transicion.getElementsByTagName("timed")
                    .item(0)
                    .getTextContent());

            if (temporal) {
                Element propiedadesEstocasticas = (Element) transicion.getElementsByTagName("stochasticProperties")
                        .item(0);

                if (propiedadesEstocasticas.getAttribute("distribution").equals("Uniform")) {
                    long alpha = (long) Float.parseFloat(propiedadesEstocasticas.getAttribute("var1"));
                    long beta = (long) Float.parseFloat(propiedadesEstocasticas.getAttribute("var2"));

                    transicionesTemp.put(transicionLabel, new Temporizacion(alpha, beta));
                }
            }
        }

        return transicionesTemp;
    }

    private void rellenarMatriz(Document doc, SortedMap<String, SortedMap<String, Integer>> matrizIncidencia,
            Map<String, String> plazas, Map<String, String> transiciones) {
        // Rellenar con ceros
        transiciones.forEach((tId, tLabel) -> {
            SortedMap<String, Integer> ceros = new TreeMap<String, Integer>();
            plazas.forEach((pId, pLabel) -> ceros.put(pLabel, 0));
            matrizIncidencia.put(tLabel, ceros);
        });

        NodeList arcos = doc.getElementsByTagName("arc");

        for (int i = 0; i < arcos.getLength(); i++) {

            Element arco = (Element) arcos.item(i);

            String source = arco.getElementsByTagName("sourceId")
                    .item(0)
                    .getTextContent();

            String target = arco.getElementsByTagName("destinationId")
                    .item(0)
                    .getTextContent();

            int weight = Integer.parseInt(arco.getElementsByTagName("multiplicity")
                    .item(0)
                    .getTextContent());

            String plaza;
            String transicion;

            // Si source es una plaza, es una plaza de entrada
            // Y target una transicion
            if (plazas.keySet().contains(source)) {
                plaza = plazas.get(source);
                transicion = transiciones.get(target);
                // Signo negativo porque es plaza de entrada
                weight *= -1;
            }
            // Source es una transicion,
            // Target es plaza de salida
            else {
                plaza = plazas.get(target);
                transicion = transiciones.get(source);
            }

            matrizIncidencia.get(transicion).put(plaza, weight);
        }
    }
}
