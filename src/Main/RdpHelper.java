package Main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;

public class RdpHelper {

    public static List<Set<String>> getTInvariantes(Rdp rdp) {
        // Resolver matriz indicencia W^T . X = 0 para obtener t invariantes
        Matriz matrizTInvariantes = rdp.getMatriz().transpose().kernel();

        // Transformar matriz resultante a lista de invariantes
        List<String> transiciones = rdp.getTrancisiones()
                .stream()
                .collect(Collectors.toList());

        List<Set<String>> tInvariantes = new ArrayList<Set<String>>();

        for (int i = 0; i < matrizTInvariantes.n; i++) {
            Set<String> tInvariante = new HashSet<String>();
            for (int j = 0; j < matrizTInvariantes.m; j++) {
                if (matrizTInvariantes.get(j, i) != 0) {
                    tInvariante.add(transiciones.get(j));
                }
            }
            tInvariantes.add(tInvariante);
        }

        return tInvariantes;
    }

    public static List<Set<String>> getPInvariantes(Rdp rdp) {
        // Resolver matriz indicencia W . X = 0 para obtener t invariantes
        Matriz matrizPInvariantes = rdp.getMatriz().kernel();

        // Transformar matriz resultante a lista de invariantes
        List<String> plazas = rdp.getPlazas()
                .stream()
                .collect(Collectors.toList());

        List<Set<String>> pInvariantes = new ArrayList<Set<String>>();

        for (int i = 0; i < matrizPInvariantes.n; i++) {
            Set<String> pInvariante = new HashSet<String>();
            for (int j = 0; j < matrizPInvariantes.m; j++) {
                if (matrizPInvariantes.get(j, i) != 0) {
                    pInvariante.add(plazas.get(j));
                }
            }
            pInvariantes.add(pInvariante);
        }

        return pInvariantes;
    }

    public static List<Set<String>> getPlazasTInvariantes(Rdp rdp) {
        SortedMap<String, SortedMap<String, Integer>> matrizMap = rdp.getMatrizMap();

        return getTInvariantes(rdp).stream().map(tInvariante -> tInvariante.stream()
                .flatMap(t -> matrizMap.get(t).entrySet().stream()
                        .filter(p -> p.getValue() != 0)
                        .map(p -> p.getKey()))
                .collect(Collectors.toSet())).collect(Collectors.toList());
    }
    /*
     * public static List<Set<String>> getPlazasAccionTInvariantes(Rdp rdp) {
     * List<Set<String>> plazasTInvariantes = getPlazasTInvariantes(rdp);
     * Map<String, Map<String, Integer>> matrizMap = rdp.getMatrizMap();
     * 
     * // Eliminar plazas recursos, idle y restricciones
     * plazasTInvariantes.forEach(plazas -> {
     * 
     * });
     * }
     */
}