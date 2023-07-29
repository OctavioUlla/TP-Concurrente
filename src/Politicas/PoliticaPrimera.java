package Politicas;

import java.util.Set;

public class PoliticaPrimera implements IPolitica {

    @Override
    public String getProximaTransicion(Set<String> transicionesSensibilizadas) {
        return transicionesSensibilizadas.iterator().next();
    }

}
