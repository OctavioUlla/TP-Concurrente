package Main;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SegmentoEjecucion implements Iterable<String> {

    private final int hilos;
    private final String[] transiciones;

    public SegmentoEjecucion(int hilos, String... transiciones) {
        this.hilos = hilos;
        this.transiciones = transiciones;
    }

    public int getHilos() {
        return hilos;
    }

    @Override
    public Iterator<String> iterator() {
        // Return iterador infinito ciclico
        return new Iterator<String>() {

            int i = 0;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public String next() {
                return transiciones[i++ % transiciones.length];
            }
        };
    }

    public static List<SegmentoEjecucion> getSegmentosEjecucion(Rdp rdp) {
        List<Set<String>> tSegmentos = AnalizadorRdp
                .getTransicionesSegmentos(rdp);

        HashSet<Map<String, Integer>> marcados = new HashSet<Map<String, Integer>>();
        AnalizadorRdp.getMarcados(rdp,
                AnalizadorRdp.getPlazasAccion(rdp),
                marcados);

        return null;
    }
}
