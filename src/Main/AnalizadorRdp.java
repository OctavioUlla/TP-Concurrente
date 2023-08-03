package Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class AnalizadorRdp {

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

        Iterator<Set<String>> iteradorPlazasAccionTInvariantes = getPlazasAccionTInvariantes(rdp, tInvariantes)
                .iterator();

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

    public static Set<String> getPlazasAccion(Rdp rdp) {
        List<Set<String>> pInvariantes = getPInvariantes(rdp);
        Set<String> plazasRecursosYIdle = new HashSet<String>();

        // Plazas que se repiten en los pInvariantes son plazas de accion
        return pInvariantes.stream()
                .flatMap(pInvariante -> pInvariante.stream()
                        .filter(p -> !plazasRecursosYIdle.add(p)))
                .collect(Collectors.toSet());
    }

    // Capaz se puede eliminar
    public static List<Set<String>> getPlazasAccionTInvariantes(Rdp rdp, List<Set<String>> tInvariantes) {
        SortedMap<String, SortedMap<String, Integer>> matrizMap = rdp.getMatrizMap();
        Set<String> plazasAccion = getPlazasAccion(rdp);

        return tInvariantes.stream()
                // Por cada tInvariante encontrar plazas involucradas
                .map(tInvariante -> tInvariante.stream()
                        // Obtener plazas que interactuan con cada transicion (t)
                        .flatMap(t -> matrizMap.get(t).entrySet().stream()
                                // Si valor es distinto de cero significa que plaza esta conectada a transicion
                                .filter(p -> p.getValue() != 0)
                                .filter(p -> plazasAccion.contains(p.getKey()))
                                .map(p -> p.getKey()))
                        .collect(Collectors.toSet()))
                .collect(Collectors.toList());
    }

    public static List<Set<String>> getTransicionesSegmentos(Rdp rdp) {
        SortedMap<String, SortedMap<String, Integer>> matriz = rdp.getMatrizMap();
        Set<String> plazasAccion = getPlazasAccion(rdp);

        Map<String, String> forks = getForks(matriz, plazasAccion);

        Map<String, String> joins = getJoins(matriz, plazasAccion);

        // Comenzar con segmentos iguales a los tInvariantes
        List<Set<String>> tSegmentos = getTInvariantes(rdp);

        // Crear segmento nuevo por cada fork
        for (Entry<String, String> fork : forks.entrySet()) {
            LinkedHashSet<String> nuevoSegmento = null;

            for (Set<String> tSegmento : tSegmentos) {
                // Si segmento contiene fork
                if (tSegmento.contains(fork.getValue())) {
                    int indice = 0;
                    // Encontrar indice del fork en el segmento
                    for (String t : tSegmento) {
                        indice++;
                        if (t == fork.getValue()) {
                            break;
                        }
                    }

                    // Encontar transiciones que pertenecen al segmento nuevo
                    nuevoSegmento = tSegmento.stream()
                            .limit(indice)
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                    // Eliminar transiciones que pertencen al nuevo segmento
                    tSegmento.removeAll(nuevoSegmento);
                }
            }

            tSegmentos.add(nuevoSegmento);
        }

        // Crear segmento nuevo por cada join
        for (Entry<String, String> join : joins.entrySet()) {
            LinkedHashSet<String> nuevoSegmento = null;

            for (Set<String> tSegmento : tSegmentos) {
                // Si segmento contiene fork
                if (tSegmento.contains(join.getValue())) {
                    int indice = 0;
                    // Encontrar indice del join en el segmento
                    for (String t : tSegmento) {
                        if (t == join.getValue()) {
                            break;
                        }
                        indice++;
                    }

                    // Encontar transiciones que pertenecen al segmento nuevo
                    nuevoSegmento = tSegmento.stream()
                            .skip(indice)
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                    // Eliminar transiciones que pertencen al nuevo segmento
                    tSegmento.removeAll(nuevoSegmento);
                }
            }

            tSegmentos.add(nuevoSegmento);
        }

        List<Set<String>> plazasSegmentos = AnalizadorRdp.getPlazasAccionTInvariantes(rdp, tSegmentos);

        // Eliminar plazas correspodientes
        for (int i = plazasSegmentos.size() - 1; i >= 0; i--) {
            // Borrar plazas de los segmentos anteriores, para evitar repeticion
            for (int j = i - 1; j >= 0; j--) {
                plazasSegmentos.get(j).removeAll(plazasSegmentos.get(i));
            }
        }

        return tSegmentos;
    }

    public static void getMarcados(Rdp rdp, Set<String> plazasAccion, HashSet<Map<String, Integer>> marcados) {
        // Deep copy
        Map<String, Integer> marcado = rdp.getMarcado().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        Map<String, Integer> marcadoProcesos = marcado.entrySet().stream()
                .filter(p -> plazasAccion.contains(p.getKey()))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        if (!marcados.add(marcadoProcesos)) {
            // Si se repite marcado volver
            return;
        }

        rdp.getTransicionesSensibilizadas()
                .forEach(t -> {
                    rdp.disparar(t);
                    getMarcados(rdp, plazasAccion, marcados);
                    rdp.setMarcado(marcado);
                });
    }

    public static double getPromedioMarcados(HashSet<Map<String, Integer>> marcados) {
        return marcados.stream()
                .map(toks -> toks.values().stream().mapToInt(Integer::intValue).sum())
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
    }

    public static int getMaxHilosActivos(HashSet<Map<String, Integer>> marcados) {
        return marcados.stream()
                .map(toks -> toks.values().stream().mapToInt(Integer::intValue).sum())
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }

    private static Map<String, String> getForks(
            SortedMap<String, SortedMap<String, Integer>> matriz,
            Set<String> plazasAccion) {
        Map<String, String> forks = new HashMap<String, String>();

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
                forks.put(plazaAccion, transicionPositiva);
            }
        }

        return forks;
    }

    private static Map<String, String> getJoins(
            SortedMap<String, SortedMap<String, Integer>> matriz,
            Set<String> plazasAccion) {
        Map<String, String> joins = new HashMap<String, String>();

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
                joins.put(plazaAccion, transicionNegativa);
            }
        }

        return joins;
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