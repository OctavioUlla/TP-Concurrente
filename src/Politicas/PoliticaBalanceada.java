package Politicas;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import Main.Estadistica;

public class PoliticaBalanceada implements IPolitica {

    private final Estadistica estadistica;

    public PoliticaBalanceada(Estadistica estadistica) {
        this.estadistica = estadistica;
    }

    @Override
    public String getProximaTransicion(Set<String> transicionesSensibilizadas) {
        return estadistica.getTInvariantesCount().entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .flatMap(entry -> entry.getKey().stream())
                .filter(t -> transicionesSensibilizadas.contains(t))
                .findFirst()
                .get();
    }

}
