package persistencia;

import excecao.FonteIndisponivelException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Cuida da conexão com o banco H2.
 * O banco fica em ARQUIVO (pasta dados/), então o histórico sobrevive ao fechar o programa.
 * As tabelas são criadas sozinhas na primeira execução.
 */
public final class Conexao {

    private static final String URL_PADRAO = "jdbc:h2:file:./dados/territorio";
    private static final String USUARIO = "sa";
    private static final String SENHA = "";

    private static String url = URL_PADRAO;

    private Conexao() {
    }

    /** Permite trocar o banco (ex.: em memória). Não é usado pelo programa normal. */
    public static void setUrl(String novaUrl) {
        url = novaUrl;
    }

    public static Connection getConnection() throws FonteIndisponivelException {
        try {
            criarPastaDoBanco();
            return DriverManager.getConnection(url, USUARIO, SENHA);
        } catch (SQLException e) {
            throw new FonteIndisponivelException(
                    "Não foi possível conectar ao banco de dados: " + e.getMessage(), e);
        }
    }

    /** Cria as tabelas MEDICAO e ALERTA, se ainda não existirem. */
    public static void inicializarBanco() throws FonteIndisponivelException {
        String criarMedicao = "CREATE TABLE IF NOT EXISTS MEDICAO ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "tipo_sensor VARCHAR(50) NOT NULL, "
                + "valor DOUBLE NOT NULL, "
                + "data_hora TIMESTAMP NOT NULL)";

        String criarAlerta = "CREATE TABLE IF NOT EXISTS ALERTA ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "tipo_sensor VARCHAR(50) NOT NULL, "
                + "valor_previsto DOUBLE NOT NULL, "
                + "nivel VARCHAR(20) NOT NULL, "
                + "mensagem VARCHAR(1000) NOT NULL, "
                + "data_hora TIMESTAMP NOT NULL)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(criarMedicao);
            stmt.execute(criarAlerta);
        } catch (SQLException e) {
            throw new FonteIndisponivelException(
                    "Não foi possível criar as tabelas do banco: " + e.getMessage(), e);
        }
    }

    private static void criarPastaDoBanco() throws FonteIndisponivelException {
        if (!url.startsWith("jdbc:h2:file:")) {
            return;
        }
        try {
            Files.createDirectories(Path.of("dados"));
        } catch (IOException e) {
            throw new FonteIndisponivelException(
                    "Não foi possível criar a pasta 'dados' para guardar o banco: " + e.getMessage(), e);
        }
    }
}
