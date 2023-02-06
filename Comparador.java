import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Comparador {

    final static int cantidadDeDisparos = 1000;

    public static void main(String[] args) {

        IImportador importador = new ImportadorPIPE();

        Rdp rdpConDeadlock = importador.Importar("RedesDePetri/Red de petri.xml");

        Rdp rdpSinDeadlock = importador.Importar("RedesDePetri/Red de petri sin deadlock.xml");

        System.out.println("Red de Petri Sin desbloquear:");
        Correr(rdpConDeadlock, cantidadDeDisparos);

        System.out.println("Red de Petri Desbloqueada:");
        Correr(rdpSinDeadlock, cantidadDeDisparos);
    }

    public static void Correr(Rdp rdp, int cantidadDeDisparos) {
        int tokensProceso = 0;
        List<Integer> estadoProceso = new ArrayList<>();

        Set<List<Integer>> marcadosProceso = new HashSet<List<Integer>>();
        List<String> transicionesSensibilizadas;

        for (int j = 0; j < 10; j++) {
            if (j != 6) {
                tokensProceso += rdp.GetEstado()[j];
                estadoProceso.add(rdp.GetEstado()[j]);
            }
        }
        marcadosProceso.add(estadoProceso);
        estadoProceso.clear();

        for (int i = 0; i < cantidadDeDisparos; i++) {
            transicionesSensibilizadas = rdp.GetTransicionesSensibilizadas();

            if (transicionesSensibilizadas.size() == 0) {
                cantidadDeDisparos = i;
                System.out.println("Se ha detectado un Deadlock \ncantidad de disparos realizados:" + i + "\n");
                break;
            }

            Collections.shuffle(transicionesSensibilizadas);
            rdp.Disparar(transicionesSensibilizadas.get(0));

            for (int j = 0; j < 10; j++) {
                if (j != 6) {
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
