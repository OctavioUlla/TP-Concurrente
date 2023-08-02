package Main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;
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

    public static List<LinkedHashSet<String>> getTInvariantesOrdenados(Rdp rdp) {
        // Comenzar con segmentos iguales o los tInvariantes
        List<Set<String>> tInvariantes = getTInvariantes(rdp);
        Iterator<Set<String>> iteradorPlazasAccionTInvariantes = getPlazasAccionTInvariantes(rdp).iterator();

        // Ordenar transiciones en tInvariante
        return tInvariantes.stream()
                .map(tInvariante -> ordenarTInvariante(tInvariante.iterator(),
                        tInvariante,
                        iteradorPlazasAccionTInvariantes.next(),
                        rdp.getMatrizMap()))
                .collect(Collectors.toList());
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

        return getTInvariantes(rdp).stream()
                // Por cada tInvariante encontrar plazas involucradas
                .map(tInvariante -> tInvariante.stream()
                        // Obtener plazas que interactuan con cada transicion (t)
                        .flatMap(t -> matrizMap.get(t).entrySet().stream()
                                // Si valor es distinto de cero significa que plaza esta conectada a transicion
                                .filter(p -> p.getValue() != 0)
                                .map(p -> p.getKey()))
                        .collect(Collectors.toSet()))
                .collect(Collectors.toList());
    }

    public static Set<String> getPlazasAccion(Rdp rdp) {
        List<Set<String>> pInvariantes = getPInvariantes(rdp);
        Set<String> plazasRecursosYIdle = new HashSet<String>();

        // Plazas que se repiten en los pInvariantes son plazas de accion
        return pInvariantes.stream()
                .flatMap(pInvariante -> pInvariante.stream()
                        .filter(p -> !plazasRecursosYIdle.add(p)))
                .collect(Collectors.toSet());
    }

    public static List<Set<String>> getPlazasAccionTInvariantes(Rdp rdp) {
        List<Set<String>> plazasTInvariantes = getPlazasTInvariantes(rdp);
        Set<String> plazasAccion = getPlazasAccion(rdp);

        // Filtrar cada set de plazas de tInvariante
        return plazasTInvariantes.stream()
                .map(plazasTInvariante -> plazasTInvariante.stream()
                        .filter(p -> plazasAccion.contains(p))
                        .collect(Collectors.toSet()))
                .collect(Collectors.toList());
    }

    public static List<SegmentoEjecucion> getSegmentos(Rdp rdp) {
        SortedMap<String, SortedMap<String, Integer>> matriz = rdp.getMatrizMap();
        Set<String> plazasAccion = getPlazasAccion(rdp);

        Set<String> transicionesFork = getForks(matriz, plazasAccion);

        Set<String> transicionesjoin = getJoins(matriz, plazasAccion);

        // Comenzar con segmentos iguales a los tInvariantes
        List<LinkedHashSet<String>> tSegmentos = getTInvariantesOrdenados(rdp);

        for (String fork : transicionesFork) {
            for (LinkedHashSet<String> tSegmento : tSegmentos) {
                // Si segmento contiene fork
                if (tSegmento.contains(fork)) {

                }
            }
        }

        return null;
    }

    private static Set<String> getForks(
            SortedMap<String, SortedMap<String, Integer>> matriz,
            Set<String> plazasAccion) {
        Set<String> transicionesFork = new HashSet<String>();

        // Buscar forks (si plaza tiene mas de un valor negativo en la matriz)
        for (String plazaAccion : plazasAccion) {
            int cantTrancicionesNegativas = 0;
            String transicionPositiva = null;

            for (Entry<String, SortedMap<String, Integer>> entry : matriz.entrySet()) {
                Integer valorMatriz = entry.getValue().get(plazaAccion);

                if (valorMatriz < 0) {
                    cantTrancicionesNegativas++;
                } else if (valorMatriz > 0) {
                    transicionPositiva = entry.getKey();
                }
            }

            if (cantTrancicionesNegativas > 1) {
                transicionesFork.add(transicionPositiva);
            }
        }

        return transicionesFork;
    }

    private static Set<String> getJoins(
            SortedMap<String, SortedMap<String, Integer>> matriz,
            Set<String> plazasAccion) {
        Set<String> transicionesJoin = new HashSet<String>();

        // Buscar forks (si plaza tiene mas de un valor positivo en la matriz)
        for (String plazaAccion : plazasAccion) {
            int cantTrancicionesPositivas = 0;
            String transicionNegativa = null;

            for (Entry<String, SortedMap<String, Integer>> entry : matriz.entrySet()) {
                Integer valorMatriz = entry.getValue().get(plazaAccion);

                if (valorMatriz > 0) {
                    cantTrancicionesPositivas++;
                } else if (valorMatriz < 0) {
                    transicionNegativa = entry.getKey();
                }
            }

            if (cantTrancicionesPositivas > 1) {
                transicionesJoin.add(transicionNegativa);
            }
        }

        return transicionesJoin;
    }

    private static LinkedHashSet<String> ordenarTInvariante(
            Iterator<String> iteradorTInvariante,
            Set<String> tInvariante,
            Set<String> plazasAccionTInvariante,
            SortedMap<String, SortedMap<String, Integer>> matriz) {
        LinkedHashSet<String> tInvarianteOrdenado = new LinkedHashSet<String>();

        String t = iteradorTInvariante.next();
        tInvarianteOrdenado.add(t);

        while (t != null) {
            // Encontrar plazas de accion de salida de la transicion que pertenezcan a la
            // invariante
            Set<String> plazasSalida = matriz.get(t).entrySet().stream()
                    .filter(p -> plazasAccionTInvariante.contains(p.getKey()))
                    .filter(p -> p.getValue() > 0)
                    .map(p -> p.getKey())
                    .collect(Collectors.toSet());

            // Econtrar transiciones de salida de plaza y filtrar la que se encuentra en el
            // tInvariante
            t = plazasSalida.stream().flatMap(
                    p -> matriz.entrySet().stream()
                            .filter(e -> e.getValue().get(p) < 0)
                            .map(e -> e.getKey()))
                    .filter(tProx -> tInvariante.contains(tProx))
                    .filter(tProx -> !tInvarianteOrdenado.contains(tProx))
                    .findFirst()
                    .orElse(null);

            if (t != null) {
                tInvarianteOrdenado.add(t);
            }
        }

        // Significa que la transicion inicial no era la correcta, intentar ordenar
        // nuevamente con la siguiente transicion
        if (tInvarianteOrdenado.size() != tInvariante.size()) {
            return ordenarTInvariante(iteradorTInvariante, tInvariante, plazasAccionTInvariante, matriz);
        }

        return tInvarianteOrdenado;
    }
}