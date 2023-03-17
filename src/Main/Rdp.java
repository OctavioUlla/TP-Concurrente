package Main;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Rdp {
    private final List<String> transiciones;
    private final Map<String, Map<String, Integer>> matriz;

    private final Map<String, Integer> plazaTokensMap;

    public Rdp(List<String> transiciones,
            Map<String, Map<String, Integer>> matrizIncidencia,
            Map<String, Integer> estadoInicial) {

        this.transiciones = Collections.unmodifiableList(transiciones);
        this.matriz = Collections.unmodifiableMap(matrizIncidencia);
        this.plazaTokensMap = estadoInicial;
    }

    public boolean disparar(String transicion) {

        if (!isSensibilizada(transicion)) {
            return false;
        }

        matriz.get(transicion)
                .entrySet()
                .forEach(plazaTok -> plazaTokensMap.merge(plazaTok.getKey(),
                        plazaTok.getValue(),
                        Integer::sum));

        return true;
    }

    public int getTokens(String plaza) {
        return plazaTokensMap.get(plaza);
    }

    public List<String> getTransicionesSensibilizadas() {
        return transiciones.stream()
                .filter(t -> isSensibilizada(t))
                .collect(Collectors.toList());
    }

    private boolean isSensibilizada(String transicion) {
        return matriz.get(transicion).entrySet().stream()
                .allMatch(plazaTok -> plazaTokensMap.get(plazaTok.getKey()) + plazaTok.getValue() >= 0);
    }
}
