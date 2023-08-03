import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import Importador.IImportador;
import Importador.ImportadorFactory;
import Importador.TipoImportador;
import Main.Disparador;
import Main.Monitor;
import Main.Rdp;
import Main.SegmentoEjecucion;
import Politicas.PoliticaPrimera;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        ImportadorFactory importadorFactory = new ImportadorFactory();
        IImportador importador = importadorFactory.getImportador(TipoImportador.PIPE);

        Rdp rdp = importador.importar("./RedesDePetri/Red de petri sin deadlock.xml");
        Monitor monitor = new Monitor(rdp, new PoliticaPrimera());

        List<SegmentoEjecucion> segmentos = SegmentoEjecucion.getSegmentosEjecucion(rdp);

        // Crear hilos necesarios por cada segmento
        List<Thread> hilos = segmentos.stream().flatMap(
                s -> IntStream.range(0, s.getHilos())
                        .mapToObj(i -> new Thread(new Disparador(monitor, s), s.toString())))
                .collect(Collectors.toList());

        // Lanzar hilos
        hilos.forEach(h -> h.start());

        // Esperar a que disparadores terminen
        hilos.forEach(h -> {
            try {
                h.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println("1000 invariantes completados!");
    }
}