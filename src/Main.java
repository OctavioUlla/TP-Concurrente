import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import Importadores.IImportador;
import Importadores.ImportadorPetrinator;
import Main.AnalizadorRdp;
import Main.Disparador;
import Main.Estadistica;
import Main.Monitor;
import Main.Rdp;
import Main.SegmentoEjecucion;
import Politicas.PoliticaBalanceada;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {
        IImportador importador = new ImportadorPetrinator();

        Rdp rdp = importador.importar("./RedesDePetri/Red de petri sin deadlock temporal.pflow");
        Monitor monitor = new Monitor(rdp);
        Estadistica estadistica = rdp.crearEstadisticas();
        monitor.setPolitica(new PoliticaBalanceada(estadistica));

        List<SegmentoEjecucion> segmentos = SegmentoEjecucion.getSegmentosEjecucion(rdp);

        // Crear hilos necesarios por cada segmento
        List<Thread> hilos = segmentos.stream().flatMap(
                s -> IntStream.range(0, s.getHilos())
                        .mapToObj(i -> new Thread(new Disparador(monitor, s), s.toString())))
                .collect(Collectors.toList());

        estadistica.startTimer();

        hilos.forEach(h -> h.start());

        estadistica.wait1000TInvariantes();

        hilos.forEach(h -> h.interrupt());

        // Esperar a que disparadores mueran
        hilos.forEach(h -> {
            try {
                h.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        completarTInvariantes(rdp);

        System.out.println("\nFin Disparos!\n");

        estadistica.stop();
        estadistica.printEstadisticas();

        runTInvarianteAnalizador();
    }

    private static void completarTInvariantes(Rdp rdp) {
        List<String> tRestantes = rdp.getEstadistica().getTInvariantesIncompletos();

        while (!tRestantes.isEmpty()) {
            // Obtener invariantes incompletos
            Iterator<Set<String>> tInvariantesIncompletos = AnalizadorRdp.getTInvariantes(rdp).stream()
                    .filter(tInvariente -> !Collections.disjoint(tInvariente, tRestantes))
                    .iterator();

            while (tInvariantesIncompletos.hasNext()) {
                Set<String> tInvarianteIncompleto = tInvariantesIncompletos.next();
                // Obtener transiciones faltantes del invariante
                if (tInvarianteIncompleto.removeAll(tRestantes)) {
                    // Disparar transiciones restantes para completar invariante
                    tInvarianteIncompleto.forEach(t -> rdp.disparar(t));
                }
            }
        }
    }

    private static void runTInvarianteAnalizador() throws IOException {
        Process process = Runtime.getRuntime().exec("python Analizador.py");
        String buffer;
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((buffer = stdInput.readLine()) != null)
            System.out.println(buffer);
        process.destroy();
    }
}