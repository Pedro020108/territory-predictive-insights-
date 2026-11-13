package app;

import dominio.Alerta;
import dominio.DadosTerritorio;
import dominio.EstrategiaPrevisao;
import dominio.Formato;
import dominio.Medicao;
import dominio.Pluviometro;
import dominio.RegiaoMonitorada;
import dominio.Sensor;
import dominio.SensorNivelAgua;
import dominio.SensorUmidadeSolo;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import persistencia.AlertaDAO;
import persistencia.Conexao;
import persistencia.MedicaoDAO;
import servico.ConfiguracaoSistema;
import servico.FabricaPrevisao;
import servico.ResultadoPrevisao;
import servico.ServicoMonitoramento;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Menu principal (console). Só cuida da conversa com o usuário:
 * as regras ficam no ServicoMonitoramento e os erros são capturados aqui,
 * sempre com uma mensagem clara.
 */
public class SistemaMonitoramento {

    private static final int MAX_LINHAS_HISTORICO = 30;

    private final Scanner teclado = new Scanner(System.in);
    private final ServicoMonitoramento servico;
    private final RegiaoMonitorada regiao;
    private boolean entradaEncerrada = false;

    public SistemaMonitoramento(ServicoMonitoramento servico, RegiaoMonitorada regiao) {
        this.servico = servico;
        this.regiao = regiao;
    }

