package Main;

public class Matriz {
    private final int N; // Columnas
    private final int M; // Filas
    public final int[][] data;

    public Matriz(int N, int M) {
        this.N = N;
        this.M = M;
        data = new int[N][M];
    }

    public Matriz(Integer[][] data) {
        N = data.length;
        M = data[0].length;
        this.data = new int[N][M];
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
        }

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
                System.out.printf("%d ", data[i][j]);
            System.out.println();
        }
    }

    private void eliminarColumna(Matriz id, int k, int l) {
        // Si pivot es negativo cambiar signo
        if (data[k][l] < 0) {
            int f = data[k][l];
            for (int j = l; j < M; j++) {
                data[k][j] = data[k][j] / f;
            }
        }

        for (int i = k + 1; i < N; i++) {
            double f = data[i][l] / data[k][l];

            for (int j = 0; j < M; j++) {
                data[i][j] = (int) Math.round(data[i][j] - data[k][j] * f);
                if (j < N) {
                    id.data[i][j] = (int) Math.round(id.data[i][j] - id.data[k][j] * f);
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
        int[] temp = data[k];
        data[k] = data[i];
        data[i] = temp;

        temp = id.data[k];
        id.data[k] = id.data[i];
        id.data[i] = temp;
    }
}
