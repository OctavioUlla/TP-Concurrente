package Main;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Disparador implements Runnable {

    private final Monitor monitor;
    private final Iterator<String> tIterator;

    public Disparador(Monitor monitor, SegmentoEjecucion segmento) {
        this.monitor = monitor;
        this.tIterator = segmento.iterator();
    }

    @Override
    public void run() {
        while (true) {
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
