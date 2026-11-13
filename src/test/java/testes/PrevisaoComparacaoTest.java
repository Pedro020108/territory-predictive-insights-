package testes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dominio.PrevisaoComparacao;
import excecao.DadosInvalidosException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class PrevisaoComparacaoTest {

    private final List<Double> dobrou = Arrays.asList(10.0, 10.0, 10.0, 10.0, 10.0, 20.0, 20.0, 20.0, 20.0, 20.0);

    @Test
    void deveCalcularAumentoDeCemPorCento() throws DadosInvalidosException {
        // Arrange
        PrevisaoComparacao previsao = new PrevisaoComparacao();

        // Act
        double variacao = previsao.calcularVariacaoPercentual(dobrou);

        // Assert
        assertEquals(100.0, variacao, 0.0001);
    }

    @Test
    void devePreverContinuidadeDaTendenciaDeAlta() throws DadosInvalidosException {
        // Arrange: média anterior 10, média atual 20 -> tendência de +10 -> 30
        PrevisaoComparacao previsao = new PrevisaoComparacao();

        // Act
        double previsto = previsao.prever(dobrou);

        // Assert
        assertEquals(30.0, previsto, 0.0001);
    }

    @Test
    void deveCalcularQuedaENaoPreverValorNegativo() throws DadosInvalidosException {
        // Arrange: média anterior 20, média atual 5 -> 5 + (5 - 20) = -10, vira 0
        PrevisaoComparacao previsao = new PrevisaoComparacao();
        List<Double> caiu = Arrays.asList(20.0, 20.0, 20.0, 20.0, 20.0, 5.0, 5.0, 5.0, 5.0, 5.0);

        // Act
        double variacao = previsao.calcularVariacaoPercentual(caiu);
        double previsto = previsao.prever(caiu);

        // Assert
        assertEquals(-75.0, variacao, 0.0001);
        assertEquals(0.0, previsto, 0.0001);
    }

    @Test
    void deveInformarQuandoPeriodoAnteriorTeveMediaZero() throws DadosInvalidosException {
        // Arrange
        PrevisaoComparacao previsao = new PrevisaoComparacao();
        List<Double> semBase = Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 10.0, 10.0, 10.0, 10.0, 10.0);

        // Act
        double variacao = previsao.calcularVariacaoPercentual(semBase);

        // Assert
        assertTrue(Double.isNaN(variacao));
    }

    @Test
    void deveSeCalarQuandoHaMenosDeDezLeituras() {
        // Arrange
        PrevisaoComparacao previsao = new PrevisaoComparacao();
        List<Double> nove = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0);

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> previsao.prever(nove));
    }
}
