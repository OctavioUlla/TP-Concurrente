package Main;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Rdp {
    private final List<String> plazas;
    private final List<String> transiciones;
    private final Map<String, int[]> matriz;

    private int[] estado;

    /**
     * @param matrizIncidencia : int[transiciones][plazas]
     */
    public Rdp(List<String> plazas,
            List<String> transiciones,
            Map<String, int[]> matrizIncidencia,
            int[] estadoInicial) {

        this.plazas = plazas;
        this.transiciones = transiciones;
        this.matriz = matrizIncidencia;
        this.estado = estadoInicial;
    }

    public boolean disparar(String transicion) {

        if (!isSensibilizada(transicion)) {
            return false;
        }

        for (int i = 0; i < plazas.size(); i++) {
            estado[i] += matriz.get(transicion)[i];
        }

        return true;
    }

    public int getTokens(String plaza) {
        return estado[plazas.indexOf(plaza)];
    }

    public List<String> getTransicionesSensibilizadas() {
        return transiciones.stream()
                .filter(t -> isSensibilizada(t))
                .collect(Collectors.toList());
    }

    private boolean isSensibilizada(String transicion) {
        int[] tokensNecesarios = matriz.get(transicion);

        return IntStream.range(0, transiciones.size())
                .allMatch(i -> estado[i] >= tokensNecesarios[i]);
    }
}
