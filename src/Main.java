import Importador.IImportador;
import Importador.ImportadorPIPE;
import Main.Rdp;

public class Main {

    public static void main(String[] args) {
        IImportador importador = new ImportadorPIPE();

        Rdp rdp = importador.Importar("./RedesDePetri/Red de petri sin deadlock.xml");
    }
}