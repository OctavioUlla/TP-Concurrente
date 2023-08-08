package Main;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;

public class Rdp {
    private final SortedMap<String, SortedMap<String, Integer>> matrizMap;
    private final Map<String, Temporizacion> transicionesTemporizadas;
    private Map<String, Integer> marcado;

    private Estadistica estadistica;

    public Rdp(
            SortedMap<String, SortedMap<String, Integer>> matrizIncidencia,
            Map<String, Integer> marcadoInicial,
            Map<String, Temporizacion> transicionesTemporizadas) {
        this.matrizMap = Collections
                .unmodifiableSortedMap(matrizIncidencia);
        this.transicionesTemporizadas = Collections
                .unmodifiableMap(transicionesTemporizadas);
        this.marcado = marcadoInicial;
    }

    public Estadistica crearEstadisticas() {
        estadistica = new Estadistica(this);
        return estadistica;
    }

    public Estadistica getEstadistica() {
        return estadistica;
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

        updateTimeStamps();

        if (estadistica != null) {
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

    public Set<String> getTrancisiones() {
        return matrizMap.keySet();
    }

    public Set<String> getPlazas() {
        return matrizMap.entrySet().iterator().next().getValue().keySet();
    }

    public Set<String> getTransicionesMarcadosNecesarios() {
        return getTrancisiones()
                .stream()
                .filter(t -> hasMarcadoNecesario(t))
                .collect(Collectors.toSet());
    }

    public boolean isTemporal(String transicion) {
        return transicionesTemporizadas.containsKey(transicion);
    }

    public boolean hasMarcadoNecesario(String transicion) {
        return matrizMap.get(transicion).entrySet().stream()
                .allMatch(
                        marcadoNecesario -> marcado.get(marcadoNecesario.getKey())
                                + marcadoNecesario.getValue() >= 0);
    }

    public long getEsperaTemporal(String transicion) {
        return transicionesTemporizadas.get(transicion).getEspera();
    }

    private void updateTimeStamps() {
        long timeStampActual = System.currentTimeMillis();

        // Actualizar time stamp de todas las transiciones con marcado necesarios y
        // temporales
        transicionesTemporizadas.entrySet().stream()
                .filter(e -> hasMarcadoNecesario(e.getKey()))
                .forEach(e -> e.getValue().setTimeStamp(timeStampActual));
    }

    private boolean isSensibilizada(String transicion) {
        if (isTemporal(transicion) && !isEnVentana(transicion)) {
            return false;
        }

        return hasMarcadoNecesario(transicion);
    }

    private boolean isEnVentana(String transicion) {
        return transicionesTemporizadas.get(transicion).isEnVentana();
    }
}
