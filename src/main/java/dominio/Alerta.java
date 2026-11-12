package dominio;

import excecao.DadosInvalidosException;
import java.time.LocalDateTime;


public class Alerta {

    private long id;
    private String tipoSensor;
    private double valorPrevisto;
    private NivelAlerta nivel;
    private String mensagem;
    private LocalDateTime dataHora;

    public Alerta(String tipoSensor, double valorPrevisto, NivelAlerta nivel, String mensagem)
            throws DadosInvalidosException {
        this(0, tipoSensor, valorPrevisto, nivel, mensagem, LocalDateTime.now());
    }

    public Alerta(long id, String tipoSensor, double valorPrevisto, NivelAlerta nivel,
                  String mensagem, LocalDateTime dataHora) throws DadosInvalidosException {
        if (tipoSensor == null || tipoSensor.isBlank()) {
            throw new DadosInvalidosException("Alerta inválido: o tipo do sensor não pode ser vazio.");
        }
        if (Double.isNaN(valorPrevisto) || valorPrevisto < 0) {
            throw new DadosInvalidosException("Alerta inválido: o valor previsto não pode ser negativo.");
        }
        if (nivel == null) {
            throw new DadosInvalidosException("Alerta inválido: o nível do alerta é obrigatório.");
        }
        if (mensagem == null || mensagem.isBlank()) {
            throw new DadosInvalidosException("Alerta inválido: todo alerta precisa explicar o motivo.");
        }
        if (dataHora == null) {
            throw new DadosInvalidosException("Alerta inválido: a data e hora são obrigatórias.");
        }
        this.id = id;
        this.tipoSensor = tipoSensor.trim();
        this.valorPrevisto = valorPrevisto;
        this.nivel = nivel;
        this.mensagem = mensagem;
        this.dataHora = dataHora;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTipoSensor() {
        return tipoSensor;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public NivelAlerta getNivel() {
        return nivel;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public String toString() {
        return "[" + Formato.dataHora(dataHora) + "] " + mensagem;
    }
}
