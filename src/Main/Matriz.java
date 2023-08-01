package Main;

import java.util.ArrayList;
import java.util.List;

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
     * Chequea si la matriz tiene alguna fila con set positivos o negativos con
     * cardinalidad 1 (Count == 1)
     * 
     * @return Indice de la fila que tiene cardinalidad 1, -1 significa que ambos
     *         sets tienen cardinalida != 1
     * 
     */
    public int filaConCardinalidadUno() {
        List<Integer> positivos, negativos;

        for (int i = 0; i < m; i++) {
            positivos = getIndicesPositivos(i);
            negativos = getIndicesNegativos(i);

            if (positivos.size() == 1 || negativos.size() == 1) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Encontrar el indice de la columna en el set que tiene cardinalidad 1
     * 
     * @return El indice de la columa en el set con cardinalidad 1
     */
    public int columnaCardinalidadUno() {
        List<Integer> positivos, negativos;

        for (int i = 0; i < m; i++) {
            positivos = getIndicesPositivos(i);
            negativos = getIndicesNegativos(i);

            if (positivos.size() == 1) {
                return positivos.get(0) - 1;
            }
            if (negativos.size() == 1) {
                return negativos.get(0) - 1;
            }
        }

        return -1;
    }

    /**
     * Chequear si la matriz contiene un set vacio de valores negativos o positivos
     * en alguna fila. Si ambos set no estan vacios se necesita de combinacion
     * lineal
     * 
     * @return True si se cumple la condición y eliminación de columna es requerida
     */
    public boolean puedeEliminarColumna() {
        for (int i = 0; i < m; i++) {
            List<Integer> positivos = getIndicesPositivos(i);
            List<Integer> negativos = getIndicesNegativos(i);

            // Si hay un solo set vacio
            if (positivos.isEmpty() ^ negativos.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtener indice de columnas a cambiar linealmente
     * 
     * @return Lista de indice de columnas a combinar linealmente
     */
    public List<Integer> columnasACombinarLinealmente() {
        List<Integer> positivos, negativos;

        for (int i = 0; i < m; i++) {
            positivos = getIndicesPositivos(i);
            negativos = getIndicesNegativos(i);

            if (positivos.size() == 1) {
                return negativos;
            } else if (negativos.size() == 1) {
                return positivos;
            }
        }
        return null;
    }

    /**
     * Eliminar columna de la matriz
     * 
     * @param iColumna Indice de columna a eliminar
     * @return Matriz con la respectiva columna eliminada
     */
    public Matriz eliminarColumna(int iColumna) {
        Matriz reducida = new Matriz(m, n);
        // Array de columnas q no van a ser eliminadas
        int[] cols = new int[n - 1];
        int count = 0;

        // Encontrar los indices de las columnas q no van a ser eliminadas
        for (int i = 0; i < n; i++) {
            if (i != iColumna) {
                cols[count++] = i;
            }
        }

        reducida = getMatriz(0, m - 1, cols);

        return reducida;
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
     * Encontrar primera fila que no es todo ceros
     * 
     * @return Indice de la primer fila con valores distintos a cero
     */
    public int primeraFilaNoTodaCeros() {
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
     * Encontrar columna del primer elemento no cero en la fila
     * 
     * @param iFila Indice de fila a buscar
     * @return Indice de columna con valor no cero en la fila
     */
    public int getPrimerElementoNoCero(int iFila) {
        int k = -1;

        for (int j = 0; j < n; j++) {
            if (get(iFila, j) != 0) {
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
     * Obtener submatriz
     * 
     * @param i0 Indice fila incial
     * @param i1 Indice fial final
     * @param j0 Indice columna inicial
     * @param j1 Indice columna final
     * @return Matriz(i0:i1,j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     */
    public Matriz getMatriz(int i0, int i1, int j0, int j1) {
        Matriz subMatriz = new Matriz(i1 - i0 + 1, j1 - j0 + 1);
        int[][] subData = subMatriz.getData();
        try {
            for (int i = i0; i <= i1; i++) {
                System.arraycopy(data[i], j0, subData[i - i0], j0 - j0, j1 + 1 - j0);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Indices submatriz");
        }
        return subMatriz;
    }

    /**
     * Obtener submatriz
     * 
     * @param i0 Indice fila incial
     * @param i1 Indice fial final
     * @param c  Array de indice columas
     * @return Matriz(i0:i1,c(:))
     * @exception ArrayIndexOutOfBoundsException
     */
    Matriz getMatriz(int i0, int i1, int[] c) {
        Matriz subMatriz = new Matriz(i1 - i0 + 1, c.length);
        int[][] subData = subMatriz.getData();

        try {
            for (int i = i0; i <= i1; i++) {
                for (int j = 0; j < c.length; j++) {
                    subData[i - i0][j] = data[i][c[j]];
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Indices submatriz");
        }
        return subMatriz;
    }

    /**
     * Obtiene indices de valores negativos en la fila
     * 
     * @param rowNo Indice de la fila
     * @return Lista de indices que contienen valores negativos
     * @exception ArrayIndexOutOfBoundsException
     */
    public List<Integer> getIndicesNegativos(int rowNo) {
        try {
            // Obtiene submatriz de la fila indicada
            Matriz a = getMatriz(rowNo, rowNo, 0, n - 1);

            List<Integer> listaNegativos = new ArrayList<Integer>();

            for (int i = 0; i < n; i++) {
                if (a.get(0, i) < 0)
                    listaNegativos.add(i + 1);
            }

            return listaNegativos;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Indice submatrix");
        }
    }

    /**
     * Obtiene indices de valores positivos en la fila
     * 
     * @param rowNo Indice de la fila
     * @return Lista de indices que contienen valores positivos
     * @exception ArrayIndexOutOfBoundsException
     */
    public List<Integer> getIndicesPositivos(int rowNo) {
        try {
            // Obtiene submatriz de la fila indicada
            Matriz a = getMatriz(rowNo, rowNo, 0, n - 1);

            List<Integer> listaPositivos = new ArrayList<Integer>();

            for (int i = 0; i < n; i++) {
                if (a.get(0, i) > 0)
                    listaPositivos.add(i + 1);
            }

            return listaPositivos;
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
     * Sumar combinacion lineal de columna k a columnas en j
     * 
     * @param k   Indice de columna que se va a combinar linealmente
     * @param chk Coeficiente a multiplicar la columna k
     * @param j   Columnas a las cuales se va a sumar la combinacion lineal
     * @param jC  Coeficientes a multiplicar columas a sumar
     * @exception ArrayIndexOutOfBoundsException
     */
    public void combinarLinealmente(int k, int chk, List<Integer> j, List<Integer> jC) {
        int chj = 0;

        for (int i = 0; i < j.size(); i++) {
            chj = jC.get(i);

            for (int w = 0; w < m; w++) {
                set(w, j.get(i) - 1, chj * get(w, k) + chk * get(w, j.get(i) - 1));
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
