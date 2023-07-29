import Importador.IImportador;
import Importador.ImportadorFactory;
import Importador.TipoImportador;
import Main.Monitor;
import Main.Rdp;
import Politicas.PoliticaPrimera;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        ImportadorFactory importadorFactory = new ImportadorFactory();
        IImportador importador = importadorFactory.getImportador(TipoImportador.PIPE);

        Rdp rdp = importador.importar("./RedesDePetri/Red de petri sin deadlock.xml");
        Monitor monitor = new Monitor(rdp, new PoliticaPrimera());
        monitor.dispararTransicion("T1");
        System.out.println("Transicion T1 Disparada");
        monitor.dispararTransicion("T2");
        System.out.println("Transicion T2 Disparada");
        monitor.dispararTransicion("T5");
        System.out.println("Transicion T5 Disparada");
    }
}