package Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;

import java.util.LinkedList;

public class RdpHelper {

    public static List<Set<String>> getTInvariantes(Rdp rdp) {
        // Resolver matriz indicencia W^T . X = 0 para obtener t invariantes
        Matrix matrizTInvariantes = findVectors(rdp.getMatriz().transpose());

        // Transformar matriz resultante a lista de invariantes
        List<String> transiciones = rdp.getTrancisiones()
                .stream()
                .collect(Collectors.toList());

        List<Set<String>> tInvariantes = new ArrayList<Set<String>>();

        for (int i = 0; i < matrizTInvariantes.getColumnDimension(); i++) {
            Set<String> tInvariante = new HashSet<String>();
            for (int j = 0; j < matrizTInvariantes.getRowDimension(); j++) {
                if (matrizTInvariantes.get(j, i) != 0) {
                    tInvariante.add(transiciones.get(j));
                }
            }
            tInvariantes.add(tInvariante);
        }

        return tInvariantes;
    }

    public static List<Set<String>> getPInvariantes(Rdp rdp) {
        // Matriz matriz = rdp.getMatriz().traspuesta();

        Matrix matrizPInvariantes = findVectors(rdp.getMatriz());

        // Transformar matriz resultante a lista de invariantes
        List<String> plazas = rdp.getPlazas()
                .stream()
                .collect(Collectors.toList());

        List<Set<String>> pInvariantes = new ArrayList<Set<String>>();

        for (int i = 0; i < matrizPInvariantes.getColumnDimension(); i++) {
            Set<String> pInvariante = new HashSet<String>();
            for (int j = 0; j < matrizPInvariantes.getRowDimension(); j++) {
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

    public static Matrix findVectors(Matrix c) {
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
        int m = c.getRowDimension(), n = c.getColumnDimension();

        // generate the nxn identity matrix
        Matrix B = Matrix.identity(n, n);

        // arrays containing the indices of +ve and -ve elements in a row vector
        // respectively
        int[] pPlus, pMinus;

        // while there are no zero elements in C do the steps of phase 1
        // --------------------------------------------------------------------------------------
        // PHASE 1:
        // --------------------------------------------------------------------------------------
        while (!(c.isZeroMatrix())) {
            if (c.checkCase11()) {
                // check each row (case 1.1)
                for (int i = 0; i < m; i++) {
                    pPlus = c.getPositiveIndices(i); // get +ve indices of ith row
                    pMinus = c.getNegativeIndices(i); // get -ve indices of ith row
                    if (isEmptySet(pPlus) || isEmptySet(pMinus)) { // case-action 1.1.a
                                                                   // this has to be done for all elements in the union
                                                                   // pPlus U pMinus
                                                                   // so first construct the union
                        int[] pPlusMinus = uniteSets(pPlus, pMinus);

                        // eliminate each column corresponding to nonzero elements in pPlusMinus union
                        for (int j = pPlusMinus.length - 1; j >= 0; j--) {
                            if (pPlusMinus[j] != 0) {
                                c = c.eliminateCol(pPlusMinus[j] - 1);
                                B = B.eliminateCol(pPlusMinus[j] - 1);
                                n--; // reduce the number of columns since new matrix is smaller
                            }
                        }
                    }
                    resetArray(pPlus); // reset pPlus and pMinus to 0
                    resetArray(pMinus);
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
                    int j[] = c.colsToUpdate();

                    // update columns with linear combinations in matrices C and B
                    // first retrieve the coefficients
                    int[] jCoef = new int[n];
                    for (int i = 0; i < j.length; i++) {
                        if (j[i] != 0) {
                            jCoef[i] = Math.abs(c.get(cardRow, (j[i] - 1)));
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
                // show the result
                // System.out.println("Pseudodiagonal positive basis of Ker C after phase 1:");
                // B.print(2, 0);
            }
        }
        // System.out.println("end of phase one");
        // END OF PHASE ONE, now B contains a pseudodiagonal positive basis of Ker C
        // --------------------------------------------------------------------------------------
        // PHASE 2:
        // --------------------------------------------------------------------------------------
        // h is -1 at this point, make it equal to the row index that has a -ve element.
        // rowWithNegativeElement with return -1 if there is no such row, and we exit
        // the loop.
        int h;
        while ((h = B.rowWithNegativeElement()) > -1) {

            pPlus = B.getPositiveIndices(h); // get +ve indices of hth row (1st col = index 1)
            pMinus = B.getNegativeIndices(h); // get -ve indices of hth row (1st col = index 1)

            // effective length is the number of non-zero elements
            int pPlusLength = effectiveSetLength(pPlus);
            int pMinusLength = effectiveSetLength(pMinus);

            if (pPlusLength != 0) { // set of positive coef. indices must me non-empty
                                    // form the cross product of pPlus and pMinus
                                    // for each pair (j, k) in the cross product, operate a linear combination on
                                    // the columns
                                    // of indices j, k, in order to get a new col with a zero at the hth element
                                    // The number of new cols obtained = the number of pairs (j, k)
                for (int j = 0; j < pPlusLength; j++) {
                    for (int k = 0; k < pMinusLength; k++) {
                        // coefficients of row h, cols indexed j, k in pPlus, pMinus
                        // respectively
                        int jC = pPlus[j] - 1, kC = pMinus[k] - 1;

                        // find coeficients for linear combination, just the abs values
                        // of elements this is more efficient than finding the least
                        // common multiple and it does not matter since later we will
                        // find gcd of col and we will normalise with that the col
                        // elements
                        int a = -B.get(h, kC), b = B.get(h, jC);

                        // create the linear combination a*jC-column + b*kC-column, an
                        // IntMatrix mx1 where m = number of rows of B
                        m = B.getRowDimension();
                        Matrix v1 = new Matrix(m, 1); // column vector mx1 of zeros
                        Matrix v2 = new Matrix(m, 1); // column vector mx1 of zeros
                        v1 = B.getMatrix(0, m - 1, jC, jC);
                        v2 = B.getMatrix(0, m - 1, kC, kC);
                        v1.timesEquals(a);
                        v2.timesEquals(b);
                        v2.plusEquals(v1);

                        // find the gcd of the elements in this new col
                        int V2gcd = v2.gcd();

                        // divide all the col elements by their gcd if gcd > 1
                        if (V2gcd > 1) {
                            v2.divideEquals(V2gcd);
                        }

                        // append the new col to B
                        n = B.getColumnDimension();
                        Matrix f = new Matrix(m, n + 1);
                        f = B.appendVector(v2);
                        B = f.copy();
                    }
                } // endfor (j,k) operations

                // delete from B all cols with index in pMinus
                for (int ww = 0; ww < pMinusLength; ww++) {
                    B = B.eliminateCol(pMinus[ww] - 1);
                }

            } // endif
        } // end while
          // System.out.println("\nAfter column transformations in phase 2 (non-minimal
          // generating set) B:");
          // B.print(2, 0);

        // delete from B all cols having non minimal support
        // k is the index of column to be eliminated, if it is -1 then there is
        // no col to be eliminated
        int k = 0;
        // form a matrix with columns the row indices of non-zero elements
        Matrix bi = B.nonZeroIndices();

        while (k > -1) {
            k = bi.findNonMinimal();

            if (k != -1) {
                B = B.eliminateCol(k);
                bi = B.nonZeroIndices();
            }
        }

        // display the result
        // System.out.println("Minimal generating set (after phase 2):");
        // B.print(2, 0);
        return B;
    }

    /**
     * find the number of non-zero elements in a set
     *
     * @param pSet The set count the number of non-zero elements.
     * @return The number of non-zero elements.
     */
    private static int effectiveSetLength(int[] pSet) {
        int effectiveLength = 0; // number of non-zero elements
        int setLength = pSet.length;

        for (int i = 0; i < setLength; i++) {
            if (pSet[i] != 0) {
                effectiveLength++;
            } else {
                return effectiveLength;
            }
        }
        return effectiveLength;
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

    private static void resetArray(int[] a) {
        for (int i = 0; i < a.length; i++) {
            a[i] = 0;
        }
    }

    /**
     * Unite two sets (arrays of integers) so that if there is a common entry in
     * the arrays it appears only once, and all the entries of each array appear
     * in the union. The resulting array size is the same as the 2 arrays and
     * they are both equal. We are only interested in non-zero elements. One of
     * the 2 input arrays is always full of zeros.
     *
     * @param A The first set to unite.
     * @param B The second set to unite.
     * @return The union of the two input sets.
     */
    private static int[] uniteSets(int[] A, int[] B) {
        int[] union = new int[A.length];

        if (isEmptySet(A)) {
            union = B;
        } else {
            union = A;
        }
        return union;
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