package Politicas;

import java.util.List;

public class Politica implements IPolitica {

    @Override
    public String getProximaTransicion(List<String> transicionesSensibilizadas) {
        return transicionesSensibilizadas.get(0);
    }

}
