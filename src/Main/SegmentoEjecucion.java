package Main;

import java.util.Iterator;

public class SegmentoEjecucion implements Iterable<String> {

    private final String[] transiciones;

    public SegmentoEjecucion(String... transiciones) {
        this.transiciones = transiciones;
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
}
