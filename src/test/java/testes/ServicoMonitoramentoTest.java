package testes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dominio.Alerta;
import dominio.Medicao;
import dominio.NivelAlerta;
import dominio.PrevisaoComparacao;
import dominio.PrevisaoMediaMovel;
import dominio.Sensor;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import persistencia.AlertaDAO;
import persistencia.MedicaoDAO;
import servico.ConfiguracaoSistema;
import servico.ResultadoPrevisao;
import servico.ServicoMonitoramento;

class ServicoMonitoramentoTest {

    private MedicaoDAO medicaoDAO;
    private AlertaDAO alertaDAO;
    private Sensor pluviometro;
    private ServicoMonitoramento servico;

    @BeforeEach
    void preparar() {
        ConfiguracaoSistema.getInstancia().restaurarPadroes(); // limiares: 15 / 35 / 55 mm

        medicaoDAO = mock(MedicaoDAO.class);
        alertaDAO = mock(AlertaDAO.class);
        pluviometro = mock(Sensor.class);
        when(pluviometro.getTipo()).thenReturn("PLUVIOMETRO");
        when(pluviometro.getUnidade()).thenReturn("mm");
        when(pluviometro.getValorMaximo()).thenReturn(80.0);

        servico = new ServicoMonitoramento(medicaoDAO, alertaDAO, List.of(pluviometro));
    }

    private List<Medicao> historico(double... valores) throws DadosInvalidosException {
        List<Medicao> lista = new ArrayList<>();
        for (double valor : valores) {
            lista.add(new Medicao("PLUVIOMETRO", valor));
        }
        return lista;
    }

    @Test
    void deveEmitirAlertaQuandoPrevisaoAtingeLimiteDeAlerta() throws Exception {
        when(medicaoDAO.buscarUltimas("PLUVIOMETRO", 5)).thenReturn(historico(40, 40, 40, 40, 40));

        ResultadoPrevisao resultado = servico.gerarPrevisao("PLUVIOMETRO", new PrevisaoMediaMovel());

        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaDAO).salvar(captor.capture());
        Alerta gravado = captor.getValue();
        assertEquals(NivelAlerta.ALERTA, gravado.getNivel());
        assertEquals(40.0, gravado.getValorPrevisto(), 0.0001);
        assertTrue(gravado.getMensagem().contains("PLUVIOMETRO"));
        assertTrue(resultado.geraAlerta());
    }

    @Test
    void deveEmitirAlertaCriticoQuandoPrevisaoAtingeLimiteCritico() throws Exception {
        when(medicaoDAO.buscarUltimas("PLUVIOMETRO", 5)).thenReturn(historico(60, 60, 60, 60, 60));

        ResultadoPrevisao resultado = servico.gerarPrevisao("PLUVIOMETRO", new PrevisaoMediaMovel());

        assertEquals(NivelAlerta.CRITICO, resultado.alerta().getNivel());
    }

    @Test
    void naoDeveEmitirAlertaQuandoPrevisaoEstaAbaixoDoLimiar() throws Exception {
        when(medicaoDAO.buscarUltimas("PLUVIOMETRO", 5)).thenReturn(historico(5, 5, 5, 5, 5));

        ResultadoPrevisao resultado = servico.gerarPrevisao("PLUVIOMETRO", new PrevisaoMediaMovel());

        assertFalse(resultado.geraAlerta());
        verify(alertaDAO, never()).salvar(any(Alerta.class));
    }

    @Test
    void deveUsarEstrategiaDeComparacaoParaAlertar() throws Exception {
        when(medicaoDAO.buscarUltimas("PLUVIOMETRO", 10))
                .thenReturn(historico(10, 10, 10, 10, 10, 20, 20, 20, 20, 20));

        ResultadoPrevisao resultado = servico.gerarPrevisao("PLUVIOMETRO", new PrevisaoComparacao());

        assertEquals(30.0, resultado.valorPrevisto(), 0.0001);
        assertEquals(NivelAlerta.ATENCAO, resultado.alerta().getNivel());
    }

    @Test
    void naoDeveGerarPrevisaoComHistoricoInsuficiente() throws Exception {
        when(medicaoDAO.buscarUltimas("PLUVIOMETRO", 5)).thenReturn(historico(40, 40, 40));

        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> servico.gerarPrevisao("PLUVIOMETRO", new PrevisaoMediaMovel()));

        assertTrue(erro.getMessage().contains("insuficiente"));
        verify(alertaDAO, never()).salvar(any(Alerta.class));
    }

    @Test
    void deveGravarMedicaoValidaLidaDoSensor() throws Exception {
        when(pluviometro.lerValor()).thenReturn(25.0);

        Medicao medicao = servico.lerESalvar(pluviometro);

        verify(medicaoDAO).salvar(any(Medicao.class));
        assertEquals(25.0, medicao.getValor(), 0.0001);
    }

    @Test
    void naoDeveGravarMedicaoForaDaFaixaDoSensor() throws Exception {
        when(pluviometro.lerValor()).thenReturn(120.0);

        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> servico.lerESalvar(pluviometro));

        assertTrue(erro.getMessage().contains("fora de faixa"));
        verify(medicaoDAO, never()).salvar(any(Medicao.class));
    }

    @Test
    void naoDeveGravarMedicaoNegativaDigitadaManualmente() throws Exception {
        assertThrows(DadosInvalidosException.class, () -> servico.registrarMedicaoManual("PLUVIOMETRO", -4.0));

        verify(medicaoDAO, never()).salvar(any(Medicao.class));
    }

    @Test
    void devePropagarFalhaDoSensorSemGravarNada() throws Exception {
        when(pluviometro.lerValor()).thenThrow(new FonteIndisponivelException("sensor fora do ar"));

        assertThrows(FonteIndisponivelException.class, () -> servico.lerESalvar(pluviometro));

        verify(medicaoDAO, never()).salvar(any(Medicao.class));
        verify(alertaDAO, never()).salvar(any(Alerta.class));
    }

    @Test
    void deveRecusarSensorDesconhecido() {
        assertThrows(DadosInvalidosException.class, () -> servico.buscarSensor("RADAR"));
    }
}
