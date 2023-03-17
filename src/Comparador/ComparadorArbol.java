package Comparador;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import Importador.IImportador;
import Importador.ImportadorFactory;
import Importador.TipoImportador;
import Main.Rdp;

public class ComparadorArbol {

    final static int cantidadDeDisparos = 10000000;
    final static List<String> plazasProcesos = Collections
            .unmodifiableList(
                    Arrays.asList(new String[] { "P1", "P2", "P3", "P4", "P5", "P6", "P8", "P9", "P10" }));

    public static void main(String[] args) {

        ImportadorFactory importadorFactory = new ImportadorFactory();
        IImportador importador = importadorFactory.getImportador(TipoImportador.PIPE);

        Rdp rdpConDeadlock = importador.importar("./RedesDePetri/Red de petri.xml");

        Rdp rdpSinDeadlock = importador.importar("./RedesDePetri/Red de petri sin deadlock.xml");

        System.out.println("Red de Petri Sin desbloquear:");
        correr(rdpConDeadlock);

        System.out.println("\nRed de Petri Desbloqueada:");
        correr(rdpSinDeadlock);
    }

    public static void correr(Rdp rdp) {
        HashSet<List<Integer>> marcados = new HashSet<List<Integer>>();

        searchMarcados(rdp, marcados);

        System.out.println("Cantidad marcados posibles: " + marcados.size());
        System.out.println("Promedio tokens en plazas: "
                + marcados.stream()
                        .map(toks -> toks
                                .stream()
                                .reduce(0, Integer::sum))
                        .reduce(0, Integer::sum) / (float) marcados.size());
    }

    public static void searchMarcados(Rdp rdp, HashSet<List<Integer>> marcados) {

        List<Integer> marcado = plazasProcesos.stream()
                .map(p -> rdp.getTokens(p))
                .collect(Collectors.toList());

        if (!marcados.add(marcado)) {
            // Si se repite marcado volver
            return;
        }

        rdp.getTransicionesSensibilizadas().stream()
                .forEach(t -> {
                    rdp.disparar(t);
                    searchMarcados(rdp, marcados);
                });

    }
}