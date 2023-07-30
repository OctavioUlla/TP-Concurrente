package Main;

public class Matriz {
    private final int N; // Columnas
    private final int M; // Filas
    public final double[][] data;

    public Matriz(int N, int M) {
        this.N = N;
        this.M = M;
        data = new double[N][M];
    }

    public Matriz(Integer[][] data) {
        N = data.length;
        M = data[0].length;
        this.data = new double[N][M];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < M; j++)
                this.data[i][j] = data[i][j];
    }

    public static Matriz identity(int N) {
        Matriz I = new Matriz(N, N);
        for (int i = 0; i < N; i++)
            I.data[i][i] = 1;
        return I;
    }

    public Matriz traspuesta() {
        Matriz A = new Matriz(M, N);
        for (int i = 0; i < N; i++)
            for (int j = 0; j < M; j++)
                A.data[j][i] = this.data[i][j];
        return A;
    }

    public Matriz multiplicar(Matriz B) {
        Matriz A = this;
        if (A.M != B.N)
            throw new RuntimeException("Illegal matrix dimensions.");
        Matriz C = new Matriz(A.N, B.M);
        for (int i = 0; i < C.N; i++)
            for (int j = 0; j < C.M; j++)
                for (int k = 0; k < A.M; k++)
                    C.data[i][j] += (A.data[i][k] * B.data[k][j]);
        return C;
    }

    public Matriz resolver() {
        // T index
        int k = 0;
        // P index
        int l = 0;

        Matriz id = Matriz.identity(N);

        while (k < N && l < M) {
            if (data[k][l] != 0) {
                eliminarColumna(id, k, l);
                k++;
                l++;
            } else if (isColumnaValoresRestantesCero(k, l) == false) {
                intercambiarFila(id, k, l);

                eliminarColumna(id, k, l);
                k++;
                l++;
            } else {
                l++;
            }
            print();
        }

        id.print();
        return id;
    }

    public int getRango() {
        int rango = 0;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (data[i][j] != 0) {
                    rango++;
                    break;
                }
            }
        }

        return rango;
    }

    public void print() {
        System.out.println();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++)
                System.out.printf("%d ", (int) data[i][j]);
            System.out.println();
        }
    }

    private void eliminarColumna(Matriz id, int k, int l) {
        // Si pivot es negativo cambiar signo
        /*
         * if (data[k][l] < 0) {
         * double f = data[k][l];
         * for (int j = 0; j < M; j++) {
         * data[k][j] = data[k][j] / f;
         * if (j < N) {
         * id.data[k][j] = id.data[k][j] / f;
         * }
         * }
         * }
         */
        /*
         * for (int i = k + 1; i < N; i++) {
         * double f = data[i][l] / data[k][l];
         * 
         * for (int j = 0; j < M; j++) {
         * data[i][j] = data[i][j] - data[k][j] * f;
         * if (j < N) {
         * id.data[i][j] = id.data[i][j] - id.data[k][j] * f;
         * }
         * }
         * }
         */

        // Si pivot es positivo
        if (data[k][l] > 0) {
            int negativo = encontrarNegativoColumna(k, l);
            // Si se encontre fila negativa
            if (negativo >= 0) {
                eliminarFilasPositivasConNegativo(id, k, l, negativo);
            }

            eliminarFilasNegativasConPositivo(id, k, l, k);
        }
        // Pivot negativo
        else if (data[k][l] < 0) {
            int positivo = encontrarPositivoColumna(k, l);
            // Si se encontre fila negativa
            if (positivo >= 0) {
                eliminarFilasNegativasConPositivo(id, k, l, positivo);
            }

            eliminarFilasPositivasConNegativo(id, k, l, k);
        }
    }

    private int encontrarNegativoColumna(int k, int l) {
        for (int i = k + 1; i < N; i++) {
            if (data[i][l] < 0) {
                return i;
            }
        }

        return -1;
    }

    private int encontrarPositivoColumna(int k, int l) {
        for (int i = k + 1; i < N; i++) {
            if (data[i][l] > 0) {
                return i;
            }
        }

        return -1;
    }

    private void eliminarFilasPositivasConNegativo(Matriz id, int k, int l, int negativo) {
        for (int i = 0; i < N; i++) {
            if (data[i][l] > 0 && i != k && i != negativo) {
                double f = data[i][l] / data[negativo][l];

                for (int j = 0; j < M; j++) {
                    data[i][j] = data[i][j] - data[negativo][j] * f;
                    if (j < N) {
                        id.data[i][j] = id.data[i][j] - id.data[k][j] * f;
                    }
                }
            }
        }
    }

    private void eliminarFilasNegativasConPositivo(Matriz id, int k, int l, int positivo) {
        for (int i = 0; i < N; i++) {
            if (data[i][l] < 0 && i != k && i != positivo) {
                double f = data[i][l] / data[positivo][l];

                for (int j = 0; j < M; j++) {
                    data[i][j] = data[i][j] - data[positivo][j] * f;
                    if (j < N) {
                        id.data[i][j] = id.data[i][j] - id.data[k][j] * f;
                    }
                }
            }
        }
    }

    private boolean isColumnaValoresRestantesCero(int k, int l) {
        for (int i = k + 1; i < N; i++) {
            if (data[i][l] != 0) {
                return false;
            }
        }

        return true;
    }

    private void intercambiarFila(Matriz id, int k, int l) {
        int i;
        // Encontrar fila con valor distinto a cero
        for (i = k + 1; i < N; i++) {
            if (data[i][l] != 0) {
                break;
            }
        }

        // Intercambiar
        double[] temp = data[k];
        data[k] = data[i];
        data[i] = temp;

        temp = id.data[k];
        id.data[k] = id.data[i];
        id.data[i] = temp;
    }
}
