package persistencia;

import dominio.Alerta;
import dominio.NivelAlerta;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** DAO da tabela ALERTA: grava cada alerta emitido e lista os já emitidos. */
public class AlertaDAO {

    public void salvar(Alerta alerta) throws FonteIndisponivelException {
        String sql = "INSERT INTO ALERTA (tipo_sensor, valor_previsto, nivel, mensagem, data_hora) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, alerta.getTipoSensor());
            stmt.setDouble(2, alerta.getValorPrevisto());
            stmt.setString(3, alerta.getNivel().name());
            stmt.setString(4, alerta.getMensagem());
            stmt.setTimestamp(5, Timestamp.valueOf(alerta.getDataHora()));
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    alerta.setId(chaves.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new FonteIndisponivelException("Falha ao salvar o alerta no banco: " + e.getMessage(), e);
        }
    }

    /** Todos os alertas já emitidos, do mais recente para o mais antigo. */
    public List<Alerta> listarTodos() throws FonteIndisponivelException {
        String sql = "SELECT id, tipo_sensor, valor_previsto, nivel, mensagem, data_hora "
                + "FROM ALERTA ORDER BY data_hora DESC, id DESC";

        List<Alerta> lista = new ArrayList<>();
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Alerta(
                        rs.getLong("id"),
                        rs.getString("tipo_sensor"),
                        rs.getDouble("valor_previsto"),
                        NivelAlerta.valueOf(rs.getString("nivel")),
                        rs.getString("mensagem"),
                        rs.getTimestamp("data_hora").toLocalDateTime()));
            }
            return lista;
        } catch (SQLException | DadosInvalidosException | IllegalArgumentException e) {
            throw new FonteIndisponivelException("Falha ao consultar os alertas: " + e.getMessage(), e);
        }
    }
}
