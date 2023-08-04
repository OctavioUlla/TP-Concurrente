package Main;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Estadistica {

    private final Logger logger = Logger.getLogger("Estadistica Logger");
    private final List<Set<String>> tInvariantes;
    private final Set<String> tInvarianteIncompleto = new HashSet<String>();
    private final Map<Set<String>, Integer> tInvariantesCount = new HashMap<Set<String>, Integer>();
    private final Object notificador = new Object();

    public Estadistica(Rdp rdp) {
        try {
            FileHandler fileHandler = new FileHandler("log.txt");
            fileHandler.setFormatter(new TransicionFormatter());
            logger.setLevel(Level.INFO);
            logger.addHandler(fileHandler);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }

        tInvariantes = AnalizadorRdp.getTInvariantes(rdp);
        // Inicializas cuenta de tInvariantes en 0
        tInvariantes.forEach(tInvariante -> tInvariantesCount.put(tInvariante, 0));
    }

    public void registrarDisparo(String transicion) {
        logger.info(transicion);

        tInvarianteIncompleto.add(transicion);

        // Verificar si se completo un invariante
        for (Set<String> tInvariante : tInvariantes) {
            // Si un invariante esta en el set es pq se completo uno
            if (tInvarianteIncompleto.containsAll(tInvariante)) {
                tInvarianteIncompleto.removeAll(tInvariante);
                // Aumentar cuenta tInvariante
                tInvariantesCount.merge(tInvariante, 1, Integer::sum);

                if (llegoTInvarianteLimite()) {
                    synchronized (notificador) {
                        notificador.notify();
                    }
                }
            }
        }
    }

    public void wait1000TInvariantes() throws InterruptedException {
        synchronized (notificador) {
            notificador.wait();
        }
    }

    private boolean llegoTInvarianteLimite() {
        int count = tInvariantesCount.values().stream()
                .mapToInt(x -> x.intValue())
                .sum();

        return count >= 1000;
    }
}
