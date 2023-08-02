package Comparador;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import Importador.IImportador;
import Importador.ImportadorFactory;
import Importador.TipoImportador;
import Main.Rdp;
import Main.RdpHelper;

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

        List<Set<String>> tInvariantes = RdpHelper.getTInvariantes(rdp);
        List<Set<String>> pInvariantes = RdpHelper.getPInvariantes(rdp);
        Set<String> plazasAccion = RdpHelper.getPlazasAccion(rdp);

        System.out.println("T Invariantes: " + tInvariantes);
        System.out.println("P Invariantes: " + pInvariantes);
        System.out.println("Plazas de Acción: " + plazasAccion);

        searchMarcados(rdp, plazasAccion, marcados);

        double promediosProcesos = getPromedioMarcados(marcados);
        int maxHilosActivos = getMaxHilosActivos(marcados);

        System.out.println("Cantidad marcados posibles: " + marcados.size());
        System.out.println("Promedio tokens en plazas: " + promediosProcesos);
        System.out.println("Max cantidad hilos activos: " + maxHilosActivos);
    }

    public static void searchMarcados(Rdp rdp, Set<String> plazasAccion, HashSet<List<Integer>> marcados) {
        // Deep copy
        Map<String, Integer> marcado = rdp.getMarcado().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        List<Integer> marcadoProcesos = marcado.entrySet().stream()
                .filter(p -> plazasAccion.contains(p.getKey()))
                .map(p -> p.getValue())
                .collect(Collectors.toList());

        if (!marcados.add(marcadoProcesos)) {
            // Si se repite marcado volver
            return;
        }

        rdp.getTransicionesSensibilizadas()
                .forEach(t -> {
                    rdp.disparar(t);
                    searchMarcados(rdp, plazasAccion, marcados);
                    rdp.setMarcado(marcado);
                });
    }

    public static double getPromedioMarcados(HashSet<List<Integer>> marcados) {
        return marcados.stream()
                .map(toks -> toks.stream().mapToInt(Integer::intValue).sum())
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
    }

    public static int getMaxHilosActivos(HashSet<List<Integer>> marcados) {
        return marcados.stream()
                .map(toks -> toks.stream().mapToInt(Integer::intValue).sum())
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }
}