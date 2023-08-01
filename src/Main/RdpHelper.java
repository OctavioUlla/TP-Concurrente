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
        List<Set<String>> plazasTInvariantes = new ArrayList<Set<String>>();
        SortedMap<String, SortedMap<String, Integer>> matrizMap = rdp.getMatrizMap();

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