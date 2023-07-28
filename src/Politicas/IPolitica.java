package Politicas;

import java.util.List;

public interface IPolitica {
    String getProximaTransicion(List<String> transicionesSensibilizadas);
}
