package Main;

import java.util.concurrent.Semaphore;

public class Monitor {
    private final Rdp rdp;
    private final Colas colas;
    private final Semaphore mutex = new Semaphore(1, false);

    public Monitor(Rdp redDePetri) {
        rdp = redDePetri;
        colas = new Colas(rdp.getTrancisiones());
    }
}
