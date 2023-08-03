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
     * Construir matriz a partir de array 2D
     * 
     * @param data 2D array
     * @exception IllegalArgumentException Todas las filas deben tener el mismo
     *                                     largo
     */
    public Matriz(Integer[][] data) {
        m = data.length;
        n = data[0].length;
        this.data = new int[m][n];
        for (int i = 0; i < m; i++) {
            if (data[i].length != n) {
                throw new IllegalArgumentException(
                        "Todas las filas deben tener el mismo largo");
            }
            for (int j = 0; j < n; j++) {
                this.data[i][j] = data[i][j];
            }
        }
    }

    public Matriz kernel() {
        Matriz r = new Matriz(this);
        int nReducida = r.n;

        // Generar matriz identidad nxn
        Matriz B = Matriz.identidad(nReducida, nReducida);

        while (!(r.esTodaCeros())) {
            // Si hay un set vacio se puede eliminar columna
            if (r.puedeEliminarColumna()) {
                for (int i = 0; i < m; i++) {
                    List<Integer> iPositivos = r.getIndicesPositivos(i);
                    List<Integer> iNegativos = r.getIndicesNegativos(i);
                    // Si hay un set vacio se puede eliminar esta columna
                    if (iPositivos.isEmpty() || iNegativos.isEmpty()) {
                        // Seleccionar set no vacio
                        List<Integer> iNoVacio = iPositivos.isEmpty() ? iNegativos : iPositivos;

                        // Eliminar cada columna correspondiente a valores no ceros del set
                        for (Integer indice : iNoVacio) {
                            r = r.eliminarColumna(indice - 1);
                            B = B.eliminarColumna(indice - 1);
                            nReducida--; // Reducir n ya que la nueva matriz es mas pequeña
                        }
                    }
                }
            }
            // Si no hay set vacios y alguno tiene cardinalidad 1
            else if (r.filaConCardinalidadUno() >= 0) {
                while (r.filaConCardinalidadUno() >= 0) {
                    // Dada esta condicion se necesita hacer combinacion lineal antes de eliminar
                    // columna
                    int filaCardinalidadUno = r.filaConCardinalidadUno();
                    // Obtener indice de la columna a eliminar
                    int pivot = r.columnaCardinalidadUno();

                    // Estas columnas son del set que tiene cardinalidad != 1
                    List<Integer> j = r.columnasACombinarLinealmente();
                    List<Integer> jCoef = new ArrayList<>(j.size());

                    // Obtener coeficientes a multiplicar columnas j
                    for (Integer i : j) {
                        jCoef.add(Math.abs(r.get(filaCardinalidadUno, i - 1)));
                    }

                    r.combinarLinealmente(pivot, Math.abs(r.get(filaCardinalidadUno, pivot)), j, jCoef);
                    B.combinarLinealmente(pivot, Math.abs(r.get(filaCardinalidadUno, pivot)), j, jCoef);

                    // Eliminar columna con cardinalidad 1
                    r = r.eliminarColumna(pivot);
                    B = B.eliminarColumna(pivot);
                    // Reducir n ya que la nueva matriz es mas pequeña
                    nReducida--;
                }
            }
            // No hay set vacios y ambos tienen cardinalidad > 1
            else {
                // Operar solo en filas que no son ceros (h)
                int fila = r.primeraFilaNoTodaCeros();
                while ((fila = r.primeraFilaNoTodaCeros()) > -1) {

                    int columna = r.getPrimerElementoNoCero(fila);

                    int itemNoCero = r.get(fila, columna);

                    // Encontar los elementos distintos de cero restantes
                    List<Integer> itemsNoCeroRestantes = r.getElementosNoCeroRestantes(fila);

                    while (!itemsNoCeroRestantes.isEmpty()) {
                        List<Integer> jCoef = r.getCoeficientesElementosNoCero(fila);

                        /*
                         * Ajustar coeficientes dependiendo el signo
                         * 
                         * Si sign(jCoef) != sign(itemNoCero)
                         * entonce alpha = abs(jCoef) beta = abs(itemNoCero)
                         * 
                         * Si sign(jCoef) == sign(itemNoCero)
                         * Entonces alpha = -abs(jCoef) beta = abs(itemNoCero)
                         */
                        List<Integer> alpha = new ArrayList<Integer>();
                        List<Integer> beta = new ArrayList<Integer>();
                        // Alpha
                        for (Integer i : jCoef) {
                            if ((itemNoCero * i) < 0) {
                                alpha.add(Math.abs(i));
                            } else {
                                alpha.add(-Math.abs(i));
                            }
                        }
                        // Beta
                        int absItemNoCero = Math.abs(itemNoCero);

                        for (int i = 0; i < jCoef.size(); i++) {
                            beta.add(absItemNoCero);
                        }

                        r.combinarLinealmente(columna, alpha, itemsNoCeroRestantes, beta);
                        B.combinarLinealmente(columna, alpha, itemsNoCeroRestantes, beta);

                        r = r.eliminarColumna(columna);
                        B = B.eliminarColumna(columna);

                        itemsNoCeroRestantes = r.getElementosNoCeroRestantes(fila);
                    }
                }
            }
        }

        return B;
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
     * Encontrar columna del primer elemento distinto a 0 en la fila
     * 
     * @param iFila Indice de fila a buscar
     * @return Indice de columna con valor distinto a 0 en la fila
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
     * Encontrar columnas restantes con valores distintos a cero
     * 
     * @param iFila Fila en la que buscar elementos
     * @return Lista de indices restantes de columnas con valor distinto a 0
     */
    public List<Integer> getElementosNoCeroRestantes(int iFila) {
        List<Integer> k = new ArrayList<Integer>();

        for (int j = 1; j < n; j++) {
            if (get(iFila, j) != 0)
                k.add(j);
        }
        return k;
    }

    /**
     * Encontrar coeficientes correspondientes a los valores restantes en las
     * columnas distintas de cero de la fila
     * 
     * @param iFila Fila en la cual buscar coeficientes
     * @return Lista de coeficientes de valores distintos a cero en la fila excepto
     *         el primero
     */
    public List<Integer> getCoeficientesElementosNoCero(int iFila) {
        List<Integer> k = new ArrayList<Integer>();
        int elemento;

        for (int j = 1; j < n; j++) {
            if ((elemento = get(iFila, j)) != 0) {
                k.add(elemento);
            }
        }
        return k;
    }

    /**
     * Obtener elemento de matriz
     * 
     * @param i Fila
     * @param j Columna
     * @return Matriz(i,j)
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
     * @param k        Indice de columna que se va a combinar linealmente
     * @param chk      Coeficiente a multiplicar la columna k
     * @param cols     Columnas a las cuales se va a sumar la combinacion lineal
     * @param colsCoef Coeficientes a multiplicar columas a sumar
     * @exception ArrayIndexOutOfBoundsException
     */
    public void combinarLinealmente(int k, int chk, List<Integer> cols, List<Integer> colsCoef) {
        int chj = 0;

        for (int i = 0; i < cols.size(); i++) {
            chj = colsCoef.get(i);

            for (int j = 0; j < m; j++) {
                set(j, cols.get(i) - 1, chj * get(j, k) + chk * get(j, cols.get(i) - 1));
            }
        }
    }

    /**
     * Sumar combinacion lineal de columna k a columnas en j
     * 
     * @param k     Indice de columna que se va a combinar linealmente
     * @param alpha Coeficientes a multiplicar la columna k
     * @param cols  Columnas a las cuales se va a sumar la combinacion lineal
     * @param beta  Coeficientes a multiplicar columas a sumar
     * @exception ArrayIndexOutOfBoundsException
     */
    public void combinarLinealmente(int k, List<Integer> alpha, List<Integer> cols, List<Integer> beta) {
        for (int i = 0; i < cols.size(); i++) {
            if (cols.get(i) != 0) {
                for (int j = 0; j < m; j++) { // for all the elements in a column
                    set(j, cols.get(i), alpha.get(i) * get(j, k) + beta.get(i) * get(j, cols.get(i)));
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
