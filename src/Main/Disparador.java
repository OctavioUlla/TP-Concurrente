package Main;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Disparador implements Runnable {
    private static final int isTInvarianteLimite = 1000;
    private final Monitor monitor;
    private final Estadistica estadistica;
    private final Iterator<String> tIterator;

    public Disparador(Monitor monitor, SegmentoEjecucion segmento) {
        this.monitor = monitor;
        this.estadistica = monitor.getEstadistica();
        this.tIterator = segmento.iterator();
    }

    @Override
    public void run() {
        while (estadistica.isTInvarianteCount(isTInvarianteLimite)) {
            try {
                monitor.dispararTransicion(tIterator.next());
            } catch (InterruptedException e) {
                break;
            } catch (NoSuchElementException e) {
                break;
            }
        }

    }
}
