package Comparador;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import Importador.IImportador;
import Importador.ImportadorFactory;
import Importador.TipoImportador;
import Main.Rdp;
import Main.AnalizadorRdp;

public class Comparador {

    public static void main(String[] args) {

        ImportadorFactory importadorFactory = new ImportadorFactory();
        IImportador importador = importadorFactory.getImportador(TipoImportador.PIPE);

        Rdp rdpConDeadlock = importador.importar("./RedesDePetri/Red de petri.xml");

        Rdp rdpSinDeadlock = importador.importar("./RedesDePetri/Red de petri sin deadlock.xml");

        System.out.println("Red de Petri Sin desbloquear:");
        analizar(rdpConDeadlock);

        System.out.println("\nRed de Petri Desbloqueada:");
        analizar(rdpSinDeadlock);
    }

    public static void analizar(Rdp rdp) {
        HashSet<List<Integer>> marcados = new HashSet<List<Integer>>();

        List<LinkedHashSet<String>> tInvariantes = AnalizadorRdp.getTInvariantesOrdenados(rdp);
        List<Set<String>> pInvariantes = AnalizadorRdp.getPInvariantes(rdp);
        Set<String> plazasAccion = AnalizadorRdp.getPlazasAccion(rdp);

        System.out.println("T Invariantes: " + tInvariantes);
        System.out.println("P Invariantes: " + pInvariantes);
        System.out.println("Plazas de Acción: " + plazasAccion);

        AnalizadorRdp.searchMarcados(rdp, plazasAccion, marcados);

        double promediosProcesos = AnalizadorRdp.getPromedioMarcados(marcados);
        int maxHilosActivos = AnalizadorRdp.getMaxHilosActivos(marcados);

        AnalizadorRdp.getTransicionesSegmentos(rdp);

        System.out.println("Cantidad marcados posibles: " + marcados.size());
        System.out.println("Promedio tokens en plazas: " + promediosProcesos);
        System.out.println("Max cantidad hilos activos: " + maxHilosActivos);
    }
}