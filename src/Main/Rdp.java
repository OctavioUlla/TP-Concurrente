package Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Rdp {

    private List<String> plazas;
    private List<String> transiciones;
    private int[][] matriz;
    private int[] estado;
    private int[] estadoInicial;

    /**
     * @param matrizIncidencia : int[transiciones][plazas]
     */
    public Rdp(List<String> plazas, List<String> transiciones, int[][] matrizIncidencia, int[] estadoInicial) {
        this.plazas = plazas;
        this.transiciones = transiciones;
        matriz = matrizIncidencia;
        this.estadoInicial = estadoInicial;

        estado = new int[estadoInicial.length];
        System.arraycopy(estadoInicial, 0, estado, 0, estado.length);
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

    public void Reset() {
        System.arraycopy(estadoInicial, 0, estado, 0, estado.length);
    }

    public int GetTokens(String plaza) {
        return estado[plazas.indexOf(plaza)];
    }

    public List<String> GetTransicionesSensibilizadas() {
        return transiciones.stream()
                .filter(t -> IsSensibilizada(transiciones.indexOf(t)))
                .collect(Collectors.toList());
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
