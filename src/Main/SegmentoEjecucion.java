package Main;

import java.util.Iterator;

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
}
