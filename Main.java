public class Main {

    public static void main(String[] args) {
        IImportador importador = new ImportadorPIPE();

        importador.Importar("RedesDePetri/Red de petri.xml");
    }
}