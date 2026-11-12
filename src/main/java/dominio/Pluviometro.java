package dominio;

import excecao.FonteIndisponivelException;
import java.util.Random;

public class Pluviometro implements Sensor {

    public static final String TIPO = "PLUVIOMETRO";
    private static final double MAXIMO = 80.0;

    private final Random random;
    private boolean funcionando = true;

    public Pluviometro() {
        this(new Random());
    }

    public Pluviometro(Random random) {
        this.random = random;
    }

    @Override
    public double lerValor() throws FonteIndisponivelException {
        if (!funcionando) {
            throw new FonteIndisponivelException("Pluviômetro indisponível: sem comunicação com o equipamento.");
        }
        double sorteio = random.nextDouble();
        // Elevar ao quadrado faz chuvas fracas serem mais comuns que chuvas fortes.
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
        return "mm";
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
