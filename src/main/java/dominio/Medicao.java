package dominio;

import excecao.DadosInvalidosException;
import java.time.LocalDateTime;

public class Medicao {

    private long id;
    private String tipoSensor;
    private double valor;
    private LocalDateTime dataHora;

    public Medicao(String tipoSensor, double valor) throws DadosInvalidosException {
        this(0, tipoSensor, valor, LocalDateTime.now());
    }

    public Medicao(long id, String tipoSensor, double valor, LocalDateTime dataHora)
            throws DadosInvalidosException {
        this.id = id;
        setTipoSensor(tipoSensor);
        setValor(valor);
        setDataHora(dataHora);
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

    public void setTipoSensor(String tipoSensor) throws DadosInvalidosException {
        if (tipoSensor == null || tipoSensor.isBlank()) {
            throw new DadosInvalidosException("Medição inválida: o tipo do sensor não pode ser vazio.");
        }
        this.tipoSensor = tipoSensor.trim();
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) throws DadosInvalidosException {
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new DadosInvalidosException("Medição inválida: o valor lido não é um número.");
        }
        if (valor < 0) {
            throw new DadosInvalidosException(
                    "Medição inválida: valor negativo (" + Formato.numero(valor) + ") não é aceito.");
        }
        this.valor = valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) throws DadosInvalidosException {
        if (dataHora == null) {
            throw new DadosInvalidosException("Medição inválida: a data e hora não podem ser vazias.");
        }
        this.dataHora = dataHora;
    }

    @Override
    public String toString() {
        return "[" + Formato.dataHora(dataHora) + "] " + tipoSensor + " = " + Formato.numero(valor);
    }
}
