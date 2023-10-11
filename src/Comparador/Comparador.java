package Comparador;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Importadores.IImportador;
import Importadores.ImportadorPetrinator;
import Main.Rdp;
import Main.SegmentoEjecucion;
import Main.AnalizadorRdp;

public class Comparador {

        public static void main(String[] args) {
                IImportador importador = new ImportadorPetrinator();

                Rdp rdpConDeadlock = importador.importar("./RedesDePetri/Red de petri.pflow");

                Rdp rdpSinDeadlock = importador.importar("./RedesDePetri/Red de petri sin deadlock temporal.pflow");

                System.out.println("Red de Petri Sin desbloquear:");
                analizar(rdpConDeadlock);

                System.out.println("\nRed de Petri Desbloqueada:");
                analizar(rdpSinDeadlock);
        }

        public static void analizar(Rdp rdp) {
                HashSet<Map<String, Integer>> marcados = new HashSet<Map<String, Integer>>();

                List<Set<String>> tInvariantes = AnalizadorRdp.getTInvariantes(rdp);
                List<Set<String>> pInvariantes = AnalizadorRdp.getPInvariantes(rdp);
                Set<String> plazasAccion = AnalizadorRdp.getPlazasAccion(rdp);

                System.out.println("T Invariantes: " + tInvariantes);
                System.out.println("P Invariantes: " + pInvariantes);
                System.out.println("Plazas de Acción: " + plazasAccion);

                AnalizadorRdp.getAllMarcados(rdp, rdp.getMarcado(), marcados);

                Map<String, Double> promedioPlazaAccion = AnalizadorRdp.getPromedioPlazas(marcados, plazasAccion);
                double promedioGeneral = AnalizadorRdp.getPromedioGeneral(promedioPlazaAccion);
                int maxHilosActivos = AnalizadorRdp.getMaxHilosActivos(marcados, plazasAccion);

                List<SegmentoEjecucion> segmentos = SegmentoEjecucion.getSegmentosEjecucion(rdp);

                boolean pInvariantesVerificados = AnalizadorRdp.verificarPInvariantes(marcados, pInvariantes);

                System.out.println("PInvariantes verifican: " + pInvariantesVerificados);
                System.out.println("Cantidad marcados posibles: " + marcados.size());
                System.out.println("Promedio tokens en cada plaza de acción: " + promedioPlazaAccion);
                System.out.println("Promedio tokens en plazas de acción: " + promedioGeneral);
                System.out.println("Max cantidad hilos activos: " + maxHilosActivos);
                System.out.println("Segmentos: " + segmentos);
        }
}