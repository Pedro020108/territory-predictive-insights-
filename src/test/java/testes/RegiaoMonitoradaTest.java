package testes;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dominio.Pluviometro;
import dominio.RegiaoMonitorada;
import dominio.SensorNivelAgua;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

class RegiaoMonitoradaTest {

    @Test
    void deveInformarOperacionalQuandoTodosOsSensoresFuncionam() throws DadosInvalidosException {
        RegiaoMonitorada regiao = new RegiaoMonitorada(1, "Baixada", "Zona Sul", 5000);
        regiao.adicionarSensor(new Pluviometro());
        regiao.adicionarSensor(new SensorNivelAgua());

        String status = regiao.verificarStatus();

        assertTrue(status.startsWith("OPERACIONAL"));
    }

    @Test
    void deveInformarDegradadoQuandoUmSensorEstaComDefeito() throws DadosInvalidosException {
        RegiaoMonitorada regiao = new RegiaoMonitorada(1, "Baixada", "Zona Sul", 5000);
        Pluviometro pluviometro = new Pluviometro();
        regiao.adicionarSensor(pluviometro);
        regiao.adicionarSensor(new SensorNivelAgua());
        pluviometro.setFuncionando(false);

        String status = regiao.verificarStatus();

        assertTrue(status.startsWith("DEGRADADO"));
    }

    @Test
    void deveRecusarPopulacaoNegativa() {
        assertThrows(DadosInvalidosException.class, () -> new RegiaoMonitorada(1, "Baixada", "Zona Sul", -10));
    }
}
