package Main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class RdpHelper {

    public static List<Set<String>> getTInvariantes(Rdp rdp) {
        // Convert Map to matriz
        List<String> trancisiones = rdp.getTrancisiones()
                .stream()
                .collect(Collectors.toList());
        List<Map<String, Integer>> rows = rdp.getMatriz().entrySet().stream()
                .map(x -> x.getValue())
                .collect(Collectors.toList());

        // T count
        int n = rows.size();
        // P count
        int m = rows.get(0).size();

        Matriz matriz = new Matriz(n, m);
        int i = 0;

        for (Map<String, Integer> row : rows) {
            int j = 0;
            for (Integer value : row.values()) {
                matriz.data[i][j] = value;
                j++;
            }
            i++;
        }

        // Resolver matriz indicencia A^T . X = 0 para obtener invariantes
        Matriz matrixTInvariantes = matriz.resolver();

        // Transformar matriz resultante a lista de invariantes
        List<Set<String>> tInvariantes = new ArrayList<Set<String>>();
        for (i = matriz.getRango(); i < n; i++) {
            Set<String> tInvariante = new HashSet<String>();
            for (int j = 0; j < n; j++) {
                if (matrixTInvariantes.data[i][j] != 0) {
                    String t = trancisiones.get(j);
                    tInvariante.add(t);
                }
            }
            tInvariantes.add(tInvariante);
        }

        return tInvariantes;
    }

    public static List<Set<String>> getPlazasTInvariantes(Rdp rdp) {
        List<Set<String>> plazasTInvariantes = new ArrayList<Set<String>>();
        Map<String, Map<String, Integer>> matrizMap = rdp.getMatriz();

        getTInvariantes(rdp).forEach(tInvariante -> {

            Set<String> plazasTInvariante = matrizMap.entrySet().stream()
                    .filter(x -> tInvariante.contains(x.getKey())) // Filtrar transiciones en T Invariante
                    .flatMap(x -> x.getValue().entrySet().stream()) // Select all plazas
                    .filter(x -> x.getValue() != 0) // Filtrar plazas que afectan transicion
                    .map(x -> x.getKey()) // Select plazas
                    .collect(Collectors.toSet());

            plazasTInvariantes.add(plazasTInvariante);
        });

        return plazasTInvariantes;
    }

    public static List<Set<String>> getPlazasAccionTInvariantes(Rdp rdp) {
        List<Set<String>> plazasTInvariantes = getPlazasTInvariantes(rdp);
    }
}
