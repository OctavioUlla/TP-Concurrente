import Importador.IImportador;
import Importador.ImportadorFactory;
import Importador.TipoImportador;
import Main.Rdp;

public class Main {

    public static void main(String[] args) {
        ImportadorFactory importadorFactory = new ImportadorFactory();
        IImportador importador = importadorFactory.GetImportador(TipoImportador.PIPE);

        Rdp rdp = importador.Importar("./RedesDePetri/Red de petri sin deadlock.xml");
    }
}