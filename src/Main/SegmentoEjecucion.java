package Main;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;

public class SegmentoEjecucion implements Iterable<String> {

    private final int hilos;
    private final String[] transiciones;

    public SegmentoEjecucion(int hilos, Set<String> transiciones) {
        this.hilos = hilos;
        this.transiciones = new String[transiciones.size()];
        transiciones.toArray(this.transiciones);
    }

    public int getHilos() {
        return hilos;
    }

    @Override
    public Iterator<String> iterator() {
        // Return iterador infinito ciclico
        return new Iterator<String>() {

            int i = 0;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public String next() {
                return transiciones[i++ % transiciones.length];
            }
        };
    }

    @Override
    public String toString() {
        return "Transiciones: " + Arrays.toString(transiciones) + " y Hilos: " + hilos;
    }

    public static List<SegmentoEjecucion> getSegmentosEjecucion(Rdp rdp) {
        SortedMap<String, SortedMap<String, Integer>> matriz = rdp.getMatrizMap();
        Set<String> plazasAccion = AnalizadorRdp.getPlazasAccion(rdp);

        Map<String, String> forks = getForks(matriz, plazasAccion);

        Map<String, String> joins = getJoins(matriz, plazasAccion);

        // Comenzar con segmentos iguales a los tInvariantes
        List<Set<String>> tSegmentos = AnalizadorRdp.getTInvariantes(rdp);

        crearSegmentosFork(forks, tSegmentos);

        crearSegmentosJoins(joins, tSegmentos);

        List<Set<String>> plazasSegmentos = AnalizadorRdp.getPlazasAccionTInvariantes(rdp, tSegmentos);

        // Eliminar plazas correspodientes
        for (int i = plazasSegmentos.size() - 1; i >= 0; i--) {
            // Borrar plazas de los segmentos anteriores, para evitar repeticion
            for (int j = i - 1; j >= 0; j--) {
                plazasSegmentos.get(j).removeAll(plazasSegmentos.get(i));
                plazasSegmentos.get(i).removeAll(plazasSegmentos.get(j));
            }
        }

        List<Integer> hilosSegmentos = getHilosSegmentos(rdp, plazasAccion, plazasSegmentos);

        return tSegmentos.stream()
                .map(tSegmento -> new SegmentoEjecucion(hilosSegmentos.get(tSegmentos.indexOf(tSegmento)), tSegmento))
                .collect(Collectors.toList());
    }

    private static List<Integer> getHilosSegmentos(
            Rdp rdp,
            Set<String> plazasAccion,
            List<Set<String>> plazasSegmentos) {

        HashSet<Map<String, Integer>> marcados = new HashSet<Map<String, Integer>>();
        AnalizadorRdp.getMarcados(rdp, plazasAccion, marcados);

        // Calcular suma maxima de marcados en las plazas de cada segmento
        return plazasSegmentos.stream()
                .map(plazasSegmento -> marcados.stream()
                        .map(marcado -> marcado.entrySet().stream()
                                .filter(p -> plazasSegmento.contains(p.getKey()))
                                .map(p -> p.getValue())
                                .mapToInt(Integer::intValue).sum())
                        .mapToInt(Integer::intValue)
                        .max()
                        .orElse(0))
                .collect(Collectors.toList());
    }

    private static void crearSegmentosJoins(Map<String, String> joins, List<Set<String>> tSegmentos) {
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
    }

    private static void crearSegmentosFork(Map<String, String> forks, List<Set<String>> tSegmentos) {
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
}
