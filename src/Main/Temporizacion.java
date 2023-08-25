package Main;

public class Temporizacion {
    private final long alpha;
    private final long beta;
    private long timeStamp;

    public Temporizacion(long alpha, long beta) {
        this.alpha = alpha;
        this.beta = beta;
        timeStamp = 0;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getEspera() {
        return Math.max(0, timeStamp + alpha - System.currentTimeMillis());
    }

    public boolean isEnVentana() {
        long timeStampActual = System.currentTimeMillis();

        return timeStamp + alpha <= timeStampActual
                && timeStampActual <= timeStamp + beta;
    }
}