    public static void main(String[] args) {
        try {
            Conexao.inicializarBanco();

            List<Sensor> sensores = new ArrayList<>();
            sensores.add(new Pluviometro());
            sensores.add(new SensorNivelAgua());
            sensores.add(new SensorUmidadeSolo());

            RegiaoMonitorada regiao = new RegiaoMonitorada(1, "Bairro Baixada do Rio", "Zona Sul", 12000);
            for (Sensor sensor : sensores) {
                regiao.adicionarSensor(sensor);
            }

            ServicoMonitoramento servico = new ServicoMonitoramento(new MedicaoDAO(), new AlertaDAO(), sensores);
            new SistemaMonitoramento(servico, regiao).executar();

        } catch (FonteIndisponivelException e) {
            System.out.println("Não foi possível iniciar o sistema: " + e.getMessage());
        } catch (DadosInvalidosException e) {
            System.out.println("Configuração inicial inválida: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ loop principal

    public void executar() {
        int opcao = -1;
        while (opcao != 0 && !entradaEncerrada) {
            mostrarMenu();
            try {
                opcao = lerInteiro("Escolha uma opção: ");
                switch (opcao) {
                    case 1 -> verificarSensores();
                    case 2 -> realizarLeituras();
                    case 3 -> rodarPrevisoes();
                    case 4 -> consultarHistorico();
                    case 5 -> consultarAlertas();
                    case 6 -> gerenciarDadosDoTerritorio();
                    case 7 -> mostrarStatusCompleto();
                    case 0 -> System.out.println("Encerrando o programa. Os dados ficam salvos no banco.");
                    default -> System.out.println("Opção inexistente: escolha um número de 0 a 7.");
                }
            } catch (DadosInvalidosException e) {
                System.out.println("⚠ Dados inválidos: " + e.getMessage());
            } catch (FonteIndisponivelException e) {
                System.out.println("⚠ Fonte de dados indisponível: " + e.getMessage());
            }
        }
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("====================================");
        System.out.println("    TERRITORY PREDICTIVE INSIGHTS");
        System.out.println("====================================");
        System.out.println("1 - Verificar sensores");
        System.out.println("2 - Realizar leituras");
        System.out.println("3 - Rodar previsões");
        System.out.println("4 - Consultar histórico");
        System.out.println("5 - Consultar alertas");
        System.out.println("6 - Gerenciar dados do território");
        System.out.println("7 - Status completo");
        System.out.println("0 - Encerrar programa");
        System.out.println();
    }

    // ------------------------------------------------------------------ 1 - sensores

    private void verificarSensores() {
        System.out.println("--- Verificação dos sensores ---");
        for (Sensor sensor : servico.getSensores()) {
            String situacao = sensor.verificarFuncionamento() ? "funcionando" : "FORA DO AR";
            System.out.println(" - " + sensor.getTipo() + " (" + sensor.getUnidade() + "): " + situacao);
        }
        System.out.println("Região: " + regiao.verificarStatus());
    }

    // ------------------------------------------------------------------ 2 - leituras

    private void realizarLeituras() {
        System.out.println("--- Leituras dos sensores ---");
        for (Sensor sensor : servico.getSensores()) {
            try {
                Medicao medicao = servico.lerESalvar(sensor);
                System.out.println(" ✔ " + sensor.getTipo() + ": " + Formato.numero(medicao.getValor()) + " "
                        + sensor.getUnidade() + " (gravada no banco, id " + medicao.getId() + ")");
            } catch (FonteIndisponivelException e) {
                System.out.println(" ✖ " + sensor.getTipo() + " sem leitura: " + e.getMessage());
            } catch (DadosInvalidosException e) {
                System.out.println(" ✖ " + sensor.getTipo() + " leitura descartada: " + e.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------ 3 - previsões

    private void rodarPrevisoes() throws DadosInvalidosException {
        List<String> tipos = FabricaPrevisao.tiposDisponiveis();

        System.out.println("--- Estratégia de previsão ---");
        for (int i = 0; i < tipos.size(); i++) {
            System.out.println((i + 1) + " - " + FabricaPrevisao.criar(tipos.get(i)).getNome());
        }
        System.out.println((tipos.size() + 1) + " - Rodar todas as estratégias");

        int escolha = lerInteiro("Opção: ");
        List<String> escolhidas = new ArrayList<>();
        if (escolha == tipos.size() + 1) {
            escolhidas.addAll(tipos);
        } else if (escolha >= 1 && escolha <= tipos.size()) {
            escolhidas.add(tipos.get(escolha - 1));
        } else {
            throw new DadosInvalidosException("Estratégia inexistente: escolha um número de 1 a "
                    + (tipos.size() + 1) + ".");
        }

        for (String tipo : escolhidas) {
            EstrategiaPrevisao estrategia = FabricaPrevisao.criar(tipo);
            System.out.println();
            System.out.println("=== " + estrategia.getNome() + " ===");
            for (Sensor sensor : servico.getSensores()) {
                preverSensor(sensor, estrategia);
            }
        }
    }

    private void preverSensor(Sensor sensor, EstrategiaPrevisao estrategia) {
        try {
            ResultadoPrevisao resultado = servico.gerarPrevisao(sensor.getTipo(), estrategia);
            System.out.println(" " + sensor.getTipo() + " -> previsto: " + Formato.numero(resultado.valorPrevisto())
                    + " " + resultado.unidade());
            System.out.println("    " + resultado.explicacao());
            if (resultado.geraAlerta()) {
                System.out.println("    >>> " + resultado.alerta().getMensagem());
            } else {
                System.out.println("    Sem alerta: previsão abaixo do limite de ATENÇÃO.");
            }
        } catch (DadosInvalidosException e) {
            System.out.println(" " + sensor.getTipo() + " -> sem previsão: " + e.getMessage());
        } catch (FonteIndisponivelException e) {
            System.out.println(" " + sensor.getTipo() + " -> falha ao consultar o banco: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ 4 - histórico

    private void consultarHistorico() throws DadosInvalidosException, FonteIndisponivelException {
        Sensor sensor = escolherSensor();
        List<Medicao> historico = servico.consultarHistorico(sensor.getTipo());

        System.out.println("--- Histórico de " + sensor.getTipo() + " (" + historico.size() + " medições) ---");
        if (historico.isEmpty()) {
            System.out.println("Nenhuma medição gravada ainda. Use a opção 2 para gerar leituras.");
            return;
        }

        int inicio = Math.max(0, historico.size() - MAX_LINHAS_HISTORICO);
        if (inicio > 0) {
            System.out.println("(mostrando as " + MAX_LINHAS_HISTORICO + " mais recentes)");
        }
        for (int i = inicio; i < historico.size(); i++) {
            System.out.println(" " + historico.get(i) + " " + sensor.getUnidade());
        }

        System.out.print("Exportar o histórico completo para CSV? (s/n): ");
        if (lerLinha().equalsIgnoreCase("s")) {
            Path arquivo = Path.of("dados", "historico_" + sensor.getTipo().toLowerCase() + ".csv");
            servico.exportarHistoricoCsv(sensor.getTipo(), arquivo);
            System.out.println("Arquivo gerado: " + arquivo.toAbsolutePath());
        }
    }

    // ------------------------------------------------------------------ 5 - alertas

    private void consultarAlertas() throws FonteIndisponivelException {
        List<Alerta> alertas = servico.consultarAlertas();
        System.out.println("--- Alertas emitidos (" + alertas.size() + ") ---");
        if (alertas.isEmpty()) {
            System.out.println("Nenhum alerta emitido até agora.");
        }
        for (Alerta alerta : alertas) {
            System.out.println(" " + alerta);
        }
    }

    // ------------------------------------------------------------------ 6 - dados do território

    private void gerenciarDadosDoTerritorio() {
        int opcao = -1;
        while (opcao != 0 && !entradaEncerrada) {
            System.out.println();
            System.out.println("--- Dados do território ---");
            System.out.println("1 - Ver limiares de alerta");
            System.out.println("2 - Alterar limiares (exige código de acesso)");
            System.out.println("3 - Registrar medição manual");
            System.out.println("4 - Simular defeito/conserto de um sensor");
            System.out.println("5 - Alterar população estimada da região");
            System.out.println("0 - Voltar");
            try {
                opcao = lerInteiro("Opção: ");
                switch (opcao) {
                    case 1 -> mostrarLimiares();
                    case 2 -> alterarLimiares();
                    case 3 -> registrarMedicaoManual();
                    case 4 -> alternarFuncionamentoDoSensor();
                    case 5 -> alterarPopulacao();
                    case 0 -> System.out.println("Voltando ao menu principal.");
                    default -> System.out.println("Opção inexistente: escolha um número de 0 a 5.");
                }
            } catch (DadosInvalidosException e) {
                System.out.println("⚠ Dados inválidos: " + e.getMessage());
            } catch (FonteIndisponivelException e) {
                System.out.println("⚠ Fonte de dados indisponível: " + e.getMessage());
            }
        }
    }

    private void mostrarLimiares() {
        System.out.println("Limiares atuais (valores de exemplo configuráveis, não são oficiais):");
        for (DadosTerritorio dados : ConfiguracaoSistema.getInstancia().listarDados()) {
            System.out.println(" - " + dados);
        }
    }

    private void alterarLimiares() throws DadosInvalidosException {
        Sensor sensor = escolherSensor();
        DadosTerritorio dados = ConfiguracaoSistema.getInstancia().getDados(sensor.getTipo());
        System.out.println("Atual: " + dados);

        System.out.print("Código de acesso: ");
        String codigo = lerLinha();
        double atencao = lerDouble("Novo limite de ATENÇÃO (" + sensor.getUnidade() + "): ");
        double alerta = lerDouble("Novo limite de ALERTA (" + sensor.getUnidade() + "): ");
        double critico = lerDouble("Novo limite CRÍTICO (" + sensor.getUnidade() + "): ");

        dados.alterarLimiares(codigo, atencao, alerta, critico);
        System.out.println("Limiares atualizados: " + dados);
    }

    private void registrarMedicaoManual() throws DadosInvalidosException, FonteIndisponivelException {
        Sensor sensor = escolherSensor();
        double valor = lerDouble("Valor medido (" + sensor.getUnidade() + "): ");
        Medicao medicao = servico.registrarMedicaoManual(sensor.getTipo(), valor);
        System.out.println("Medição registrada: " + medicao + " " + sensor.getUnidade());
    }

    private void alternarFuncionamentoDoSensor() throws DadosInvalidosException {
        Sensor sensor = escolherSensor();
        boolean novoEstado = !sensor.verificarFuncionamento();
        sensor.setFuncionando(novoEstado);
        System.out.println(sensor.getTipo() + (novoEstado ? " voltou a funcionar." : " agora está com defeito (simulado)."));
    }

    private void alterarPopulacao() throws DadosInvalidosException {
        int populacao = lerInteiro("Nova população estimada da região (número de habitantes): ");
        regiao.setPopulacaoEstimada(populacao);
        System.out.println("População estimada atualizada: " + regiao.getPopulacaoEstimada());
    }

    // ------------------------------------------------------------------ 7 - status completo

    private void mostrarStatusCompleto() {
        System.out.println("=== STATUS COMPLETO ===");
        System.out.println("Região: " + regiao.getNome() + " (" + regiao.getRegiao() + ")");
        System.out.println("Situação: " + regiao.verificarStatus());
        System.out.println("População estimada: " + regiao.getPopulacaoEstimada() + " habitantes");
        System.out.println();

        for (Sensor sensor : servico.getSensores()) {
            System.out.println(sensor.getTipo() + " (" + sensor.getUnidade() + ")");
            System.out.println("   Situação: " + (sensor.verificarFuncionamento() ? "funcionando" : "FORA DO AR"));
            try {
                long total = servico.contarMedicoes(sensor.getTipo());
                Optional<Medicao> ultima = servico.ultimaMedicao(sensor.getTipo());
                System.out.println("   Medições no banco: " + total);
                System.out.println("   Última medição: "
                        + (ultima.isPresent() ? ultima.get().toString() + " " + sensor.getUnidade() : "nenhuma"));
            } catch (FonteIndisponivelException e) {
                System.out.println("   Banco indisponível para este sensor: " + e.getMessage());
            }
        }

        System.out.println();
        mostrarLimiares();

        try {
            List<Alerta> alertas = servico.consultarAlertas();
            System.out.println();
            System.out.println("Alertas emitidos: " + alertas.size());
            for (int i = 0; i < Math.min(3, alertas.size()); i++) {
                System.out.println(" " + alertas.get(i));
            }
        } catch (FonteIndisponivelException e) {
            System.out.println("Não foi possível consultar os alertas: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ leitura de dados do teclado

    private Sensor escolherSensor() throws DadosInvalidosException {
        List<Sensor> sensores = servico.getSensores();
        System.out.println("Sensores:");
        for (int i = 0; i < sensores.size(); i++) {
            System.out.println(" " + (i + 1) + " - " + sensores.get(i).getTipo()
                    + " (" + sensores.get(i).getUnidade() + ")");
        }
        int escolha = lerInteiro("Sensor: ");
        if (escolha < 1 || escolha > sensores.size()) {
            throw new DadosInvalidosException("Sensor inexistente: escolha um número de 1 a " + sensores.size() + ".");
        }
        return sensores.get(escolha - 1);
    }

    private String lerLinha() {
        if (!teclado.hasNextLine()) {
            entradaEncerrada = true; // acabou a entrada: encerra sem travar
            return "";
        }
        return teclado.nextLine().trim();
    }

    private int lerInteiro(String pergunta) throws DadosInvalidosException {
        System.out.print(pergunta);
        String texto = lerLinha();
        if (entradaEncerrada) {
            return 0;
        }
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("Entrada inválida: '" + texto + "' não é um número inteiro.");
        }
    }

    private double lerDouble(String pergunta) throws DadosInvalidosException {
        System.out.print(pergunta);
        String texto = lerLinha();
        try {
            return Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("Entrada inválida: '" + texto + "' não é um número.");
        }
    }
}
