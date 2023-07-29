package Main;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Rdp {
    private final Map<String, Map<String, Integer>> matriz;

    private Map<String, Integer> marcado;

    public Rdp(Map<String, Map<String, Integer>> matrizIncidencia, Map<String, Integer> marcadoInicial) {
        this.matriz = Collections.unmodifiableMap(matrizIncidencia);
        this.marcado = marcadoInicial;
    }

    public boolean disparar(String transicion) {

        if (!isSensibilizada(transicion)) {
            return false;
        }

        matriz.get(transicion)
                .entrySet()
                .forEach(plazaTok -> marcado.merge(plazaTok.getKey(),
                        plazaTok.getValue(),
                        Integer::sum));

        return true;
    }

    public Map<String, Integer> getMarcado() {
        return marcado;
    }

    public void setMarcado(Map<String, Integer> estado) {
        marcado = new HashMap<String, Integer>(estado);
    }

    public Set<String> getTrancisiones() {
        return matriz.keySet();
    }

    public Set<String> getTransicionesSensibilizadas() {
        return matriz.keySet().stream()
                .filter(t -> isSensibilizada(t))
                .collect(Collectors.toSet());
    }

    private boolean isSensibilizada(String transicion) {
        return matriz.get(transicion).entrySet().stream()
                .allMatch(
                        marcadoNecesario -> marcado.get(marcadoNecesario.getKey())
                                + marcadoNecesario.getValue() >= 0);
    }
}
