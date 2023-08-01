package Main;

/**
 * This support class provides many general matrix manipulation functions, as 
 * well as a number of specialised matrix operations pertaining to Petri net 
 * analysis.
 * @author Manos Papantoniou & Michael Camacho
 * @version February 2004
 *
 * Based on the Jama Matrix class, the PNMatrix class offers a small subset
 * of the operations, and is used for matrices of integers only, as required
 * by the petri net analyser project.
 *
 * <P>
 * This Class provides the fundamental operations of numerical linear algebra.  
 * Various constructors create Matrices from two dimensional arrays of integer 
 * numbers. 
 * Various "gets" and "sets" provide access to submatrices and matrix elements. 
 * Several methods implement basic matrix arithmetic, including matrix addition 
 * and multiplication, and element-by-element array operations.
 * Methods for reading and printing matrices are also included.
 * <P>
 * @author Edwin Chung a new boolean attribute was added (6th Feb 2007)
 * @author Pere Bonet (minor changes)
 */
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class Matriz {
    private final int[][] data;

    /**
     * Tamaño fila
     * 
     * @serial Tamaño fila
     */
    private final int m;

    /**
     * Tamaño columna
     * 
     * @serial Tamaño columna
     */
    private final int n;

    /**
     * Construir matriz M x N
     * 
     * @param m Cantidad filas
     * @param n Cantidad columnas
     */
    public Matriz(int m, int n) {
        this.m = m;
        this.n = n;
        data = new int[m][n];
    }

    /**
     * Construir matriz a partir de otra
     */
    public Matriz(Matriz matriz) {
        this.m = matriz.m;
        this.n = matriz.n;
        data = matriz.data.clone();
    }

    /**
     * Construir matriz a partir de array 2D
     * 
     * @param data 2D array
     * @exception IllegalArgumentException Todas las filas deben tener el mismo
     *                                     largo
     */
    public Matriz(int[][] data) {
        m = data.length;
        n = data[0].length;
        for (int i = 0; i < m; i++) {
            if (data[i].length != n) {
                throw new IllegalArgumentException(
                        "Todas las filas deben tener el mismo largo");
            }
        }
        this.data = data;
    }

    /**
     * Check if a matrix has a row that satisfies the cardinality condition 1.1.b
     * of the algorithm.
     * 
     * @return True if the matrix satisfies the condition and linear combination
     *         of columns followed by column elimination is required.
     */
    public int cardinalityCondition() {
        int cardRow = -1; // a value >= 0 means either pPlus or pMinus have
        // cardinality == 1 and it is the value of the row where this condition
        // occurs -1 means that both pPlus and pMinus have cardinality != 1
        int pPlusCard = 0, pMinusCard = 0, countpPlus = 0, countpMinus = 0;
        int[] pPlus, pMinus; // arrays containing the indices of +ve and -ve
        int m = getRowDimension(), n = getColumnDimension();

        for (int i = 0; i < m; i++) {
            countpPlus = 0;
            countpMinus = 0;
            pPlus = getPositiveIndices(i); // get +ve indices of ith row
            pMinus = getNegativeIndices(i); // get -ve indices of ith row
            for (int j = 0; j < n; j++) {
                if (pPlus[j] != 0) { // if there is nonzero element count it
                    countpPlus++;
                }
            }
            for (int j = 0; j < n; j++) {
                if (pMinus[j] != 0) { // if there is nonzero element count it
                    countpMinus++;
                }
            }
            if (countpPlus == 1 || countpMinus == 1) {
                return i;
            }
        }
        return cardRow;
    }

    /**
     * Find the column index of the element in the pPlus or pMinus set, where
     * pPlus or pMinus has cardinality == 1.
     * 
     * @return The column index, -1 if unsuccessful (this shouldn't happen under
     *         normal operation).
     */
    public int cardinalityOne() {
        int k = -1; // the col index of cardinality == 1 element

        int pPlusCard = 0, pMinusCard = 0, countpPlus = 0, countpMinus = 0;
        int[] pPlus, pMinus; // arrays containing the indices of +ve and -ve
        int m = getRowDimension(), n = getColumnDimension();

        for (int i = 0; i < m; i++) {
            countpPlus = 0;
            countpMinus = 0;
            pPlus = getPositiveIndices(i); // get +ve indices of ith row
            pMinus = getNegativeIndices(i); // get -ve indices of ith row
            for (int j = 0; j < n; j++) {
                if (pPlus[j] != 0) { // if there is nonzero element count it
                    countpPlus++;
                }
            }
            for (int j = 0; j < n; j++) {
                if (pMinus[j] != 0) {// if there is nonzero element count it
                    countpMinus++;
                }
            }
            if (countpPlus == 1) {
                return pPlus[0] - 1;
            }
            if (countpMinus == 1) {
                return pMinus[0] - 1;
            }
        }

        return k;
    }

    /**
     * Check if a matrix satisfies condition 1.1 of the algorithm.
     * 
     * @return True if the matrix satisfies the condition and column elimination
     *         is required.
     */
    public boolean checkCase11() {
        boolean satisfies11 = false; // true means there is an empty set pPlus or pMinus
        // false means that both pPlus and pMinus are non-empty
        boolean pPlusEmpty = true, pMinusEmpty = true;
        int[] pPlus, pMinus; // arrays containing the indices of +ve and -ve
        int m = getRowDimension();

        for (int i = 0; i < m; i++) {
            pPlusEmpty = true;
            pMinusEmpty = true;
            pPlus = getPositiveIndices(i); // get +ve indices of ith row
            pMinus = getNegativeIndices(i); // get -ve indices of ith row
            int pLength = pPlus.length, mLength = pMinus.length;

            for (int j = 0; j < pLength; j++) {
                if (pPlus[j] != 0) {
                    // if there is nonzero element then false (non-empty set)
                    pPlusEmpty = false;
                }
            }
            for (int j = 0; j < mLength; j++) {
                if (pMinus[j] != 0) {
                    // if there is nonzero element then false (non-empty set)
                    pMinusEmpty = false;
                }
            }
            // if there is an empty set and it is not a zeros-only row then column
            // elimination is possible
            if ((pPlusEmpty || pMinusEmpty) && !isZeroRow(i)) {
                return true;
            }
            // reset pPlus and pMinus to 0
            for (int j = 0; j < pLength; j++) {
                pPlus[j] = 0;
            }
            for (int j = 0; j < mLength; j++) {
                pMinus[j] = 0;
            }
        }
        return satisfies11;
    }

    /**
     * Find the comlumn indices to be changed by linear combination.
     * 
     * @return An array of integers, these are the indices increased by 1 each.
     */
    public int[] colsToUpdate() {
        int js[] = null; // An array of integers with the comlumn indices to be
        // changed by linear combination.
        // the col index of cardinality == 1 element

        int pPlusCard = 0, pMinusCard = 0, countpPlus = 0, countpMinus = 0;
        int[] pPlus, pMinus; // arrays containing the indices of +ve and -ve
        int m = getRowDimension();
        int n = getColumnDimension();

        for (int i = 0; i < m; i++) {
            countpPlus = 0;
            countpMinus = 0;
            pPlus = getPositiveIndices(i); // get +ve indices of ith row
            pMinus = getNegativeIndices(i); // get -ve indices of ith row
            for (int j = 0; j < n; j++) {
                if (pPlus[j] != 0) { // if there is nonzero element count it
                    countpPlus++;
                }
            }
            for (int j = 0; j < n; j++) {
                if (pMinus[j] != 0) { // if there is nonzero element count it
                    countpMinus++;
                }
            }
            // if pPlus has cardinality ==1 return all the elements in pMinus reduced by 1
            // each
            if (countpPlus == 1) {
                return pMinus;
            } else if (countpMinus == 1) {
                // if pMinus has cardinality ==1 return all the elements in pPlus reduced by 1
                // each
                return pPlus;
            }
        }
        return js;
    }

    /**
     * Eliminate a column from the matrix, column index is toDelete
     * 
     * @param toDelete The column number to delete.
     * @return The matrix with the required row deleted.
     */
    public Matriz eliminateCol(int toDelete) {
        int m = getRowDimension(), n = getColumnDimension();
        Matriz reduced = new Matriz(m, n);
        int[] cols = new int[n - 1]; // array of cols which will not be eliminated
        int count = 0;

        // find the col numbers which will not be eliminated
        for (int i = 0; i < n; i++) {
            // if an index will not be eliminated, keep it in the new array cols
            if (i != toDelete) {
                cols[count++] = i;
            }
        }
        // System.out.print("Eliminating column " + toDelete + " from matrix below...
        // keeping columns ");
        // printArray(cols);
        // print(2, 0);
        // System.out.println("Reduced matrix");
        reduced = getMatrix(0, m - 1, cols);
        // reduced.print(2, 0);

        return reduced;
    }

    /**
     * Access the internal two-dimensional array.
     * 
     * @return Pointer to the two-dimensional array of matrix elements.
     */
    int[][] getData() {
        return data;
    }

    /**
     * Get row dimension.
     * 
     * @return The number of rows.
     */
    public int getRowDimension() {
        return m;
    }

    /**
     * Get column dimension.
     * 
     * @return The number of columns.
     */
    public int getColumnDimension() {
        return n;
    }

    /**
     * Find the first non-zero row of a matrix.
     * 
     * @return Row index (starting from 0 for 1st row) of the first row from top
     *         that is not only zeros, -1 of there is no such row.
     */
    public int firstNonZeroRowIndex() {
        int m = getRowDimension();
        int n = getColumnDimension();
        int h = -1;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (get(i, j) != 0) {
                    return i;
                }
            }
        }
        return h;
    }

    /**
     * Find the column index of the first non zero element of row h.
     * 
     * @param h The row to look for the non-zero element in
     * @return Column index (starting from 0 for 1st column) of the first
     *         non-zero element of row h, -1 if there is no such column.
     */
    public int firstNonZeroElementIndex(int h) {
        int n = getColumnDimension();
        int k = -1;

        for (int j = 0; j < n; j++) {
            if (get(h, j) != 0) {
                return j;
            }
        }
        return k;
    }

    /**
     * Find the column indices of all but the first non zero elements of row h.
     * 
     * @param h The row to look for the non-zero element in
     * @return Array of ints of column indices (starting from 0 for 1st column)
     *         of all but the first non-zero elements of row h.
     */
    public int[] findRemainingNZIndices(int h) {
        int n = getColumnDimension();
        int[] k = new int[n];
        int count = 0; // increases as we add new indices in the array of ints

        for (int j = 1; j < n; j++) {
            if (get(h, j) != 0)
                k[count++] = j;
        }
        return k;
    }

    /**
     * Find the coefficients corresponding to column indices of all but the first
     * non zero elements of row h.
     * 
     * @param h The row to look for the non-zero coefficients in
     * @return Array of ints of coefficients of all but the first non-zero
     *         elements of row h.
     */
    public int[] findRemainingNZCoef(int h) {
        int n = getColumnDimension();
        int[] k = new int[n];
        int count = 0; // increases as we add new indices in the array of ints
        int anElement; // an element of the matrix

        for (int j = 1; j < n; j++) {
            if ((anElement = get(h, j)) != 0) {
                k[count++] = anElement;
            }
        }
        return k;
    }

    /**
     * Get a single element.
     * 
     * @param i Row index.
     * @param j Column index.
     * @return A(i,j)
     * @exception ArrayIndexOutOfBoundsException
     */
    public int get(int i, int j) {
        return data[i][j];
    }

    /**
     * Get a submatrix.
     * 
     * @param i0 Initial row index
     * @param i1 Final row index
     * @param j0 Initial column index
     * @param j1 Final column index
     * @return A(i0:i1,j0:j1)
     * @exception ArrayIndexOutOfBoundsException Submatrix indices
     */
    public Matriz getMatrix(int i0, int i1, int j0, int j1) {
        Matriz x = new Matriz(i1 - i0 + 1, j1 - j0 + 1);
        int[][] B = x.getData();
        try {
            for (int i = i0; i <= i1; i++) {
                System.arraycopy(data[i], j0, B[i - i0], j0 - j0, j1 + 1 - j0);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return x;
    }

    /**
     * Get a submatrix.
     * 
     * @param i0 Initial row index
     * @param i1 Final row index
     * @param c  Array of column indices.
     * @return A(i0:i1,c(:))
     * @exception ArrayIndexOutOfBoundsException Submatrix indices
     */
    Matriz getMatrix(int i0, int i1, int[] c) {
        Matriz x = new Matriz(i1 - i0 + 1, c.length);
        int[][] B = x.getData();

        try {
            for (int i = i0; i <= i1; i++) {
                for (int j = 0; j < c.length; j++) {
                    B[i - i0][j] = data[i][c[j]];
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return x;
    }

    /**
     * For row rowNo of the matrix received return the column indices of all the
     * negative elements
     * 
     * @param rowNo row iside the Matrix to check for -ve elements
     * @return Integer array of indices of negative elements.
     * @exception ArrayIndexOutOfBoundsException Submatrix indices
     */
    public int[] getNegativeIndices(int rowNo) {
        int n = getColumnDimension(); // find the number of columns

        // create the single row submatrix for the required row
        try {
            Matriz a = new Matriz(1, n);
            a = getMatrix(rowNo, rowNo, 0, n - 1);

            int count = 0; // index of a negative element in the returned array
            int[] negativesArray = new int[n];
            for (int i = 0; i < n; i++) // initialise to zero
                negativesArray[i] = 0;

            for (int i = 0; i < n; i++) {
                if (a.get(0, i) < 0)
                    negativesArray[count++] = i + 1;
            }

            return negativesArray;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
    }

    /**
     * For row rowNo of the matrix received return the column indices of all the
     * positive elements
     * 
     * @param rowNo row iside the Matrix to check for +ve elements
     * @return The integer array of indices of all positive elements.
     * @exception ArrayIndexOutOfBoundsException Submatrix indices
     */
    public int[] getPositiveIndices(int rowNo) {
        int n = getColumnDimension(); // find the number of columns

        // create the single row submatrix for the required row
        try {
            Matriz a = new Matriz(1, n);
            a = getMatrix(rowNo, rowNo, 0, n - 1);

            int count = 0; // index of a positive element in the returned array
            int[] positivesArray = new int[n];
            for (int i = 0; i < n; i++) // initialise to zero
                positivesArray[i] = 0;

            for (int i = 0; i < n; i++) {
                if (a.get(0, i) > 0)
                    positivesArray[count++] = i + 1;
            }

            return positivesArray;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
    }

    /**
     * Check if a matrix is all zeros.
     * 
     * @return true if all zeros, false otherwise
     */
    public boolean isZeroMatrix() {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (get(i, j) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * isZeroRow returns true if the ith row is all zeros
     * 
     * @param r row to check for full zeros.
     * @return true if the row is full of zeros.
     */
    boolean isZeroRow(int r) {
        // TODO: optimize this!
        Matriz a = new Matriz(1, getColumnDimension());
        a = getMatrix(r, r, 0, getColumnDimension() - 1);
        return a.isZeroMatrix();
    }

    /**
     * Add a linear combination of column k to columns in array j[].
     * 
     * @param k   Column index to add.
     * @param chk Coefficient of col to add
     * @param j   Array of column indices to add to.
     * @param jC  Array of coefficients of column indices to add to.
     * @exception ArrayIndexOutOfBoundsException
     */
    public void linearlyCombine(int k, int chk, int[] j, int[] jC) {
        // k is column index of coefficient of col to add
        // chj is coefficient of col to add
        int chj = 0; // coefficient of column to add to
        int m = getRowDimension();

        for (int i = 0; i < j.length; i++) {
            if (j[i] != 0) {
                chj = jC[i];
                // System.out.print("\nchk = " + chk + "\n");
                for (int w = 0; w < m; w++) {
                    set(w, j[i] - 1, chj * get(w, k) + chk * get(w, j[i] - 1));
                }
            }
        }
    }

    /**
     * Add a linear combination of column k to columns in array j[].
     * 
     * @param k     Column index to add.
     * @param alpha Array of coefficients of col to add
     * @param j     Array of column indices to add to.
     * @param beta  Array of coefficients of column indices to add to.
     * @exception ArrayIndexOutOfBoundsException
     */
    public void linearlyCombine(int k, int[] alpha, int[] j, int[] beta) {
        // k is column index of coefficient of col to add
        // a is array of coefficients of col to add
        // int chk = 0; // coefficient of column to add to
        int m = getRowDimension(), n = j.length;

        for (int i = 0; i < n; i++) {
            if (j[i] != 0) {
                // chk = jC[i];
                // System.out.print("\nchk = " + chk + "\n");
                for (int w = 0; w < m; w++) { // for all the elements in a column
                    set(w, j[i], alpha[i] * get(w, k) + beta[i] * get(w, j[i]));
                }
            }
        }
    }

    /**
     * Set a single element.
     * 
     * @param i Row index.
     * @param j Column index.
     * @param s A(i,j).
     * @exception ArrayIndexOutOfBoundsException
     */
    public void set(int i, int j, int s) {
        data[i][j] = s;
    }

    /**
     * Matrix transpose.
     * 
     * @return A'
     */
    public Matriz transpose() {
        Matriz t = new Matriz(n, m);
        int[][] tData = t.getData();

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                tData[j][i] = data[i][j];
            }
        }
        return t;
    }

    /**
     * Generate identity matrix]
     * 
     * @param m Number of rows.
     * @param n Number of colums.
     * @return An m-by-n matrix with ones on the diagonal and zeros elsewhere.
     */
    public static Matriz identity(int m, int n) {
        Matriz a = new Matriz(m, n);

        int[][] X = a.getData();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                X[i][j] = (i == j ? 1 : 0);
            }
        }
        return a;
    }

    /**
     * Print the matrix to stdout. Line the elements up in columns
     * with a Fortran-like 'Fw.d' style format.
     * 
     * @param w Column width.
     * @param d Number of digits after the decimal.
     */
    public void print(int w, int d) {
        print(new PrintWriter(System.out, true), w, d);
    }

    /**
     * Print the matrix to the output stream. Line the elements up in
     * columns with a Fortran-like 'Fw.d' style format.
     * 
     * @param output Output stream.
     * @param w      Column width.
     * @param d      Number of digits after the decimal.
     */
    void print(PrintWriter output, int w, int d) {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.UK));
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);
        print(output, format, w + 2);
    }

    /**
     * Print the matrix to stdout. Line the elements up in columns.
     * Use the format object, and right justify within columns of width
     * characters.
     * Note that if the matrix is to be read back in, you probably will want
     * to use a NumberFormat that is set to UK Locale.
     * 
     * @param format A Formatting object for individual elements.
     * @param width  Field width for each column.
     * @see java.text.DecimalFormat#setDecimalFormatSymbols
     */
    public void print(NumberFormat format, int width) {
        print(new PrintWriter(System.out, true), format, width);
    }

    // DecimalFormat is a little disappointing coming from Fortran or C's printf.
    // Since it doesn't pad on the left, the elements will come out different
    // widths. Consequently, we'll pass the desired column width in as an
    // argument and do the extra padding ourselves.

    /**
     * Print the matrix to the output stream. Line the elements up in columns.
     * Use the format object, and right justify within columns of width
     * characters.
     * Note that is the matrix is to be read back in, you probably will want
     * to use a NumberFormat that is set to US Locale.
     * 
     * @param output the output stream.
     * @param format A formatting object to format the matrix elements
     * @param width  Column width.
     * @see java.text.DecimalFormat#setDecimalFormatSymbols
     */
    void print(PrintWriter output, NumberFormat format, int width) {
        output.println(); // start on new line.
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                String s = format.format(data[i][j]); // format the number
                int padding = Math.max(1, width - s.length()); // At _least_ 1 space
                for (int k = 0; k < padding; k++) {
                    output.print(' ');
                }
                output.print(s);
            }
            output.println();
        }
        output.println(); // end with blank line.
    }
}
