package servico;

import dominio.Alerta;
import dominio.DadosTerritorio;
import dominio.EstrategiaPrevisao;
import dominio.Formato;
import dominio.Medicao;
import dominio.NivelAlerta;
import dominio.Sensor;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import persistencia.AlertaDAO;
import persistencia.MedicaoDAO;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Regras de negócio do sistema: lê sensores, valida, grava no banco, prevê e emite alertas.
 * Recebe os DAOs e os sensores no construtor, o que permite testar a lógica com Mockito,
 * sem banco de dados de verdade.
 */
public class ServicoMonitoramento {

    private final MedicaoDAO medicaoDAO;
    private final AlertaDAO alertaDAO;
    private final List<Sensor> sensores;

    public ServicoMonitoramento(MedicaoDAO medicaoDAO, AlertaDAO alertaDAO, List<Sensor> sensores) {
        this.medicaoDAO = medicaoDAO;
        this.alertaDAO = alertaDAO;
        this.sensores = sensores;
    }

    public List<Sensor> getSensores() {
        return Collections.unmodifiableList(sensores);
    }

    public Sensor buscarSensor(String tipoSensor) throws DadosInvalidosException {
        for (Sensor sensor : sensores) {
            if (sensor.getTipo().equalsIgnoreCase(tipoSensor)) {
                return sensor;
            }
        }
        throw new DadosInvalidosException("Sensor desconhecido: '" + tipoSensor + "'.");
    }

    // ---------------------------------------------------------------- leituras

    /** Pede uma leitura ao sensor, valida e grava no banco. */
    public Medicao lerESalvar(Sensor sensor) throws FonteIndisponivelException, DadosInvalidosException {
        double valor = sensor.lerValor();
        return validarESalvar(sensor, valor);
    }

    /** Registra uma medição digitada pelo operador (passa pelas mesmas validações). */
    public Medicao registrarMedicaoManual(String tipoSensor, double valor)
            throws FonteIndisponivelException, DadosInvalidosException {
        return validarESalvar(buscarSensor(tipoSensor), valor);
    }

    private Medicao validarESalvar(Sensor sensor, double valor)
            throws FonteIndisponivelException, DadosInvalidosException {
        Medicao medicao = new Medicao(sensor.getTipo(), valor); // recusa negativo e NaN
        if (valor > sensor.getValorMaximo()) {
            throw new DadosInvalidosException("Medição fora de faixa: " + Formato.numero(valor) + " "
                    + sensor.getUnidade() + " em " + sensor.getTipo() + " (máximo do sensor: "
                    + Formato.numero(sensor.getValorMaximo()) + " " + sensor.getUnidade() + ").");
        }
        medicaoDAO.salvar(medicao);
        return medicao;
    }

    // ---------------------------------------------------------------- previsões e alertas

    /**
     * Busca o histórico no BANCO, calcula a previsão com a estratégia recebida (polimorfismo)
     * e compara com os limiares. Se atingir algum, emite e grava um alerta.
     */
    public ResultadoPrevisao gerarPrevisao(String tipoSensor, EstrategiaPrevisao estrategia)
            throws FonteIndisponivelException, DadosInvalidosException {
        Sensor sensor = buscarSensor(tipoSensor);

        List<Medicao> historico = medicaoDAO.buscarUltimas(sensor.getTipo(), estrategia.getQuantidadeNecessaria());
        List<Double> valores = historico.stream().map(Medicao::getValor).toList();

        double valorPrevisto = estrategia.prever(valores);
        String explicacao = estrategia.descrever(valores, valorPrevisto, sensor.getUnidade());
        Alerta alerta = avaliarAlerta(sensor, estrategia, valorPrevisto);

        return new ResultadoPrevisao(sensor.getTipo(), estrategia.getNome(), valorPrevisto,
                sensor.getUnidade(), explicacao, alerta);
    }

    private Alerta avaliarAlerta(Sensor sensor, EstrategiaPrevisao estrategia, double valorPrevisto)
            throws FonteIndisponivelException, DadosInvalidosException {
        DadosTerritorio limiares = ConfiguracaoSistema.getInstancia().getDados(sensor.getTipo());
        Optional<NivelAlerta> nivel = limiares.classificar(valorPrevisto);
        if (nivel.isEmpty()) {
            return null;
        }

        String unidade = sensor.getUnidade();
        String mensagem = nivel.get().getRotulo() + ": " + sensor.getTipo()
                + " com valor previsto de " + Formato.numero(valorPrevisto) + " " + unidade
                + " (" + estrategia.getNome() + "), que atingiu o limite de "
                + nivel.get().getRotulo() + " de " + Formato.numero(limiares.getLimite(nivel.get()))
                + " " + unidade + ". " + nivel.get().getOrientacao()
                + " A decisão final é da equipe responsável.";

        Alerta alerta = new Alerta(sensor.getTipo(), valorPrevisto, nivel.get(), mensagem);
        alertaDAO.salvar(alerta);
        return alerta;
    }

    // ---------------------------------------------------------------- consultas

    public List<Medicao> consultarHistorico(String tipoSensor) throws FonteIndisponivelException {
        return medicaoDAO.buscarHistoricoPorSensor(tipoSensor);
    }

    public List<Alerta> consultarAlertas() throws FonteIndisponivelException {
        return alertaDAO.listarTodos();
    }

    public long contarMedicoes(String tipoSensor) throws FonteIndisponivelException {
        return medicaoDAO.contarPorSensor(tipoSensor);
    }

    public Optional<Medicao> ultimaMedicao(String tipoSensor) throws FonteIndisponivelException {
        List<Medicao> ultimas = medicaoDAO.buscarUltimas(tipoSensor, 1);
        return ultimas.isEmpty() ? Optional.empty() : Optional.of(ultimas.get(0));
    }

    /** BÔNUS: exporta o histórico de um sensor para um arquivo CSV (separador ';', abre direto no Excel). */
    public Path exportarHistoricoCsv(String tipoSensor, Path destino) throws FonteIndisponivelException {
        List<Medicao> historico = medicaoDAO.buscarHistoricoPorSensor(tipoSensor);

        try {
            Path pasta = destino.toAbsolutePath().getParent();
            if (pasta != null) {
                Files.createDirectories(pasta);
            }
            try (BufferedWriter escritor = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
                escritor.write("id;tipo_sensor;valor;data_hora");
                escritor.newLine();
                for (Medicao m : historico) {
                    escritor.write(m.getId() + ";" + m.getTipoSensor() + ";"
                            + String.valueOf(m.getValor()).replace('.', ',') + ";"
                            + Formato.dataHora(m.getDataHora()));
                    escritor.newLine();
                }
            }
        } catch (IOException e) {
            throw new FonteIndisponivelException("Não foi possível gravar o arquivo CSV: " + e.getMessage(), e);
        }
        return destino;
    }
}
