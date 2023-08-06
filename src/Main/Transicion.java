package Main;

public class Transicion implements Comparable<Transicion> {
    private final String nombre;
    private final boolean temporal;
    private final long alpha;
    private final long beta;

    public Transicion(String nombre) {
        this.nombre = nombre;
        this.temporal = false;
        this.alpha = 0;
        this.beta = 0;
    }

    public Transicion(String nombre, long alpha, long beta) {
        this.nombre = nombre;
        this.temporal = true;
        this.alpha = alpha;
        this.beta = beta;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isTemporal() {
        return temporal;
    }

    public boolean isEnVentana(long timeStamp) {
        return alpha <= timeStamp && timeStamp <= beta;
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public int compareTo(Transicion t) {
        // Ordenar por orden alfabetico de nombre
        return nombre.compareTo(t.getNombre());
    }
}
