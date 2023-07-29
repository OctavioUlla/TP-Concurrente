package Main;

import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

import Politicas.IPolitica;

public class Monitor {
    private final Rdp rdp;
    private final Colas colas;
    private final Semaphore mutex = new Semaphore(1, false);
    private IPolitica politica;

    private boolean k = false;

    public Monitor(Rdp redDePetri, IPolitica politica) {
        rdp = redDePetri;
        colas = new Colas(rdp.getTrancisiones());
        this.politica = politica;
    }

    public void dispararTransicion(String transicion) throws InterruptedException {
        mutex.acquire();

        k = true;

        while (k) {
            boolean disparoExitoso = rdp.disparar(transicion);

            if (disparoExitoso) {
                Set<String> sensibilizadas = rdp.getTransicionesSensibilizadas();
                Set<String> esperando = colas.getTransicionesEspera();

                // Transiciones sensibilizadas y con hilos bloqueados
                Set<String> proximasTrancisiones = sensibilizadas.stream()
                        .filter(t -> esperando.contains(t))
                        .collect(Collectors.toSet());

                // Si hay transiciones que se pueden disparar con hilos bloqueados
                if (proximasTrancisiones.size() > 0) {
                    String proximaTransicion = politica.getProximaTransicion(proximasTrancisiones);

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
}
