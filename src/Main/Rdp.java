package Main;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        if (IsSensibilizada(transicion)) {

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
                .filter(t -> IsSensibilizada(t))
                .collect(Collectors.toList());
    }

    private boolean IsSensibilizada(String transicion) {

        int transicionIndex = transiciones.indexOf(transicion);
        int[] tokensNecesarios = matriz[transicionIndex];

        return IntStream.range(0, transiciones.size())
                .allMatch(i -> estado[i] >= tokensNecesarios[i]);
    }
}
