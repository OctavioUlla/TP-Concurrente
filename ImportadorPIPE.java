import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ImportadorPIPE implements IImportador {

    @Override
    public Rdp Importar(String filename) {

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

        List<String> plazas = GetPlazas(doc);
        List<String> transiciones = GetTransiciones(doc);

        int[][] matrizIncidencia = new int[transiciones.size()][plazas.size()];

        RellenarMatriz(doc, matrizIncidencia, plazas, transiciones);

        int[] estadoInicial = GetEstadoInicial(doc, plazas);

        return new Rdp(plazas, transiciones, matrizIncidencia, estadoInicial);
    }

    private List<String> GetPlazas(Document doc) {

        List<String> plazas = new ArrayList<String>();

        NodeList plazasNodes = doc.getElementsByTagName("place");

        for (int i = 0; i < plazasNodes.getLength(); i++) {

            Element plaza = (Element) plazasNodes.item(i);

            String plazaId = plaza.getAttribute("id");

            plazas.add(plazaId);
        }

        return plazas;
    }

    private int[] GetEstadoInicial(Document doc, List<String> plazas) {

        int[] estado = new int[plazas.size()];

        NodeList plazasNodes = doc.getElementsByTagName("place");

        for (int i = 0; i < plazasNodes.getLength(); i++) {

            Element plaza = (Element) plazasNodes.item(i);

            Element initialMarking = (Element) plaza
                    .getElementsByTagName("initialMarking")
                    .item(0);

            estado[i] = Integer.parseInt(initialMarking
                    .getElementsByTagName("value")
                    .item(0)
                    .getTextContent()
                    .split(",")[1]);
        }

        return estado;
    }

    private List<String> GetTransiciones(Document doc) {

        List<String> transiciones = new ArrayList<String>();

        NodeList transicionesNodes = doc.getElementsByTagName("transition");

        for (int i = 0; i < transicionesNodes.getLength(); i++) {

            Element transicion = (Element) transicionesNodes.item(i);

            String transicionId = transicion.getAttribute("id");

            transiciones.add(transicionId);
        }

        return transiciones;
    }

    private void RellenarMatriz(
            Document doc,
            int[][] matrizIncidencia,
            List<String> plazas,
            List<String> transiciones) {

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

            int plazaIndex;
            int transicionIndex;

            // Si source es una plaza, es una plaza de entrada
            // Y target una transicion
            if (plazas.contains(source)) {
                plazaIndex = plazas.indexOf(source);
                transicionIndex = transiciones.indexOf(target);
                // Signo negativo porque es plaza de entrada
                weight *= -1;
            }
            // Source es una transicion,
            // Target es plaza de salida
            else {
                plazaIndex = plazas.indexOf(target);
                transicionIndex = transiciones.indexOf(source);
            }

            matrizIncidencia[transicionIndex][plazaIndex] = weight;
        }
    }
}
