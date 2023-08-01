package Main;

public class Matriz {
    private final int[][] data;

    /**
     * Tamaño fila
     * 
     * @serial Tamaño fila
     */
    public final int m;

    /**
     * Tamaño columna
     * 
     * @serial Tamaño columna
     */
    public final int n;

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
        // a value >= 0 means either pPlus or pMinus have
        // cardinality == 1 and it is the value of the row where this condition
        // occurs -1 means that both pPlus and pMinus have cardinality != 1
        int countpPlus = 0, countpMinus = 0;
        int[] pPlus, pMinus; // arrays containing the indices of +ve and -ve

        for (int i = 0; i < m; i++) {
            countpPlus = 0;
            countpMinus = 0;
            pPlus = getIndicesPositivos(i); // get +ve indices of ith row
            pMinus = getIndicesNegativos(i); // get -ve indices of ith row
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
        return -1;
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

        int countpPlus = 0, countpMinus = 0;
        int[] pPlus, pMinus; // arrays containing the indices of +ve and -ve

        for (int i = 0; i < m; i++) {
            countpPlus = 0;
            countpMinus = 0;
            pPlus = getIndicesPositivos(i); // get +ve indices of ith row
            pMinus = getIndicesNegativos(i); // get -ve indices of ith row
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
     * Chequear si la matriz contiene un set vacio de valores negativos o positivos
     * en alguna fila. Si ambos set no estan vacios se necesita de combinacion
     * lineal
     * 
     * @return True si se cumple la condición y eliminación de columna es requerida
     */
    public boolean puedeEliminarColumna() {
        // Falso significa que no hay ningun set de positivos o negativos vacios
        boolean positivosVacio = true, negativosVacio = true;

        for (int i = 0; i < m; i++) {
            positivosVacio = true;
            negativosVacio = true;
            int[] positivos = getIndicesPositivos(i);
            int[] negativos = getIndicesNegativos(i);
            int pLength = positivos.length, nLength = negativos.length;

            for (int j = 0; j < pLength; j++) {
                if (positivos[j] != 0) {
                    // Si hay un valor distinto de cero el set no esta vacio
                    positivosVacio = false;
                }
            }
            for (int j = 0; j < nLength; j++) {
                if (negativos[j] != 0) {
                    // Si hay un valor distinto de cero el set no esta vacio
                    negativosVacio = false;
                }
            }
            // Si hay un solo set vacio
            if (positivosVacio ^ negativosVacio) {
                return true;
            }
        }
        return false;
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

        int countpPlus = 0, countpMinus = 0;
        int[] pPlus, pMinus; // arrays containing the indices of +ve and -ve

        for (int i = 0; i < m; i++) {
            countpPlus = 0;
            countpMinus = 0;
            pPlus = getIndicesPositivos(i); // get +ve indices of ith row
            pMinus = getIndicesNegativos(i); // get -ve indices of ith row
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

        reduced = getMatrix(0, m - 1, cols);

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
     * Find the first non-zero row of a matrix.
     * 
     * @return Row index (starting from 0 for 1st row) of the first row from top
     *         that is not only zeros, -1 of there is no such row.
     */
    public int firstNonZeroRowIndex() {
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
     * Obtiene indices de valores negativos en la fila
     * 
     * @param rowNo Indice de la fila
     * @return Array de indices que contienen valores negativos
     * @exception ArrayIndexOutOfBoundsException
     */
    public int[] getIndicesNegativos(int rowNo) {
        try {
            // Obtiene submatriz de la fila indicada
            Matriz a = getMatrix(rowNo, rowNo, 0, n - 1);

            int count = 0; // Cantidad de elementos positivos
            int[] negativesArray = new int[n];

            for (int i = 0; i < n; i++) {
                if (a.get(0, i) < 0)
                    negativesArray[count++] = i + 1;
            }

            return negativesArray;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Indice submatrix");
        }
    }

    /**
     * Obtiene indices de valores positivos en la fila
     * 
     * @param rowNo Indice de la fila
     * @return Array de indices que contienen valores positivos
     * @exception ArrayIndexOutOfBoundsException
     */
    public int[] getIndicesPositivos(int rowNo) {
        try {
            // Obtiene submatriz de la fila indicada
            Matriz a = getMatrix(rowNo, rowNo, 0, n - 1);

            int count = 0; // Cantidad de elementos positivos
            int[] arrayPositivos = new int[n];

            for (int i = 0; i < n; i++) {
                if (a.get(0, i) > 0)
                    arrayPositivos[count++] = i + 1;
            }

            return arrayPositivos;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Indice submatrix");
        }
    }

    /**
     * Chequea si la matriz consiste de todos ceros
     * 
     * @return true si contiene todos ceros
     */
    public boolean esTodaCeros() {
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
        int n = j.length;

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
     * Setear valor de elemento
     * 
     * @param i Indice fila
     * @param j indice columna
     * @param v valor
     * @exception ArrayIndexOutOfBoundsException
     */
    public void set(int i, int j, int v) {
        data[i][j] = v;
    }

    /**
     * Obtener matriz traspuesta
     * 
     * @return Matriz traspuesta
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
     * Generar matriz identidad
     * 
     * @param m Numero de filas
     * @param n Numero de columnas
     * @return Una matriz m x n con 1 en la diagonal
     */
    public static Matriz identidad(int m, int n) {
        Matriz identidad = new Matriz(m, n);

        int[][] X = identidad.getData();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                X[i][j] = (i == j ? 1 : 0);
            }
        }
        return identidad;
    }

    public void print() {
        System.out.println();

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                System.out.printf("%d ", data[i][j]);
            }
            System.out.println();
        }
    }
}
