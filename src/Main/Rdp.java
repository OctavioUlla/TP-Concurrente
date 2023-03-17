package Main;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Rdp {
    private final List<String> plazas;
    private final List<String> transiciones;
    private final Map<String, Map<String, Integer>> matriz;

    private final Map<String, Integer> estado;

    /**
     * @param matrizIncidencia : int[transiciones][plazas]
     */
    public Rdp(List<String> plazas,
            List<String> transiciones,
            Map<String, Map<String, Integer>> matrizIncidencia,
            Map<String, Integer> estadoInicial) {

        this.plazas = Collections.unmodifiableList(plazas);
        this.transiciones = Collections.unmodifiableList(transiciones);
        this.matriz = Collections.unmodifiableMap(matrizIncidencia);
        this.estado = Collections.unmodifiableMap(estadoInicial);
    }

    public boolean disparar(String transicion) {

        if (!isSensibilizada(transicion)) {
            return false;
        }

        Map<String, Integer> plazasMatriz = matriz.get(transicion);

        for (String plaza : plazas) {
            estado.put(plaza, estado.get(plaza) + plazasMatriz.get(plaza));
        }

        return true;
    }

    public int getTokens(String plaza) {
        return estado.get(plaza);
    }

    public List<String> getTransicionesSensibilizadas() {
        return transiciones.stream()
                .filter(t -> isSensibilizada(t))
                .collect(Collectors.toList());
    }

    private boolean isSensibilizada(String transicion) {
        Map<String, Integer> plazasNecesarias = matriz.get(transicion);

        return plazasNecesarias.keySet().stream()
                .allMatch(p -> estado.get(p) + plazasNecesarias.get(p) >= 0);
    }
}
