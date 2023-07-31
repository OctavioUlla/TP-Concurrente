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
        Set<Entry<String, Map<String, Integer>>> matrizEntries = rdp.getMatriz().entrySet();
        List<String> trancisiones = matrizEntries.stream()
                .map(x -> x.getKey())
                .collect(Collectors.toList());
        List<Map<String, Integer>> rows = matrizEntries.stream()
                .map(x -> x.getValue())
                .collect(Collectors.toList());

        // T count
        int n = matrizEntries.size();
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
                    .filter(x -> tInvariante.contains(x.getKey()))
                    .flatMap(x -> x.getValue().entrySet().stream())
                    .filter(x -> x.getValue() != 0)
                    .map(x -> x.getKey())
                    .collect(Collectors.toSet());

            plazasTInvariantes.add(plazasTInvariante);
        });

        System.out.println(plazasTInvariantes);
        return plazasTInvariantes;
    }
}
