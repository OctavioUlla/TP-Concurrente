package Main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Estadistica {
    private final List<Set<String>> tInvariantes;
    private final List<String> tInvarianteIncompleto = new ArrayList<String>();
    private final Map<Set<String>, Integer> tInvariantesCount = new HashMap<Set<String>, Integer>();
    private final Map<Set<String>, Integer> pInvariantesSuma;
    private final Object notificador = new Object();
    private BufferedWriter writer;
    private long startTime;
    private long stopTime;
    private boolean verificanPInvariantes = true;

    public Estadistica(Rdp rdp) {
        tInvariantes = AnalizadorRdp.getTInvariantes(rdp);
        // Inicializas cuenta de tInvariantes en 0
        tInvariantes.forEach(tInvariante -> tInvariantesCount.put(tInvariante, 0));

        // Obtiene la suma de tokens en cada pInvariante
        List<Set<String>> pInvariantes = AnalizadorRdp.getPInvariantes(rdp);
        pInvariantesSuma = pInvariantes.stream()
                .collect(Collectors.toMap(pInvariante -> pInvariante,
                        pInvariante -> rdp.getMarcado().entrySet().stream()
                                .filter(p -> pInvariante.contains(p.getKey()))
                                .map(p -> p.getValue())
                                .reduce(0, Integer::sum)));

        try {
            writer = new BufferedWriter(new FileWriter("log.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registrarDisparo(String transicion, Map<String, Integer> newMarcado) {
        System.out.printf("%s Disparada\n", transicion);

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

        verificarPInvariantes(newMarcado);
    }

    public void wait1000TInvariantes() throws InterruptedException {
        synchronized (notificador) {
            notificador.wait();
        }
    }

    public List<String> getTInvariantesIncompletos() {
        return tInvarianteIncompleto;
    }

    public Map<Set<String>, Integer> getTInvariantesCount() {
        return tInvariantesCount;
    }

    public void startTimer() {
        startTime = System.currentTimeMillis();
    }

    public void stop() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stopTime = System.currentTimeMillis();
    }

    public void printEstadisticas() {
        tInvariantesCount.forEach((tInvariante, count) -> {
            System.out.printf("Invariante %s: %d", tInvariante, count);
            System.out.println();
        });

        System.out.printf("PInvariantes verifican: %s\n", verificanPInvariantes);
        System.out.printf("Tiempo de ejecución: %dms\n", stopTime - startTime);
    }

    private boolean llegoTInvarianteLimite() {
        int count = tInvariantesCount.values().stream()
                .mapToInt(x -> x.intValue())
                .sum();

        return count == 1000;
    }

    private void verificarPInvariantes(Map<String, Integer> newMarcado) {
        for (Entry<Set<String>, Integer> pInvarianteSuma : pInvariantesSuma.entrySet()) {
            int suma = newMarcado.entrySet().stream()
                    .filter(p -> pInvarianteSuma.getKey().contains(p.getKey()))
                    .map(p -> p.getValue())
                    .reduce(0, Integer::sum);

            if (pInvarianteSuma.getValue() != suma) {
                verificanPInvariantes = false;
            }
        }
    }
}
