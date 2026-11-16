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
        ConfiguracaoSistema primeira = ConfiguracaoSistema.getInstancia();
        ConfiguracaoSistema segunda = ConfiguracaoSistema.getInstancia();

        assertSame(primeira, segunda);
    }

    @Test
    void deveManterLimiarAlteradoEmTodoOSistema() throws DadosInvalidosException {
        ConfiguracaoSistema.getInstancia().getDados(Pluviometro.TIPO).alterarLimiares("1234", 5.0, 10.0, 20.0);

        double atencao = ConfiguracaoSistema.getInstancia().getDados(Pluviometro.TIPO).getLimiteAtencao();

        assertEquals(5.0, atencao, 0.0001);
    }

    @Test
    void deveRecusarSensorSemLimiaresConfigurados() {
        assertThrows(DadosInvalidosException.class, () -> ConfiguracaoSistema.getInstancia().getDados("RADAR"));
    }
}
