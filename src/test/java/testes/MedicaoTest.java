package testes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dominio.Medicao;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

class MedicaoTest {

    @Test
    void deveCriarMedicaoValida() throws DadosInvalidosException {
        // Arrange
        String tipo = "PLUVIOMETRO";
        double valor = 12.5;

        // Act
        Medicao medicao = new Medicao(tipo, valor);

        // Assert
        assertEquals("PLUVIOMETRO", medicao.getTipoSensor());
        assertEquals(12.5, medicao.getValor(), 0.0001);
        assertNotNull(medicao.getDataHora());
    }

    @Test
    void deveLancarDadosInvalidosParaValorNegativo() {
        // Arrange
        double valorNegativo = -3.0;

        // Act
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> new Medicao("PLUVIOMETRO", valorNegativo));

        // Assert
        assertTrue(erro.getMessage().contains("negativo"));
    }

    @Test
    void deveLancarDadosInvalidosParaValorNaoNumerico() {
        // Arrange + Act + Assert
        assertThrows(DadosInvalidosException.class, () -> new Medicao("PLUVIOMETRO", Double.NaN));
    }

    @Test
    void deveLancarDadosInvalidosParaTipoDeSensorVazio() {
        // Arrange + Act + Assert
        assertThrows(DadosInvalidosException.class, () -> new Medicao("  ", 10.0));
    }
}
