package testes;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dominio.PrevisaoComparacao;
import dominio.PrevisaoMediaMovel;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;
import servico.FabricaPrevisao;

class FabricaPrevisaoTest {

    @Test
    void deveCriarMediaMovel() throws DadosInvalidosException {
        // Arrange + Act
        Object estrategia = FabricaPrevisao.criar("MEDIA");

        // Assert
        assertInstanceOf(PrevisaoMediaMovel.class, estrategia);
    }

    @Test
    void deveCriarComparacaoIgnorandoMaiusculasEEspacos() throws DadosInvalidosException {
        // Arrange + Act
        Object estrategia = FabricaPrevisao.criar("  comparacao ");

        // Assert
        assertInstanceOf(PrevisaoComparacao.class, estrategia);
    }

    @Test
    void deveRecusarTipoDesconhecido() {
        // Arrange + Act + Assert
        assertThrows(DadosInvalidosException.class, () -> FabricaPrevisao.criar("ADIVINHACAO"));
    }
}
