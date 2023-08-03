package Main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Estadistica {

    private final List<Set<String>> tInvariantes;
    private final Set<String> tInvarianteIncompleto = new HashSet<String>();
    private final Map<Set<String>, Integer> tInvariantesCount = new HashMap<Set<String>, Integer>();

    public Estadistica(Rdp rdp) {
        tInvariantes = AnalizadorRdp.getTInvariantes(rdp);
        // Inicializas cuenta de tInvariantes en 0
        tInvariantes.forEach(tInvariante -> tInvariantesCount.put(tInvariante, 0));
    }

    public void registrarDisparo(String transicion) {
        tInvarianteIncompleto.add(transicion);

        // Verificar si se completo un invariante
        for (Set<String> tInvariante : tInvariantes) {
            // Si un invariante esta en el set es pq se completo uno
            if (tInvarianteIncompleto.containsAll(tInvariante)) {
                tInvarianteIncompleto.removeAll(tInvariante);
                // Aumentar cuenta tInvariante
                tInvariantesCount.merge(tInvariante, 1, Integer::sum);
            }
        }
    }

    public boolean isTInvarianteCount(int limite) {
        int count = tInvariantesCount.values().stream()
                .mapToInt(x -> x.intValue())
                .sum();

        return count >= limite;
    }
}
