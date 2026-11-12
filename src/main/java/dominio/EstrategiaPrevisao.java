package dominio;

import excecao.DadosInvalidosException;
import java.util.List;


public abstract class EstrategiaPrevisao {

    private final String nome;
    private final int quantidadeNecessaria;

    protected EstrategiaPrevisao(String nome, int quantidadeNecessaria) {
        this.nome = nome;
        this.quantidadeNecessaria = quantidadeNecessaria;
    }

    public String getNome() {
        return nome;
    }

    public int getQuantidadeNecessaria() {
        return quantidadeNecessaria;
    }

    public abstract double prever(List<Double> valores) throws DadosInvalidosException;

    public abstract String descrever(List<Double> valores, double valorPrevisto, String unidade)
            throws DadosInvalidosException;

    protected void validarHistorico(List<Double> valores) throws DadosInvalidosException {
        int disponiveis = (valores == null) ? 0 : valores.size();
        if (disponiveis < quantidadeNecessaria) {
            throw new DadosInvalidosException("Histórico insuficiente para '" + nome + "': são necessárias "
                    + quantidadeNecessaria + " leituras e há " + disponiveis
                    + ". Previsão não emitida para não passar um chute como informação.");
        }
        for (Double valor : valores) {
            if (valor == null || valor.isNaN() || valor < 0) {
                throw new DadosInvalidosException("Histórico com leitura inválida: a previsão '" + nome
                        + "' não pode ser calculada.");
            }
        }
    }

    protected static double calcularMedia(List<Double> valores, int de, int ate) {
        double soma = 0;
        for (int i = de; i < ate; i++) {
            soma += valores.get(i);
        }
        return soma / (ate - de);
    }
}
