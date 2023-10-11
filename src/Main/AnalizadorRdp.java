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

        Iterator<Set<String>> iteradorPlazasAccionTInvariantes = getPlazasAccionTInvariantes(rdp, tInvariantes,
                AnalizadorRdp.getPlazasAccion(rdp))
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

    public static List<Set<String>> getPlazasAccionTInvariantes(Rdp rdp, List<Set<String>> tInvariantes,
            Set<String> plazasAccion) {
        SortedMap<String, SortedMap<String, Integer>> matrizMap = rdp.getMatrizMap();

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

    public static void getAllMarcados(
            Rdp rdp,
            Map<String, Integer> marcadoInicial,
            HashSet<Map<String, Integer>> marcados) {

        if (!marcados.add(marcadoInicial)) {
            // Si se repite marcado volver
            return;
        }

        Set<String> transicionesSensibilizadas = rdp.getTrancisiones().stream()
                .filter(t -> rdp.getMatrizMap().get(t).entrySet().stream()
                        .allMatch(
                                marcadoNecesario -> marcadoInicial.get(marcadoNecesario.getKey())
                                        + marcadoNecesario.getValue() >= 0))
                .collect(Collectors.toSet());

        for (String t : transicionesSensibilizadas) {
            // Copy marcado
            Map<String, Integer> marcado = new HashMap<String, Integer>();
            marcado.putAll(marcadoInicial);
            // Simular disparo
            rdp.getMatrizMap().get(t)
                    .entrySet()
                    .forEach(plazaTok -> marcado.merge(plazaTok.getKey(),
                            plazaTok.getValue(),
                            Integer::sum));

            getAllMarcados(rdp, marcado, marcados);
        }
    }

    public static Map<String, Double> getPromedioPlazas(HashSet<Map<String, Integer>> marcados,
            Set<String> plazas) {
        HashSet<Map<String, Integer>> marcadosPlazas = new HashSet<Map<String, Integer>>();
        for (Map<String, Integer> marcado : marcados) {
            marcadosPlazas.add(marcado.entrySet().stream().filter(p -> plazas.contains(p.getKey()))
                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())));
        }

        Map<String, Double> promedioPlazas = new HashMap<String, Double>();

        for (Map<String, Integer> map : marcadosPlazas) {
            for (Entry<String, Integer> e : map.entrySet()) {
                promedioPlazas.merge(e.getKey(), Double.valueOf(e.getValue()), Double::sum);
            }
        }

        return promedioPlazas.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue() / marcadosPlazas.size()));
    }

    public static double getPromedioGeneral(Map<String, Double> marcadosPromedio) {
        return marcadosPromedio.entrySet().stream()
                .mapToDouble(x -> x.getValue())
                .sum();
    }

    public static int getMaxHilosActivos(HashSet<Map<String, Integer>> marcados, Set<String> plazasAccion) {

        HashSet<Map<String, Integer>> marcadosAccion = new HashSet<Map<String, Integer>>();
        for (Map<String, Integer> marcado : marcados) {
            marcadosAccion.add(marcado.entrySet().stream().filter(p -> plazasAccion.contains(p.getKey()))
                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())));
        }

        return marcadosAccion.stream()
                .map(marcado -> marcado.values().stream().mapToInt(Integer::intValue).sum())
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }

    public static boolean verificarPInvariantes(HashSet<Map<String, Integer>> marcados,
            List<Set<String>> pInvariantes) {

        Map<Set<String>, Integer> pInvariantesSumas = new HashMap<Set<String>, Integer>();

        for (Set<String> pInvariante : pInvariantes) {
            for (Map<String, Integer> marcado : marcados) {
                int sumaPInvariante = marcado.entrySet().stream()
                        .filter(p -> pInvariante.contains(p.getKey()))
                        .map(p -> p.getValue())
                        .reduce(0, Integer::sum);

                if (pInvariantesSumas.containsKey(pInvariante)) {
                    if (sumaPInvariante != pInvariantesSumas.get(pInvariante)) {
                        return false;
                    }
                }

                pInvariantesSumas.put(pInvariante, sumaPInvariante);
            }
        }

        return true;
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