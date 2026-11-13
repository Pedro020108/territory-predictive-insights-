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
        // Arrange
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel();
        List<Double> valores = Arrays.asList(10.0, 20.0, 30.0, 40.0, 50.0);

        // Act
        double resultado = previsao.prever(valores);

        // Assert
        assertEquals(30.0, resultado, 0.0001);
    }

    @Test
    void deveUsarApenasAsCincoLeiturasMaisRecentes() throws DadosInvalidosException {
        // Arrange: a primeira leitura (100) está fora da janela
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel();
        List<Double> valores = Arrays.asList(100.0, 10.0, 20.0, 30.0, 40.0, 50.0);

        // Act
        double resultado = previsao.prever(valores);

        // Assert
        assertEquals(30.0, resultado, 0.0001);
    }

    @Test
    void deveSeCalarQuandoHaMenosDeCincoLeituras() {
        // Arrange
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel();
        List<Double> poucas = Arrays.asList(10.0, 20.0, 30.0);

        // Act
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> previsao.prever(poucas));

        // Assert
        assertTrue(erro.getMessage().contains("insuficiente"));
    }

    @Test
    void deveRecusarHistoricoComLeituraNegativa() {
        // Arrange
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel();
        List<Double> valores = Arrays.asList(10.0, 20.0, -1.0, 40.0, 50.0);

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> previsao.prever(valores));
    }
}
