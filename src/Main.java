import Importador.IImportador;
import Importador.ImportadorFactory;
import Importador.TipoImportador;
import Main.Rdp;

public class Main {

    public static void main(String[] args) {
        ImportadorFactory importadorFactory = new ImportadorFactory();
        IImportador importador = importadorFactory.getImportador(TipoImportador.PIPE);

        Rdp rdp = importador.importar("./RedesDePetri/Red de petri sin deadlock.xml");
        rdp.disparar("T1");
    }
}