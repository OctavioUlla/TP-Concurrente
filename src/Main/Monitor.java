package Main;

import java.util.Set;
import java.util.concurrent.Semaphore;

import Politicas.IPolitica;
import Politicas.PoliticaRandom;

public class Monitor {
    private final Rdp rdp;
    private final Colas colas;
    private final Semaphore mutex = new Semaphore(1, true);

    private IPolitica politica;
    private boolean k = false;

    public Monitor(Rdp redDePetri) {
        rdp = redDePetri;
        colas = new Colas(rdp.getTrancisiones());
        this.politica = new PoliticaRandom();
    }

    public void dispararTransicion(String transicion) throws InterruptedException {
        mutex.acquire();

        k = true;

        while (k) {
            boolean disparoExitoso = rdp.disparar(transicion);

            if (disparoExitoso) {
                System.out.printf("%s Disparada\n", transicion);

                Set<String> sensibilizadas = rdp.getTransicionesMarcadosNecesarios();
                Set<String> esperando = colas.getTransicionesEspera();

                // Transiciones sensibilizadas y con hilos bloqueados
                sensibilizadas.retainAll(esperando);

                // Si hay transiciones que se pueden disparar con hilos bloqueados
                if (sensibilizadas.size() > 0) {
                    String proximaTransicion = politica
                            .getProximaTransicion(sensibilizadas);

                    // Activar hilo y salir del monitor
                    colas.release(proximaTransicion);

                    return;
                } else {
                    k = false;
                }
            } else {
                // Si tiene marcados necesarios significa que es temporal y todavia no se llego
                // a alfa, esperar hasta alfa
                if (rdp.hasMarcadoNecesario(transicion)) {
                    mutex.release();
                    Thread.sleep(rdp.getEsperaTemporal(transicion));
                    mutex.acquire();
                    k = true;
                }
                // Si no es temporal esperar a marcado necesario
                else {
                    mutex.release();
                    colas.acquire(transicion);
                }
            }
        }

        mutex.release();
    }

    public void setPolitica(IPolitica politica) {
        this.politica = politica;
    }
}
