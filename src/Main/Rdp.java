package Main;

import java.util.ArrayList;
import java.util.List;

public class Rdp {

    private List<String> plazas;
    private List<String> transiciones;
    private int[][] matriz;
    private int[] estado;

    /**
     * @param matrizIncidencia : int[transiciones][plazas]
     */
    public Rdp(List<String> plazas, List<String> transiciones, int[][] matrizIncidencia, int[] estadoInicial) {
        this.plazas = plazas;
        this.transiciones = transiciones;
        matriz = matrizIncidencia;
        estado = estadoInicial;
    }

    public boolean Disparar(String transicion) {

        int transicionIndex = transiciones.indexOf(transicion);

        if (IsSensibilizada(transicionIndex)) {

            for (int i = 0; i < plazas.size(); i++) {
                estado[i] += matriz[transicionIndex][i];
            }

            return true;
        }

        return false;
    }

    public int GetTokens(String plaza) {
        return estado[plazas.indexOf(plaza)];
    }

    public List<String> GetTransicionesSensibilizadas() {
        List<String> transicionesSensibilizadas = new ArrayList<String>();

        for (int i = 0; i < transiciones.size(); i++) {
            if (IsSensibilizada(i)) {
                transicionesSensibilizadas.add(transiciones.get(i));
            }
        }

        return transicionesSensibilizadas;
    }

    private boolean IsSensibilizada(int transicionIndex) {

        for (int i = 0; i < plazas.size(); i++) {

            int tokens = matriz[transicionIndex][i];

            // Compara la cantidad de tokens por la cantidad de necesarios por esta plaza
            // Si la suma es menos que 0 no esta sensibilizada
            if (tokens < 0) {
                if (tokens + estado[i] < 0) {
                    // No hay tokens necesarios para sensibilizar transición
                    return false;
                }
            }
        }

        return true;
    }
}
