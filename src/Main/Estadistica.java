package Main;

import java.util.HashMap;
import java.util.Map;

public class Estadistica {

    private final Rdp rdp;
    private final Map<String, Integer> tCount = new HashMap<String, Integer>();

    public Estadistica(Rdp rdp) {
        this.rdp = rdp;
        // Inicializar cuenta con todas transiciones en 0
        rdp.getTrancisiones().stream().forEach(t -> tCount.put(t, 0));
    }

    public void registrarDisparo(String transicion) {
        tCount.merge(transicion, 1, Integer::sum);
    }

}
