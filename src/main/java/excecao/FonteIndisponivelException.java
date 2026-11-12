package excecao;

/**
 * Lançada quando uma fonte de dados falha: sensor fora do ar, banco de dados
 * inacessível, arquivo que não pôde ser gravado...
 */
public class FonteIndisponivelException extends Exception {

    public FonteIndisponivelException(String mensagem) {
        super(mensagem);
    }

    public FonteIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
