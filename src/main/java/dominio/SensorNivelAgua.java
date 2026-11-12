package dominio;

import excecao.FonteIndisponivelException;
import java.util.Random;

public class SensorNivelAgua implements Sensor {

    public static final String TIPO = "NIVEL_AGUA";
    private static final double MAXIMO = 50.0;

    private final Random random;
    private boolean funcionando = true;

    public SensorNivelAgua() {
        this(new Random());
    }

    public SensorNivelAgua(Random random) {
        this.random = random;
    }

    @Override
    public double lerValor() throws FonteIndisponivelException {
        if (!funcionando) {
            throw new FonteIndisponivelException("Sensor de nível da água indisponível: falha de leitura.");
        }
        double sorteio = random.nextDouble();
        return Math.round(sorteio * sorteio * MAXIMO * 10.0) / 10.0;
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
        return "cm";
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
