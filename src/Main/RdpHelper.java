package Main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RdpHelper {

    public static List<Set<String>> getTInvariantes(Rdp rdp) {
        Matriz matriz = rdp.getMatriz();

        // Resolver matriz indicencia W^T . X = 0 para obtener t invariantes
        Matriz matrizTInvariantes = matriz.resolver(true);

        // Transformar matriz resultante a lista de invariantes
        List<String> transiciones = rdp.getTrancisiones()
                .stream()
                .collect(Collectors.toList());

        List<Set<String>> tInvariantes = new ArrayList<Set<String>>();

        for (int i = matriz.getRango(); i < matriz.N; i++) {
            Set<String> tInvariante = new HashSet<String>();
            for (int j = 0; j < matriz.N; j++) {
                if (matrizTInvariantes.data[i][j] != 0) {
                    tInvariante.add(transiciones.get(j));
                }
            }
            tInvariantes.add(tInvariante);
        }

        return tInvariantes;
    }

    public static List<Set<String>> getPInvariantes(Rdp rdp) {
        Matriz matriz = rdp.getMatriz().traspuesta();

        // Resolver matriz indicencia W . X = 0 para obtener p invariantes
        Matriz matrizPInvariantes = matriz.resolver(false);

        // Transformar matriz resultante a lista de invariantes
        List<String> plazas = rdp.getPlazas()
                .stream()
                .collect(Collectors.toList());

        List<Set<String>> pInvariantes = new ArrayList<Set<String>>();

        for (int i = matriz.getRango(); i < matriz.N; i++) {
            Set<String> pInvariante = new HashSet<String>();
            for (int j = 0; j < matriz.N; j++) {
                if (matrizPInvariantes.data[i][j] != 0) {
                    pInvariante.add(plazas.get(j));
                }
            }
            pInvariantes.add(pInvariante);
        }

        System.out.println(pInvariantes);
        return pInvariantes;
    }

    public static List<Set<String>> getPlazasTInvariantes(Rdp rdp) {
        List<Set<String>> plazasTInvariantes = new ArrayList<Set<String>>();
        Map<String, Map<String, Integer>> matrizMap = rdp.getMatrizMap();

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
        Map<String, Map<String, Integer>> matrizMap = rdp.getMatrizMap();

        // Eliminar plazas recursos, idle y restricciones
        plazasTInvariantes.forEach(plazas -> {

        });
    }
}
