package Comparador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Importador.IImportador;
import Importador.ImportadorPIPE;
import Main.Rdp;

public class Comparador {

    final static int cantidadDeDisparos = 10000000;
    final static String[] plazasProcesos = { "P1", "P2", "P3", "P4", "P5", "P6", "P8", "P9", "P10" };

    public static void main(String[] args) {

        IImportador importador = new ImportadorPIPE();

        Rdp rdpConDeadlock = importador.Importar("./RedesDePetri/Red de petri.xml");

        Rdp rdpSinDeadlock = importador.Importar("./RedesDePetri/Red de petri sin deadlock.xml");

        System.out.println("Red de Petri Sin desbloquear:");
        Correr(rdpConDeadlock, cantidadDeDisparos);

        System.out.println("\nRed de Petri Desbloqueada:");
        Correr(rdpSinDeadlock, cantidadDeDisparos);
    }

    private static void Correr(Rdp rdp, int cantidadDeDisparos) {
        float[] tokensCount = new float[plazasProcesos.length];
        Set<List<Integer>> marcados = new HashSet<List<Integer>>();
        int deadlocks = 0;

        RellenarEstadisticas(rdp, tokensCount, marcados);

        for (int i = 0; i < cantidadDeDisparos; i++) {
            List<String> transicionesSensibilizadas = rdp.GetTransicionesSensibilizadas();

            if (transicionesSensibilizadas.size() == 0) {
                deadlocks++;
                rdp.Reset();
                continue;
            }

            Collections.shuffle(transicionesSensibilizadas);
            rdp.Disparar(transicionesSensibilizadas.get(0));

            RellenarEstadisticas(rdp, tokensCount, marcados);
        }

        System.out.println("Deadlocks: " + deadlocks);

        System.out.println("Cantidad de tokens promedio: ");

        for (int i = 0; i < tokensCount.length; i++) {
            tokensCount[i] /= cantidadDeDisparos;
            System.out.print(plazasProcesos[i] + ": " + tokensCount[i] + " ");
        }

        System.out.println("\nCantidad de marcados: " + marcados.size());
        return;
    }

    private static void RellenarEstadisticas(Rdp rdp, float[] tokensCount, Set<List<Integer>> marcados) {

        List<Integer> estadoActual = new ArrayList<Integer>();

        for (int i = 0; i < plazasProcesos.length; i++) {
            int tokens = rdp.GetTokens(plazasProcesos[i]);
            estadoActual.add(tokens);
            tokensCount[i] += tokens;
        }

        marcados.add(estadoActual);
    }
}
