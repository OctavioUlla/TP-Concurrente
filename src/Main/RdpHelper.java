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
        // T count
        int n = rdp.getMatriz().size();
        // P count
        int m = rdp.getMatriz().values().iterator().next().size();

        // Convert Map to matriz
        Matriz matriz = new Matriz(n, m);
        int i = 0;

        Set<Entry<String, Map<String, Integer>>> matrizEntries = rdp.getMatriz().entrySet();
        List<String> trancisiones = matrizEntries.stream()
                .map(x -> x.getKey())
                .collect(Collectors.toList());
        List<Map<String, Integer>> rows = matrizEntries.stream()
                .map(x -> x.getValue())
                .collect(Collectors.toList());

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
        int tInvarianteCount = n - matriz.getRango();

        // Transformar matriz resultante a lista de invariantes
        List<Set<String>> tInvariantes = new ArrayList<Set<String>>();
        for (i = n - tInvarianteCount; i < n; i++) {
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
}
