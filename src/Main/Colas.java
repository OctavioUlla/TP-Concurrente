package Main;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class Colas {
    private final Map<String, Semaphore> semaforos;

    public Colas(Set<String> transiciones) {
        semaforos = Collections.unmodifiableMap(transiciones.stream()
                .collect(Collectors
                        .toMap(x -> x, x -> new Semaphore(0))));
    }

    public Set<String> getTransicionesEspera() {
        return semaforos.entrySet().stream()
                .filter(x -> x.getValue().hasQueuedThreads())
                .map(x -> x.getKey())
                .collect(Collectors.toSet());
    }

    public void acquire(String transicion) throws InterruptedException {
        semaforos.get(transicion).acquire();
    }

    public void release(String transicion) {
        semaforos.get(transicion).release();
    }
}
