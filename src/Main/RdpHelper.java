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
        /*
         * | Tests Invariant Analysis IModule
         * |
         * | C = incidence matrix.
         * | B = identity matrix with same number of columns as C.
         * | Becomes the matrix of vectors in the end.
         * | pPlus = integer array of +ve indices of a row.
         * | pMinus = integer array of -ve indices of a row.
         * | pPlusMinus = set union of the above integer arrays.
         */
        int m = c.m;
        int n = c.n;

        // generate the nxn identity matrix
        Matriz B = Matriz.identidad(n, n);

        // while there are no zero elements in C do the steps of phase 1
        // --------------------------------------------------------------------------------------
        // PHASE 1:
        // --------------------------------------------------------------------------------------
        while (!(c.esTodaCeros())) {
            if (c.puedeEliminarColumna()) {
                // check each row (case 1.1)
                for (int i = 0; i < m; i++) {
                    List<Integer> pPlus = c.getIndicesPositivos(i); // get +ve indices of ith row
                    List<Integer> pMinus = c.getIndicesNegativos(i); // get -ve indices of ith row
                    if (pPlus.isEmpty() || pMinus.isEmpty()) { // case-action 1.1.a
                                                               // this has to be done for all elements in the union
                                                               // pPlus U pMinus
                                                               // so first construct the union
                        List<Integer> pPlusMinus = pPlus.isEmpty() ? pMinus : pPlus;

                        // eliminate each column corresponding to nonzero elements in pPlusMinus union
                        for (int j = pPlusMinus.size() - 1; j >= 0; j--) {
                            if (pPlusMinus.get(j) != 0) {
                                c = c.eliminateCol(pPlusMinus.get(j) - 1);
                                B = B.eliminateCol(pPlusMinus.get(j) - 1);
                                n--; // reduce the number of columns since new matrix is smaller
                            }
                        }
                    }
                }
            } else if (c.cardinalityCondition() >= 0) {
                while (c.cardinalityCondition() >= 0) {
                    // while there is a row in the C matrix that satisfies the cardinality condition
                    // do a linear combination of the appropriate columns and eliminate the
                    // appropriate column.
                    int cardRow = -1; // the row index where cardinality == 1
                    cardRow = c.cardinalityCondition();
                    // get the column index of the column to be eliminated
                    int k = c.cardinalityOne();
                    if (k == -1) {
                        System.out.println("Error");
                    }

                    // get the comlumn indices to be changed by linear combination
                    List<Integer> j = c.colsToUpdate();

                    // update columns with linear combinations in matrices C and B
                    // first retrieve the coefficients
                    int[] jCoef = new int[n];
                    for (int i = 0; i < j.size(); i++) {
                        if (j.get(i) != 0) {
                            jCoef[i] = Math.abs(c.get(cardRow, (j.get(i) - 1)));
                        }
                    }

                    // do the linear combination for C and B
                    // k is the column to add, j is the array of cols to add to
                    c.linearlyCombine(k, Math.abs(c.get(cardRow, k)), j, jCoef);
                    B.linearlyCombine(k, Math.abs(c.get(cardRow, k)), j, jCoef);

                    // eliminate column of cardinality == 1 in matrices C and B
                    c = c.eliminateCol(k);
                    B = B.eliminateCol(k);
                    // reduce the number of columns since new matrix is smaller
                    n--;
                }
            } else {
                // row annihilations (condition 1.1.b.2)
                // operate only on non-zero rows of C (row index h)
                // find index of first non-zero row of C (int h)
                int h = c.firstNonZeroRowIndex();
                while ((h = c.firstNonZeroRowIndex()) > -1) {

                    // the column index of the first non zero element of row h
                    int k = c.firstNonZeroElementIndex(h);

                    // find first non-zero element at column k, chk
                    int chk = c.get(h, k);

                    // find all the other indices of non-zero elements in that row chj[]
                    int[] chj = new int[n - 1];
                    chj = c.findRemainingNZIndices(h);

                    while (!(isEmptySet(chj))) {
                        // chj empty only when there is just one nonzero element in the
                        // whole row, this should not happen as this case is eliminated
                        // in the first step, so we would never arrive at this while()
                        // with just one nonzero element

                        // find all the corresponding elements in that row (coefficients jCoef[])
                        int[] jCoef = c.findRemainingNZCoef(h);

                        // adjust linear combination coefficients according to sign
                        int[] alpha, beta; // adjusted coefficients for kth and remaining columns respectively
                        alpha = alphaCoef(chk, jCoef);
                        beta = betaCoef(chk, jCoef.length);

                        // linearly combine kth column, coefficient alpha, to jth columns, coefficients
                        // beta
                        c.linearlyCombine(k, alpha, chj, beta);
                        B.linearlyCombine(k, alpha, chj, beta);

                        // delete kth column
                        c = c.eliminateCol(k);
                        B = B.eliminateCol(k);

                        chj = c.findRemainingNZIndices(h);
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