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
        Matriz matrizTInvariantes = findVectors(rdp.getMatriz().transpose());

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
        Matriz matrizPInvariantes = findVectors(rdp.getMatriz());

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

    public static Matriz findVectors(Matriz c) {
        int m = c.m;
        int n = c.n;

        // Generar matriz identidad nxn
        Matriz B = Matriz.identidad(n, n);

        while (!(c.esTodaCeros())) {
            // Si hay un set vacio se puede eliminar columna
            if (c.puedeEliminarColumna()) {
                for (int i = 0; i < m; i++) {
                    List<Integer> iPositivos = c.getIndicesPositivos(i);
                    List<Integer> iNegativos = c.getIndicesNegativos(i);
                    // Si hay un set vacio se puede eliminar esta columna
                    if (iPositivos.isEmpty() || iNegativos.isEmpty()) {
                        // Seleccionar set no vacio
                        List<Integer> iNoVacio = iPositivos.isEmpty() ? iNegativos : iPositivos;

                        // Eliminar cada columna correspondiente a valores no ceros del set
                        for (Integer indice : iNoVacio) {
                            c = c.eliminarColumna(indice - 1);
                            B = B.eliminarColumna(indice - 1);
                            n--; // Reducir n ya que la nueva matriz es mas pequeña
                        }
                    }
                }
            }
            // Si no hay set vacios y alguno tiene cardinalidad 1
            else if (c.filaConCardinalidadUno() >= 0) {
                while (c.filaConCardinalidadUno() >= 0) {
                    // Dada esta condicion se necesita hacer combinacion lineal antes de eliminar
                    // columna
                    int filaCardinalidadUno = c.filaConCardinalidadUno();
                    // Obtener indice de la columna a eliminar
                    int pivot = c.columnaCardinalidadUno();

                    // Estas columnas son del set que tiene cardinalidad != 1
                    List<Integer> j = c.columnasACombinarLinealmente();
                    List<Integer> jCoef = new ArrayList<>(j.size());

                    // Obtener coeficientes a multiplicar columnas j
                    for (Integer i : j) {
                        jCoef.add(Math.abs(c.get(filaCardinalidadUno, i - 1)));
                    }

                    // Hacer combinación lineal
                    c.combinarLinealmente(pivot, Math.abs(c.get(filaCardinalidadUno, pivot)), j, jCoef);
                    B.combinarLinealmente(pivot, Math.abs(c.get(filaCardinalidadUno, pivot)), j, jCoef);

                    // Eliminar columna con cardinalidad 1
                    c = c.eliminarColumna(pivot);
                    B = B.eliminarColumna(pivot);
                    // Reducir n ya que la nueva matriz es mas pequeña
                    n--;
                }
            }
            // No hay set vacios y ambos tienen cardinalidad > 1
            else {
                // Operar solo en filas que no son ceros (h)
                int fila = c.primeraFilaNoTodaCeros();
                while ((fila = c.primeraFilaNoTodaCeros()) > -1) {

                    // the column index of the first non zero element of row h
                    int k = c.getPrimerElementoNoCero(fila);

                    // find first non-zero element at column k, chk
                    int chk = c.get(fila, k);

                    // find all the other indices of non-zero elements in that row chj[]
                    int[] chj = new int[n - 1];
                    chj = c.findRemainingNZIndices(fila);

                    while (!(isEmptySet(chj))) {
                        // chj empty only when there is just one nonzero element in the
                        // whole row, this should not happen as this case is eliminated
                        // in the first step, so we would never arrive at this while()
                        // with just one nonzero element

                        // find all the corresponding elements in that row (coefficients jCoef[])
                        int[] jCoef = c.findRemainingNZCoef(fila);

                        // adjust linear combination coefficients according to sign
                        int[] alpha, beta; // adjusted coefficients for kth and remaining columns respectively
                        alpha = alphaCoef(chk, jCoef);
                        beta = betaCoef(chk, jCoef.length);

                        // linearly combine kth column, coefficient alpha, to jth columns, coefficients
                        // beta
                        c.linearlyCombine(k, alpha, chj, beta);
                        B.linearlyCombine(k, alpha, chj, beta);

                        // delete kth column
                        c = c.eliminarColumna(k);
                        B = B.eliminarColumna(k);

                        chj = c.findRemainingNZIndices(fila);
                    }
                }
            }
        }

        return B;
    }

    /**
     * adjust linear combination coefficients according to sign
     * if sign(j) <> sign(k) then alpha = abs(j) beta = abs(k)
     * if sign(j) == sign(k) then alpha = -abs(j) beta = abs(k)
     *
     * @param k The column index of the first coefficient
     * @param j The column indices of the remaining coefficients
     * @return The adjusted alpha coefficients
     */
    private static int[] alphaCoef(int k, int[] j) {
        int n = j.length; // the length of one row
        int[] alpha = new int[n];

        for (int i = 0; i < n; i++) {
            if ((k * j[i]) < 0) {
                alpha[i] = Math.abs(j[i]);
            } else {
                alpha[i] = -Math.abs(j[i]);
            }
        }
        return alpha;
    }

    /**
     * adjust linear combination coefficients according to sign
     * if sign(j) <> sign(k) then alpha = abs(j) beta = abs(k)
     * if sign(j) == sign(k) then alpha = -abs(j) beta = abs(k)
     *
     * @param chk The first coefficient
     * @param n   The length of one row
     * @return The adjusted beta coefficients
     */
    private static int[] betaCoef(int chk, int n) {
        int[] beta = new int[n];
        int abschk = Math.abs(chk);

        for (int i = 0; i < n; i++) {
            beta[i] = abschk;
        }
        return beta;
    }

    /**
     * check if an array is empty (only zeros)
     *
     * @param pSet The set to check if it is empty.
     * @return True if the set is empty.
     */
    private static boolean isEmptySet(int[] pSet) {
        int setLength = pSet.length;

        for (int i = 0; i < setLength; i++) {
            if (pSet[i] != 0) {
                return false;
            }
        }
        return true;
    }
}