package Importador;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

public class ImportadorPIPE implements IImportador {

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

        Set<String> plazas = getPlazas(doc);
        Set<String> transiciones = getTransiciones(doc);

        SortedMap<String, SortedMap<String, Integer>> matrizIncidencia = new TreeMap<String, SortedMap<String, Integer>>();

        rellenarMatriz(doc, matrizIncidencia, plazas, transiciones);

        Map<String, Integer> estadoInicial = getEstadoInicial(doc);

        return new Rdp(matrizIncidencia, estadoInicial);
    }

    private Set<String> getPlazas(Document doc) {

        Set<String> plazas = new HashSet<String>();

        NodeList plazasNodes = doc.getElementsByTagName("place");

        for (int i = 0; i < plazasNodes.getLength(); i++) {

            Element plaza = (Element) plazasNodes.item(i);

            String plazaId = plaza.getAttribute("id");

            plazas.add(plazaId);
        }

        return plazas;
    }

    private Map<String, Integer> getEstadoInicial(Document doc) {

        Map<String, Integer> estado = new HashMap<String, Integer>();

        NodeList plazasNodes = doc.getElementsByTagName("place");

        for (int i = 0; i < plazasNodes.getLength(); i++) {

            Element plaza = (Element) plazasNodes.item(i);

            String plazaName = plaza.getAttribute("id");

            Element initialMarking = (Element) plaza
                    .getElementsByTagName("initialMarking")
                    .item(0);

            int tokens = Integer.parseInt(initialMarking
                    .getElementsByTagName("value")
                    .item(0)
                    .getTextContent()
                    .split(",")[1]);

            estado.put(plazaName, tokens);
        }

        return estado;
    }

    private Set<String> getTransiciones(Document doc) {

        Set<String> transiciones = new HashSet<String>();

        NodeList transicionesNodes = doc.getElementsByTagName("transition");

        for (int i = 0; i < transicionesNodes.getLength(); i++) {

            Element transicion = (Element) transicionesNodes.item(i);

            String transicionId = transicion.getAttribute("id");

            transiciones.add(transicionId);
        }

        return transiciones;
    }

    private void rellenarMatriz(
            Document doc,
            SortedMap<String, SortedMap<String, Integer>> matrizIncidencia,
            Set<String> plazas,
            Set<String> transiciones) {

        // Rellenar con ceros
        transiciones.stream().forEach(t -> {
            SortedMap<String, Integer> ceros = new TreeMap<String, Integer>();
            plazas.forEach(p -> ceros.put(p, 0));
            matrizIncidencia.put(t, ceros);
        });

        NodeList arcos = doc.getElementsByTagName("arc");

        for (int i = 0; i < arcos.getLength(); i++) {

            Element arco = (Element) arcos.item(i);

            String source = arco.getAttribute("source");

            String target = arco.getAttribute("target");

            Element inscription = (Element) arco
                    .getElementsByTagName("inscription")
                    .item(0);

            int weight = Integer.parseInt(inscription
                    .getElementsByTagName("value")
                    .item(0)
                    .getTextContent()
                    .split(",")[1]);

            String plaza;
            String transicion;

            // Si source es una plaza, es una plaza de entrada
            // Y target una transicion
            if (plazas.contains(source)) {
                plaza = source;
                transicion = target;
                // Signo negativo porque es plaza de entrada
                weight *= -1;
            }
            // Source es una transicion,
            // Target es plaza de salida
            else {
                plaza = target;
                transicion = source;
            }

            matrizIncidencia.get(transicion).put(plaza, weight);
        }
    }
}
