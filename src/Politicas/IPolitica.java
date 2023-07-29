package Politicas;

import java.util.Set;

public interface IPolitica {
    String getProximaTransicion(Set<String> transicionesSensibilizadas);
}
