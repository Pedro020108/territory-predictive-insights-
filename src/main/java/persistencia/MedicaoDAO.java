package persistencia;

import dominio.Medicao;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DAO (Data Access Object): a única classe que conversa com a tabela MEDICAO.
 * Todo SQL usa PreparedStatement (parâmetros "?"), nunca concatenação de String,
 * e os recursos são fechados automaticamente pelo try-with-resources.
 */
public class MedicaoDAO {

    /** Grava a medição e devolve o id gerado pelo banco dentro do próprio objeto. */
    public void salvar(Medicao medicao) throws FonteIndisponivelException {
        String sql = "INSERT INTO MEDICAO (tipo_sensor, valor, data_hora) VALUES (?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, medicao.getTipoSensor());
            stmt.setDouble(2, medicao.getValor());
            stmt.setTimestamp(3, Timestamp.valueOf(medicao.getDataHora()));
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    medicao.setId(chaves.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new FonteIndisponivelException("Falha ao salvar a medição no banco: " + e.getMessage(), e);
        }
    }

    /** Todas as medições de todos os sensores, da mais antiga para a mais recente. */
    public List<Medicao> listarTodas() throws FonteIndisponivelException {
        String sql = "SELECT id, tipo_sensor, valor, data_hora FROM MEDICAO ORDER BY data_hora, id";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            return lerResultados(stmt);
        } catch (SQLException | DadosInvalidosException e) {
            throw new FonteIndisponivelException("Falha ao consultar as medições: " + e.getMessage(), e);
        }
    }

    /** Histórico completo de um sensor, da mais antiga para a mais recente. */
    public List<Medicao> buscarHistoricoPorSensor(String tipoSensor) throws FonteIndisponivelException {
        String sql = "SELECT id, tipo_sensor, valor, data_hora FROM MEDICAO "
                + "WHERE tipo_sensor = ? ORDER BY data_hora, id";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tipoSensor);
            return lerResultados(stmt);
        } catch (SQLException | DadosInvalidosException e) {
            throw new FonteIndisponivelException(
                    "Falha ao consultar o histórico do sensor " + tipoSensor + ": " + e.getMessage(), e);
        }
    }

    /**
     * As últimas "quantidade" medições de um sensor, em ordem cronológica
     * (a mais antiga primeiro, a mais recente por último). É a base das previsões.
     */
    public List<Medicao> buscarUltimas(String tipoSensor, int quantidade) throws FonteIndisponivelException {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade de medições deve ser maior que zero.");
        }
        String sql = "SELECT id, tipo_sensor, valor, data_hora FROM MEDICAO "
                + "WHERE tipo_sensor = ? ORDER BY data_hora DESC, id DESC LIMIT ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tipoSensor);
            stmt.setInt(2, quantidade);
            List<Medicao> ultimas = lerResultados(stmt);
            Collections.reverse(ultimas); // veio da mais nova para a mais antiga
            return ultimas;
        } catch (SQLException | DadosInvalidosException e) {
            throw new FonteIndisponivelException(
                    "Falha ao consultar as últimas medições de " + tipoSensor + ": " + e.getMessage(), e);
        }
    }

    /** Quantas medições existem no banco para o sensor. */
    public long contarPorSensor(String tipoSensor) throws FonteIndisponivelException {
        String sql = "SELECT COUNT(*) FROM MEDICAO WHERE tipo_sensor = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tipoSensor);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new FonteIndisponivelException(
                    "Falha ao contar as medições de " + tipoSensor + ": " + e.getMessage(), e);
        }
    }

    private List<Medicao> lerResultados(PreparedStatement stmt) throws SQLException, DadosInvalidosException {
        List<Medicao> lista = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Medicao(
                        rs.getLong("id"),
                        rs.getString("tipo_sensor"),
                        rs.getDouble("valor"),
                        rs.getTimestamp("data_hora").toLocalDateTime()));
            }
        }
        return lista;
    }
}
