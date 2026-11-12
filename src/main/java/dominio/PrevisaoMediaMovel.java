package dominio;

import excecao.DadosInvalidosException;
import java.util.List;

public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    public static final int JANELA = 5;

    public PrevisaoMediaMovel() {
        super("Média móvel (últimas " + JANELA + " leituras)", JANELA);
    }

    @Override
    public double prever(List<Double> valores) throws DadosInvalidosException {
        super.validarHistorico(valores);
        return calcularMedia(valores, valores.size() - JANELA, valores.size());
    }

    @Override
    public String descrever(List<Double> valores, double valorPrevisto, String unidade)
            throws DadosInvalidosException {
        super.validarHistorico(valores);
        return "Média das últimas " + JANELA + " leituras: " + Formato.numero(valorPrevisto) + " " + unidade
                + ". Se o comportamento recente continuar, é esse o nível esperado.";
    }
}
