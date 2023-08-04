package Main;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;

public class Rdp {
    private final SortedMap<String, SortedMap<String, Integer>> matrizMap;
    private Map<String, Integer> marcado;

    private final Estadistica estadistica;
    private boolean estadisticaActivada;

    public Rdp(SortedMap<String, SortedMap<String, Integer>> matrizIncidencia, Map<String, Integer> marcadoInicial) {
        this.matrizMap = Collections.unmodifiableSortedMap(matrizIncidencia);
        this.marcado = marcadoInicial;
        this.estadistica = new Estadistica(this);
    }

    public boolean disparar(String transicion) {

        if (!isSensibilizada(transicion)) {
            return false;
        }

        matrizMap.get(transicion)
                .entrySet()
                .forEach(plazaTok -> marcado.merge(plazaTok.getKey(),
                        plazaTok.getValue(),
                        Integer::sum));

        if (estadisticaActivada) {
            estadistica.registrarDisparo(transicion);
        }

        return true;
    }

    public SortedMap<String, SortedMap<String, Integer>> getMatrizMap() {
        return matrizMap;
    }

    public Matriz getMatriz() {
        Integer[][] matriz = matrizMap.entrySet().stream()
                .map(x -> x.getValue().values().stream().toArray(n -> new Integer[n]))
                .toArray(Integer[][]::new);

        return new Matriz(matriz);
    }

    public Map<String, Integer> getMarcado() {
        return marcado;
    }

    public void setMarcado(Map<String, Integer> estado) {
        marcado = new HashMap<String, Integer>(estado);
    }

    public Set<String> getTrancisiones() {
        return matrizMap.keySet();
    }

    public Set<String> getPlazas() {
        return matrizMap.entrySet().iterator().next().getValue().keySet();
    }

    public Set<String> getTransicionesSensibilizadas() {
        return getTrancisiones()
                .stream()
                .filter(t -> isSensibilizada(t))
                .collect(Collectors.toSet());
    }

    public boolean isSensibilizada(String transicion) {
        return matrizMap.get(transicion).entrySet().stream()
                .allMatch(
                        marcadoNecesario -> marcado.get(marcadoNecesario.getKey())
                                + marcadoNecesario.getValue() >= 0);
    }

    public Estadistica getEstadistica() {
        return estadistica;
    }

    public void activarEstadisticas(boolean activarEstadistica) {
        this.estadisticaActivada = activarEstadistica;
    }
}
