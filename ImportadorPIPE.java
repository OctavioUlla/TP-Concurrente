import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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

        return null;
    }

    private void RellenarMatriz(
            Document doc,
            int[][] matrizIncidencia,
            List<String> plazas,
            List<String> transiciones) {

        NodeList arcos = doc.getElementsByTagName("arc");

        for (int i = 0; i < arcos.getLength(); i++) {

            Node arco = arcos.item(i);

            if (arco.getNodeType() == Node.ELEMENT_NODE) {
                Element arcoData = (Element) arco;

                String source = arcoData.getAttribute("source");

                String target = arcoData.getAttribute("target");

                Element inscription = (Element) arcoData
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

    private List<String> GetPlazas(Document doc) {

        List<String> plazas = new ArrayList<String>();

        NodeList plazasNodes = doc.getElementsByTagName("place");

        for (int i = 0; i < plazasNodes.getLength(); i++) {

            Node plaza = plazasNodes.item(i);

            if (plaza.getNodeType() == Node.ELEMENT_NODE) {
                Element plazaData = (Element) plaza;

                String plazaId = plazaData.getAttribute("id");

                plazas.add(plazaId);
            }
        }

        return plazas;
    }

    private List<String> GetTransiciones(Document doc) {

        List<String> transiciones = new ArrayList<String>();

        NodeList transicionesNodes = doc.getElementsByTagName("transition");

        for (int i = 0; i < transicionesNodes.getLength(); i++) {

            Node transicion = transicionesNodes.item(i);

            if (transicion.getNodeType() == Node.ELEMENT_NODE) {
                Element transicionData = (Element) transicion;

                String transicionId = transicionData.getAttribute("id");

                transiciones.add(transicionId);
            }
        }

        return transiciones;
    }
}