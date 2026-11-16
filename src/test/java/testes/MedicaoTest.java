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
        String tipo = "PLUVIOMETRO";
        double valor = 12.5;

        Medicao medicao = new Medicao(tipo, valor);

        assertEquals("PLUVIOMETRO", medicao.getTipoSensor());
        assertEquals(12.5, medicao.getValor(), 0.0001);
        assertNotNull(medicao.getDataHora());
    }

    @Test
    void deveLancarDadosInvalidosParaValorNegativo() {
        double valorNegativo = -3.0;

        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> new Medicao("PLUVIOMETRO", valorNegativo));

        assertTrue(erro.getMessage().contains("negativo"));
    }

    @Test
    void deveLancarDadosInvalidosParaValorNaoNumerico() {
        assertThrows(DadosInvalidosException.class, () -> new Medicao("PLUVIOMETRO", Double.NaN));
    }

    @Test
    void deveLancarDadosInvalidosParaTipoDeSensorVazio() {
        assertThrows(DadosInvalidosException.class, () -> new Medicao("  ", 10.0));
    }
}
