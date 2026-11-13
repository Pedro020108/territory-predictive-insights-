package testes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dominio.Pluviometro;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servico.ConfiguracaoSistema;

class ConfiguracaoSistemaTest {

    @BeforeEach
    void restaurar() {
        ConfiguracaoSistema.getInstancia().restaurarPadroes();
    }

    @Test
    void deveDevolverSempreAMesmaInstancia() {
        // Arrange + Act
        ConfiguracaoSistema primeira = ConfiguracaoSistema.getInstancia();
        ConfiguracaoSistema segunda = ConfiguracaoSistema.getInstancia();

        // Assert
        assertSame(primeira, segunda);
    }

    @Test
    void deveManterLimiarAlteradoEmTodoOSistema() throws DadosInvalidosException {
        // Arrange
        ConfiguracaoSistema.getInstancia().getDados(Pluviometro.TIPO).alterarLimiares("1234", 5.0, 10.0, 20.0);

        // Act: outra "chamada" qualquer enxerga o mesmo valor
        double atencao = ConfiguracaoSistema.getInstancia().getDados(Pluviometro.TIPO).getLimiteAtencao();

        // Assert
        assertEquals(5.0, atencao, 0.0001);
    }

    @Test
    void deveRecusarSensorSemLimiaresConfigurados() {
        // Arrange + Act + Assert
        assertThrows(DadosInvalidosException.class, () -> ConfiguracaoSistema.getInstancia().getDados("RADAR"));
    }
}
