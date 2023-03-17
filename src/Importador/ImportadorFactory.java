package Importador;

public class ImportadorFactory {

    public IImportador getImportador(TipoImportador tipo) {
        switch (tipo) {
            case PIPE:
                return new ImportadorPIPE();
            case PETRINATOR:
                throw new UnsupportedOperationException("Importador para petrinator no implementado");
            default:
                return new ImportadorPIPE();
        }
    }
}