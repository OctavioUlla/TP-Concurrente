package Main;

public class Temporizacion {
    private final long alpha;
    private final long beta;

    public Temporizacion(long alpha, long beta) {
        this.alpha = alpha;
        this.beta = beta;
    }

    public boolean isEnVentana(long timeStamp) {
        return alpha <= timeStamp && timeStamp <= beta;
    }
}
