import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Comparador {

    public static void main(String[] args) {
        final int cantidadDeDisparos = 1000;
        int[][] matrizIncidenciaConDeadlock = {
                { 1, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, -1, 0, 0, 0 },
                { -1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0 },
                { -1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0 },
                { 0, -1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0 },
                { 0, 0, -1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0 },
                { 0, 0, 0, -1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, -1 },
                { 0, 0, 0, 0, -1, 1, 0, 0, 0, 0, 0, 0, 0, 1, -1 },
                { 0, 0, 0, 0, 0, -1, 1, 0, 0, 0, 0, 0, 0, 0, 1 },
                { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, -1, 0, 0, -1, 0 },
                { 0, 0, 0, 0, 0, 0, 0, -1, 1, 0, 0, 0, -1, 1, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 0, -1, 1, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 1, 0, 0, 0 } };

        int[] estadoInicialConDeadlock = { 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 4, 2, 2, 3, 1 };

        Rdp rdpConDeadlock = new Rdp(matrizIncidenciaConDeadlock, estadoInicialConDeadlock);

        int[][] matrizIncidenciaSinDeadlock = {
                { 1, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, -1, 0, 0, 0, -1, -1 },
                { -1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0, 1, 0 },
                { -1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0, 1, 0 },
                { 0, -1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0, 1 },
                { 0, 0, -1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0, 1 },
                { 0, 0, 0, -1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0 },
                { 0, 0, 0, 0, -1, 1, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0 },
                { 0, 0, 0, 0, 0, -1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, -1, 0, 0, -1, 0, -1, -1 },
                { 0, 0, 0, 0, 0, 0, 0, -1, 1, 0, 0, 0, -1, 1, 0, 0, 1 },
                { 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 0, -1, 1, 0, 0, 1, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 1, 0, 0, 0, 0, 0 } };

        int[] estadoInicialSinDeadlock = { 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 4, 2, 2, 3, 1, 2, 3 };

        Rdp rdpSinDeadlock = new Rdp(matrizIncidenciaSinDeadlock, estadoInicialSinDeadlock);

        System.out.println("Red de Petri Sin desbloquear:");
        Correr(rdpConDeadlock, cantidadDeDisparos);

        System.out.println("Red de Petri Desbloqueada:");
        Correr(rdpSinDeadlock, cantidadDeDisparos);
    }

    public static void Correr(Rdp rdp, int cantidadDeDisparos) {
        int tokensProceso = 0;
        List<Integer> estadoProceso = new ArrayList<>();
        //int[] estadoProceso = new int[10];
        Set<List<Integer>> marcadosProceso = new HashSet<List<Integer>>();
        List<Integer> transicionesSensibilizadas = new ArrayList<>();

        for (int j = 0; j < 10; j++) 
        { 
            if(j!=7)
            {
                tokensProceso += rdp.GetEstado()[j];
                estadoProceso.add(rdp.GetEstado()[j]);
            }            
        }
        marcadosProceso.add(estadoProceso);
        estadoProceso.clear();

        for (int i = 0; i < cantidadDeDisparos; i++) 
        {
            transicionesSensibilizadas = rdp.GetTransicionesSensibilizadas();

            if (transicionesSensibilizadas.size() == 0) 
            {
                cantidadDeDisparos = i;
                System.out.println("Se ha detectado un Deadlock \ncantidad de disparos realizados:" + i + "\n");
                break;
            }

            Collections.shuffle(transicionesSensibilizadas);
            rdp.Disparar(transicionesSensibilizadas.get(0));

            for (int j = 0; j < 10; j++) 
            { 
                if(j!=7)
                {
                    tokensProceso += rdp.GetEstado()[j];
                    estadoProceso.add(rdp.GetEstado()[j]);
                }            
            }
            marcadosProceso.add(estadoProceso);
            estadoProceso.clear();
        }

        System.out.println(
                "\nNumero de tokens promedio en actividades del proceso: " + tokensProceso / (cantidadDeDisparos + 1.0f)
                        +
                        "\nCantidad de marcados en actividades del proceso: " + marcadosProceso.size());
        return;
    }
}
