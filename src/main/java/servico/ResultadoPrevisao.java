package servico;

import dominio.Alerta;

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
