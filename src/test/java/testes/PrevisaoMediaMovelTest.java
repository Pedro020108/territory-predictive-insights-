package testes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dominio.PrevisaoMediaMovel;
import excecao.DadosInvalidosException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class PrevisaoMediaMovelTest {

    @Test
    void deveCalcularMediaDasCincoLeituras() throws DadosInvalidosException {
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel();
        List<Double> valores = Arrays.asList(10.0, 20.0, 30.0, 40.0, 50.0);

        double resultado = previsao.prever(valores);

        assertEquals(30.0, resultado, 0.0001);
    }

    @Test
    void deveUsarApenasAsCincoLeiturasMaisRecentes() throws DadosInvalidosException {
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel();
        List<Double> valores = Arrays.asList(100.0, 10.0, 20.0, 30.0, 40.0, 50.0);

        double resultado = previsao.prever(valores);

        assertEquals(30.0, resultado, 0.0001);
    }

    @Test
    void deveSeCalarQuandoHaMenosDeCincoLeituras() {
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel();
        List<Double> poucas = Arrays.asList(10.0, 20.0, 30.0);

        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> previsao.prever(poucas));

        assertTrue(erro.getMessage().contains("insuficiente"));
    }

    @Test
    void deveRecusarHistoricoComLeituraNegativa() {
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel();
        List<Double> valores = Arrays.asList(10.0, 20.0, -1.0, 40.0, 50.0);

        assertThrows(DadosInvalidosException.class, () -> previsao.prever(valores));
    }
}
