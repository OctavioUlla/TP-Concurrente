import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Comparador {
    
    public static void main(String[] args) {
        int[][] matrizIncidenciaConDeadlock =  {{1,0,0,0,0,0,-1,0,0,0,0,-1,0,0,0},
                                                {-1,1,0,0,0,0,0,0,0,0,0,1,-1,0,0},
                                                {-1,0,1,0,0,0,0,0,0,0,0,1,-1,0,0},
                                                {0,-1,0,1,0,0,0,0,0,0,0,0,1,-1,0},
                                                {0,0,-1,0,1,0,0,0,0,0,0,0,1,-1,0},
                                                {0,0,0,-1,0,1,0,0,0,0,0,0,0,1,-1},
                                                {0,0,0,0,-1,1,0,0,0,0,0,0,0,1,-1},
                                                {0,0,0,0,0,-1,1,0,0,0,0,0,0,0,1},
                                                {0,0,0,0,0,0,0,1,0,0,-1,0,0,-1,0},
                                                {0,0,0,0,0,0,0,-1,1,0,0,0,-1,1,0},
                                                {0,0,0,0,0,0,0,0,-1,1,0,-1,1,0,0},
                                                {0,0,0,0,0,0,0,0,0,-1,1,1,0,0,0}};

        int[] estadoInicialConDeadlock = {0,0,0,0,0,0,4,0,0,0,4,2,2,3,1};

        Rdp rdpConDeadlock = new Rdp(matrizIncidenciaConDeadlock, estadoInicialConDeadlock);

        int[][] matrizIncidenciaSinDeadlock =  {{1,0,0,0,0,0,-1,0,0,0,0,-1,0,0,0,-1,-1},
                                                {-1,1,0,0,0,0,0,0,0,0,0,1,-1,0,0,1,0},
                                                {-1,0,1,0,0,0,0,0,0,0,0,1,-1,0,0,1,0},
                                                {0,-1,0,1,0,0,0,0,0,0,0,0,1,-1,0,0,1},
                                                {0,0,-1,0,1,0,0,0,0,0,0,0,1,-1,0,0,1},
                                                {0,0,0,-1,0,1,0,0,0,0,0,0,0,1,-1,0,0},
                                                {0,0,0,0,-1,1,0,0,0,0,0,0,0,1,-1,0,0},
                                                {0,0,0,0,0,-1,1,0,0,0,0,0,0,0,1,0,0},
                                                {0,0,0,0,0,0,0,1,0,0,-1,0,0,-1,0,-1,-1},
                                                {0,0,0,0,0,0,0,-1,1,0,0,0,-1,1,0,0,1},
                                                {0,0,0,0,0,0,0,0,-1,1,0,-1,1,0,0,1,0},
                                                {0,0,0,0,0,0,0,0,0,-1,1,1,0,0,0,0,0}};

        int[] estadoInicialSinDeadlock = {0,0,0,0,0,0,4,0,0,0,4,2,2,3,1,2,3};

        Rdp rdpSinDeadlock = new Rdp(matrizIncidenciaSinDeadlock, estadoInicialSinDeadlock);

        System.out.println("Red de Petri Sin desbloquear:");
        Correr(rdpConDeadlock,1000);

        System.out.println("Red de Petri Desbloqueada:");
        Correr(rdpSinDeadlock,1000);

        
    }
    
    public static void Correr(Rdp rdp, int cantidadDeDisparos){
        int tokensProceso = 0;
        int[] estadoProceso = new int[10];
        Set<int[]> marcadosProceso =  new HashSet<int[]>();
        List<Integer> transicionesSensibilizadas =  new ArrayList<>();

        for(int j=0 ; j<10 ; j++){
            tokensProceso += rdp.GetEstado()[j];
            //estadoProceso[j] = rdp.GetEstado()[j];
        }
        marcadosProceso.add(rdp.GetEstado());

        for(int i=0 ; i<cantidadDeDisparos ; i++){

            transicionesSensibilizadas = rdp.GetTransicionesSensibilizadas();
            
            if(transicionesSensibilizadas.size()==0){
                cantidadDeDisparos = i;
                System.out.println("Se ha detectado un Deadlock \ncantidad de disparos realizados:"+ i + "\n");
                break;
            }

            Collections.shuffle(transicionesSensibilizadas);
            rdp.Disparar(transicionesSensibilizadas.get(0));    
            
            for(int j=0 ; j<10 ; j++){
                tokensProceso += rdp.GetEstado()[j];
                //estadoProceso[j] = rdp.GetEstado()[j];
            }
            marcadosProceso.add(rdp.GetEstado());
        }

        System.out.println(
                            "\nNumero de tokens promedio en actividades del proceso: "+ tokensProceso/(cantidadDeDisparos+1)+
                            "\nCantidad de marcados en actividades del proceso: "+ marcadosProceso.size());
        return;
    }
}
