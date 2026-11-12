package excecao;

/**
 * Lançada quando um dado não pode ser aceito: medição negativa ou fora de faixa,
 * população negativa, limiar inválido, código de acesso errado, histórico insuficiente...
 */
public class DadosInvalidosException extends Exception {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
