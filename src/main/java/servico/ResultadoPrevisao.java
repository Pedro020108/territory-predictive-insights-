package servico;

import dominio.Alerta;

/**
 * Resultado de uma previsão: o valor previsto, o que ele significa e, se houve,
 * o alerta emitido (null quando a previsão ficou abaixo de todos os limiares).
 */
public record ResultadoPrevisao(String tipoSensor,
                                String estrategia,
                                double valorPrevisto,
                                String unidade,
                                String explicacao,
                                Alerta alerta) {

    public boolean geraAlerta() {
        return alerta != null;
    }
}
