package Politicas;

import java.util.Random;
import java.util.Set;

public class PoliticaRandom implements IPolitica {

    Random random = new Random();

    @Override
    public String getProximaTransicion(Set<String> transicionesSensibilizadas) {
        return transicionesSensibilizadas.stream()
                .skip(random.nextInt(transicionesSensibilizadas.size()))
                .findFirst()
                .get();
    }

}
