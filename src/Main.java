import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import Importador.IImportador;
import Importador.ImportadorFactory;
import Importador.TipoImportador;
import Main.AnalizadorRdp;
import Main.Disparador;
import Main.Estadistica;
import Main.Monitor;
import Main.Rdp;
import Main.SegmentoEjecucion;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        ImportadorFactory importadorFactory = new ImportadorFactory();
        IImportador importador = importadorFactory.getImportador(TipoImportador.PIPE);

        Rdp rdp = importador.importar("./RedesDePetri/Red de petri sin deadlock.xml");
        Monitor monitor = new Monitor(rdp);
        Estadistica estadistica = rdp.getEstadistica();

        List<SegmentoEjecucion> segmentos = SegmentoEjecucion.getSegmentosEjecucion(rdp);

        // Crear hilos necesarios por cada segmento
        List<Thread> hilos = segmentos.stream().flatMap(
                s -> IntStream.range(0, s.getHilos())
                        .mapToObj(i -> new Thread(new Disparador(monitor, s), s.toString())))
                .collect(Collectors.toList());

        rdp.startEstadisticas();

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

        System.out.println("1000 invariantes completados!");

        // Terminar invariantes incompletos}
        List<String> tRestantes = estadistica.getTInvariantesIncompletos();

        while (!tRestantes.isEmpty()) {
            // Obtener invariantes incompletos
            Iterator<Set<String>> tInvariantesIncompletos = AnalizadorRdp.getTInvariantes(rdp).stream()
                    .filter(tInvariente -> !Collections.disjoint(tInvariente, tRestantes))
                    .iterator();

            while (tInvariantesIncompletos.hasNext()) {
                System.out.println(tRestantes);
                Set<String> tInvarianteIncompleto = tInvariantesIncompletos.next();
                // Obtener transiciones faltantes del invariante
                if (tInvarianteIncompleto.removeAll(tRestantes)) {
                    // Disparar transiciones restantes para completar invariante
                    tInvarianteIncompleto.forEach(t -> rdp.disparar(t));
                }
            }
        }

        rdp.stopEstadisticas();

        System.out.println("TInvariantes incompletos completados");
    }
}