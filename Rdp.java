import java.util.ArrayList;
import java.util.List;

public class Rdp {

    private int[][] matriz;
    private int[] estado;
    private int plazas;
    private int transiciones;

    /**
     * @param matrizIncidencia : int[transiciones][plazas]
     */
    public Rdp(int[][] matrizIncidencia, int[] estadoInicial) {
        matriz = matrizIncidencia;
        estado = estadoInicial;

        plazas = estado.length;
        transiciones = matrizIncidencia.length;
    }

    public boolean Disparar(int transicion) {
        if (IsSensibilizada(transicion)) {
            try {
                for (int i = 0; i < plazas; i++) {
                    estado[i] += matriz[transicion][i];
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                System.err.println(
                        "Numero de plazas en matriz de incidencia no coincide con numero de plazas en el estado incial");
            }

            return true;
        }

        return false;
    }

    public int[] GetEstado() {
        return estado;
    }

    public List<Integer> GetTransicionesSensibilizadas() {
        List<Integer> transicionesSensibilizadas = new ArrayList<Integer>();

        for (int i = 0; i < transiciones; i++) {
            if (IsSensibilizada(i)) {
                transicionesSensibilizadas.add(i);
            }
        }

        return transicionesSensibilizadas;
    }

    private boolean IsSensibilizada(int transicion) {
        try {
            for (int i = 0; i < plazas; i++) {

                int tokens = matriz[transicion][i];

                // Compara la cantidad de tokens por la cantidad de necesarios por esta plaza
                // Si la suma es menos que 0 no esta sensibilizada
                if (tokens < 0) {
                    if (tokens + estado[i] < 0) {
                        // No hay tokens necesarios para sensibilizar transición
                        return false;
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            System.err.println(
                    "Numero de plazas en matriz de incidencia no coincide con numero de plazas en el estado incial");
        }

        return true;
    }
}
