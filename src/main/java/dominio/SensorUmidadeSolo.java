package dominio;

import excecao.FonteIndisponivelException;
import java.util.Random;

public class SensorUmidadeSolo implements Sensor {

    public static final String TIPO = "UMIDADE_SOLO";
    private static final double MAXIMO = 100.0;

    private final Random random;
    private boolean funcionando = true;

    public SensorUmidadeSolo() {
        this(new Random());
    }

    public SensorUmidadeSolo(Random random) {
        this.random = random;
    }

    @Override
    public double lerValor() throws FonteIndisponivelException {
        if (!funcionando) {
            throw new FonteIndisponivelException("Sensor de umidade do solo indisponível: bateria descarregada.");
        }
        // Solo raramente fica abaixo de 30% de umidade: sorteamos entre 30 e 100.
        return Math.round((30.0 + random.nextDouble() * 70.0) * 10.0) / 10.0;
    }

    @Override
    public boolean verificarFuncionamento() {
        return funcionando;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }

    @Override
    public String getUnidade() {
        return "%";
    }

    @Override
    public double getValorMaximo() {
        return MAXIMO;
    }

    @Override
    public void setFuncionando(boolean funcionando) {
        this.funcionando = funcionando;
    }
}
