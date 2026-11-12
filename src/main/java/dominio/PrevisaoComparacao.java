package dominio;

import excecao.DadosInvalidosException;
import java.util.List;


public class PrevisaoComparacao extends EstrategiaPrevisao {

    public PrevisaoComparacao() {
        super("Comparação com o período anterior (" + PrevisaoMediaMovel.JANELA + " x "
                + PrevisaoMediaMovel.JANELA + " leituras)", 2 * PrevisaoMediaMovel.JANELA);
    }

    @Override
    public double prever(List<Double> valores) throws DadosInvalidosException {
        super.validarHistorico(valores);
        double atual = mediaAtual(valores);
        double anterior = mediaAnterior(valores);
        // Chuva, nível e umidade nunca são negativos.
        return Math.max(0.0, atual + (atual - anterior));
    }


    public double calcularVariacaoPercentual(List<Double> valores) throws DadosInvalidosException {
        super.validarHistorico(valores);
        double atual = mediaAtual(valores);
        double anterior = mediaAnterior(valores);
        if (anterior == 0) {
            return atual == 0 ? 0.0 : Double.NaN;
        }
        return (atual - anterior) / anterior * 100.0;
    }

    @Override
    public String descrever(List<Double> valores, double valorPrevisto, String unidade)
            throws DadosInvalidosException {
        double variacao = calcularVariacaoPercentual(valores);
        String comparacao;
        if (Double.isNaN(variacao)) {
            comparacao = "O período anterior teve média zero, então a variação em % não pode ser calculada";
        } else {
            comparacao = "Variação de " + (variacao >= 0 ? "+" : "") + Formato.numero(variacao)
                    + "% em relação ao período anterior";
        }
        return comparacao + " (média atual " + Formato.numero(mediaAtual(valores)) + " " + unidade
                + " contra " + Formato.numero(mediaAnterior(valores)) + " " + unidade
                + "). Se a tendência continuar, o próximo período chega a " + Formato.numero(valorPrevisto)
                + " " + unidade + ".";
    }

    private double mediaAtual(List<Double> valores) {
        int n = valores.size();
        return calcularMedia(valores, n - PrevisaoMediaMovel.JANELA, n);
    }

    private double mediaAnterior(List<Double> valores) {
        int n = valores.size();
        return calcularMedia(valores, n - 2 * PrevisaoMediaMovel.JANELA, n - PrevisaoMediaMovel.JANELA);
    }
}
