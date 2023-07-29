package Main;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Disparador implements Runnable {

    private final Monitor monitor;
    private final Iterator<String> tIterator;

    public Disparador(Monitor monitor, Iterable<String> transiciones) {
        this.monitor = monitor;
        this.tIterator = transiciones.iterator();
    }

    @Override
    public void run() {
        while (true) {
            try {
                String transicion = tIterator.next();
                monitor.dispararTransicion(transicion);
            } catch (InterruptedException e) {
                break;
            } catch (NoSuchElementException e) {
                break;
            }
        }

    }
}
