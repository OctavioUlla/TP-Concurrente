package Main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Rdp {
    private final List<String> plazas;
    private final List<String> transiciones;
    private final Map<String, int[]> matrizMap;
    private final int[] estadoInicial;

    private int[] estado;

    /**
     * @param matrizIncidencia : int[transiciones][plazas]
     */
    public Rdp(List<String> plazas, List<String> transiciones, int[][] matrizIncidencia, int[] estadoInicial) {
        this.plazas = plazas;
        this.transiciones = transiciones;
        this.estadoInicial = estadoInicial;

        matrizMap = new HashMap<String, int[]>();
        // Armar HashMap
        for (int i = 0; i < transiciones.size(); i++) {
            matrizMap.put(transiciones.get(i), matrizIncidencia[i]);
        }

        estado = new int[estadoInicial.length];
        System.arraycopy(estadoInicial, 0, estado, 0, estado.length);
    }

    public boolean Disparar(String transicion) {

        if (!IsSensibilizada(transicion)) {
            return false;
        }

        for (int i = 0; i < plazas.size(); i++) {
            estado[i] += matrizMap.get(transicion)[i];
        }

        return true;
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
        int[] tokensNecesarios = matrizMap.get(transicion);

        return IntStream.range(0, transiciones.size())
                .allMatch(i -> estado[i] >= tokensNecesarios[i]);
    }
}
