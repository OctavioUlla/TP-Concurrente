package Main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Estadistica {
    private final List<Set<String>> tInvariantes;
    private final List<String> tInvarianteIncompleto = new ArrayList<String>();
    private final Map<Set<String>, Integer> tInvariantesCount = new HashMap<Set<String>, Integer>();

    private BufferedWriter writer;
    private final Object notificador = new Object();

    public Estadistica(Rdp rdp) {
        tInvariantes = AnalizadorRdp.getTInvariantes(rdp);
        // Inicializas cuenta de tInvariantes en 0
        tInvariantes.forEach(tInvariante -> tInvariantesCount.put(tInvariante, 0));
    }

    public void registrarDisparo(String transicion) {
        // Log transicion
        try {
            writer.write(transicion + "-");
        } catch (IOException e) {
            e.printStackTrace();
        }

        tInvarianteIncompleto.add(transicion);

        // Verificar si se completo un invariante
        for (Set<String> tInvariante : tInvariantes) {
            // Si un invariante esta en el set es pq se completo uno
            if (tInvarianteIncompleto.containsAll(tInvariante)) {
                tInvariante.forEach(t -> tInvarianteIncompleto.remove(tInvarianteIncompleto.indexOf(t)));
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

    public List<String> getTInvariantesIncompletos() {
        return tInvarianteIncompleto;
    }

    public void start() {
        try {
            writer = new BufferedWriter(new FileWriter("log.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printEstadisticas() {
        tInvariantesCount.forEach((tInvariante, count) -> {
            System.out.printf("Invariante %s: %d", tInvariante, count);
            System.out.println();
        });

    }

    private boolean llegoTInvarianteLimite() {
        int count = tInvariantesCount.values().stream()
                .mapToInt(x -> x.intValue())
                .sum();

        return count == 1000;
    }
}
