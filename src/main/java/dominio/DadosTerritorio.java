package dominio;

import excecao.DadosInvalidosException;
import java.util.Optional;


public class DadosTerritorio {

    // Código simples da disciplina. Em um sistema real, ficaria fora do código-fonte.
    private static final String CODIGO_ACESSO = "1234";

    private final String tipoSensor;
    private final String unidade;
    private double limiteAtencao;
    private double limiteAlerta;
    private double limiteCritico;

    public DadosTerritorio(String tipoSensor, String unidade,
                           double limiteAtencao, double limiteAlerta, double limiteCritico)
            throws DadosInvalidosException {
        if (tipoSensor == null || tipoSensor.isBlank()) {
            throw new DadosInvalidosException("O tipo do sensor dos limiares não pode ser vazio.");
        }
        validarLimiares(limiteAtencao, limiteAlerta, limiteCritico);
        this.tipoSensor = tipoSensor;
        this.unidade = unidade;
        this.limiteAtencao = limiteAtencao;
        this.limiteAlerta = limiteAlerta;
        this.limiteCritico = limiteCritico;
    }

    public void alterarLimiares(String codigoAcesso, double novoAtencao, double novoAlerta, double novoCritico)
            throws DadosInvalidosException {
        if (!CODIGO_ACESSO.equals(codigoAcesso)) {
            throw new DadosInvalidosException("Código de acesso incorreto: os limiares não foram alterados.");
        }
        validarLimiares(novoAtencao, novoAlerta, novoCritico);
        this.limiteAtencao = novoAtencao;
        this.limiteAlerta = novoAlerta;
        this.limiteCritico = novoCritico;
    }

    private static void validarLimiares(double atencao, double alerta, double critico)
            throws DadosInvalidosException {
        if (Double.isNaN(atencao) || Double.isNaN(alerta) || Double.isNaN(critico)
                || atencao <= 0 || alerta <= 0 || critico <= 0) {
            throw new DadosInvalidosException("Limiar inválido: todos os limites devem ser números maiores que zero.");
        }
        if (!(atencao < alerta && alerta < critico)) {
            throw new DadosInvalidosException("Limiar inválido: é preciso ATENÇÃO < ALERTA < CRÍTICO (recebido "
                    + Formato.numero(atencao) + " / " + Formato.numero(alerta) + " / "
                    + Formato.numero(critico) + ").");
        }
    }

    public Optional<NivelAlerta> classificar(double valor) {
        if (valor >= limiteCritico) {
            return Optional.of(NivelAlerta.CRITICO);
        }
        if (valor >= limiteAlerta) {
            return Optional.of(NivelAlerta.ALERTA);
        }
        if (valor >= limiteAtencao) {
            return Optional.of(NivelAlerta.ATENCAO);
        }
        return Optional.empty();
    }

    public double getLimite(NivelAlerta nivel) {
        return switch (nivel) {
            case ATENCAO -> limiteAtencao;
            case ALERTA -> limiteAlerta;
            case CRITICO -> limiteCritico;
        };
    }

    public String getTipoSensor() {
        return tipoSensor;
    }

    public String getUnidade() {
        return unidade;
    }

    public double getLimiteAtencao() {
        return limiteAtencao;
    }

    public double getLimiteAlerta() {
        return limiteAlerta;
    }

    public double getLimiteCritico() {
        return limiteCritico;
    }

    @Override
    public String toString() {
        return tipoSensor + ": ATENÇÃO >= " + Formato.numero(limiteAtencao) + " " + unidade
                + " | ALERTA >= " + Formato.numero(limiteAlerta) + " " + unidade
                + " | CRÍTICO >= " + Formato.numero(limiteCritico) + " " + unidade;
    }
}
