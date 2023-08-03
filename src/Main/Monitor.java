package Main;

import java.util.Set;
import java.util.concurrent.Semaphore;

import Politicas.IPolitica;

public class Monitor {
    private final Rdp rdp;
    private final Colas colas;
    private final Semaphore mutex = new Semaphore(1, true);

    private Estadistica estadistica;
    private IPolitica politica;

    private boolean k = false;

    public Monitor(Rdp redDePetri, IPolitica politica) {
        rdp = redDePetri;
        colas = new Colas(rdp.getTrancisiones());
        this.politica = politica;
        this.estadistica = new Estadistica(redDePetri);
    }

    public void dispararTransicion(String transicion) throws InterruptedException {
        mutex.acquire();

        k = true;

        while (k) {
            boolean disparoExitoso = rdp.disparar(transicion);

            if (disparoExitoso) {
                estadistica.registrarDisparo(transicion);

                Set<String> sensibilizadas = rdp.getTransicionesSensibilizadas();
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
                // Bloquear hilo, a espera de que la transicion se sensibilize
                mutex.release();
                colas.acquire(transicion);
            }
        }

        mutex.release();
    }

    public Estadistica getEstadistica() {
        return estadistica;
    }
}
