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
        Set<String> tInvariantePrioridad = estadistica.getTInvariantesCount().entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey())
                .findFirst()
                .get();

        return transicionesSensibilizadas.stream()
                .sorted(Comparator.comparingInt(x -> tInvariantePrioridad.contains(x) ? 0 : 1))
                .findFirst()
                .get();
    }

}
