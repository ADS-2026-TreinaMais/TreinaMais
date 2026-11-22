package com.pucgo.edu.treinamais.dao;

import android.util.Log;

import com.pucgo.edu.treinamais.model.ConnectionFactory;
import com.pucgo.edu.treinamais.model.Professor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProfessorDAO {

    private static final String TAG = "ProfessorDAO";

    public boolean inserir(Professor professor, String senhaHash) {
        String sqlUsuario = "INSERT INTO usuarios (nome, email, senha_hash, tipo, status) VALUES (?, ?, ?, 'PROFESSOR', 'ATIVO')";
        String sqlProfessor = "INSERT INTO professores (usuario_id, cref, telefone) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = ConnectionFactory.getInst().getConn();
            conn.setAutoCommit(false);

            long usuarioId;
            try (PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                stmtUsuario.setString(1, professor.getNome());
                stmtUsuario.setString(2, professor.getEmail().toLowerCase().trim());
                stmtUsuario.setString(3, senhaHash);

                int affected = stmtUsuario.executeUpdate();
                if (affected == 0) {
                    conn.rollback();
                    return false;
                }

                try (ResultSet generatedKeys = stmtUsuario.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuarioId = generatedKeys.getLong(1);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement stmtProf = conn.prepareStatement(sqlProfessor, Statement.RETURN_GENERATED_KEYS)) {
                stmtProf.setLong(1, usuarioId);
                stmtProf.setString(2, professor.getCref().trim());
                stmtProf.setString(3, professor.getTelefone());

                int affected = stmtProf.executeUpdate();
                if (affected == 0) {
                    conn.rollback();
                    return false;
                }

                try (ResultSet generatedKeys = stmtProf.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        professor.setId(generatedKeys.getLong(1));
                    }
                }
            }

            professor.setUsuarioId(usuarioId);
            conn.commit();
            return true;

        } catch (SQLException e) {
            Log.e(TAG, "Erro ao inserir professor no banco de dados", e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    Log.e(TAG, "Erro ao realizar rollback", ex);
                }
            }
            return false;
        } finally {
            fecharConexao(conn);
        }
    }

    public boolean atualizar(Professor professor) {
        String sqlProf = "UPDATE professores SET cref = ?, telefone = ? WHERE id = ?";
        String sqlUser = "UPDATE usuarios SET nome = ?, email = ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = ConnectionFactory.getInst().getConn();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtProf = conn.prepareStatement(sqlProf)) {
                stmtProf.setString(1, professor.getCref());
                stmtProf.setString(2, professor.getTelefone());
                stmtProf.setLong(3, professor.getId());
                stmtProf.executeUpdate();
            }

            if (professor.getUsuarioId() != null) {
                try (PreparedStatement stmtUser = conn.prepareStatement(sqlUser)) {
                    stmtUser.setString(1, professor.getNome());
                    stmtUser.setString(2, professor.getEmail());
                    stmtUser.setLong(3, professor.getUsuarioId());
                    stmtUser.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            Log.e(TAG, "Erro ao atualizar professor", e);
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            fecharConexao(conn);
        }
    }

    public boolean excluir(Long id) {
        String sql = "DELETE FROM professores WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInst().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            Log.e(TAG, "Erro ao excluir professor ID: " + id, e);
            return false;
        }
    }

    public Professor buscarPorId(Long id) {
        String sql = "SELECT p.id, p.usuario_id, p.cref, p.telefone, p.criado_em, u.nome, u.email " +
                     "FROM professores p " +
                     "JOIN usuarios u ON p.usuario_id = u.id " +
                     "WHERE p.id = ?";

        try (Connection conn = ConnectionFactory.getInst().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProfessor(rs);
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar professor por ID: " + id, e);
        }
        return null;
    }

    public Professor buscarPorUsuarioId(Long usuarioId) {
        String sql = "SELECT p.id, p.usuario_id, p.cref, p.telefone, p.criado_em, u.nome, u.email " +
                     "FROM professores p " +
                     "JOIN usuarios u ON p.usuario_id = u.id " +
                     "WHERE p.usuario_id = ?";

        try (Connection conn = ConnectionFactory.getInst().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProfessor(rs);
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar professor por usuario_id: " + usuarioId, e);
        }
        return null;
    }

    public Professor buscarPorCref(String cref) {
        String sql = "SELECT p.id, p.usuario_id, p.cref, p.telefone, p.criado_em, u.nome, u.email " +
                     "FROM professores p " +
                     "JOIN usuarios u ON p.usuario_id = u.id " +
                     "WHERE p.cref = ?";

        try (Connection conn = ConnectionFactory.getInst().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cref);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProfessor(rs);
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar professor por CREF: " + cref, e);
        }
        return null;
    }

    public Professor buscarPorEmail(String email) {
        String sql = "SELECT p.id, p.usuario_id, p.cref, p.telefone, p.criado_em, u.nome, u.email " +
                     "FROM professores p " +
                     "JOIN usuarios u ON p.usuario_id = u.id " +
                     "WHERE LOWER(u.email) = LOWER(?)";

        try (Connection conn = ConnectionFactory.getInst().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProfessor(rs);
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar professor por email: " + email, e);
        }
        return null;
    }

    public List<Professor> listarTodos() {
        List<Professor> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.usuario_id, p.cref, p.telefone, p.criado_em, u.nome, u.email " +
                     "FROM professores p " +
                     "JOIN usuarios u ON p.usuario_id = u.id " +
                     "ORDER BY u.nome ASC";

        try (Connection conn = ConnectionFactory.getInst().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearProfessor(rs));
            }

        } catch (SQLException e) {
            Log.e(TAG, "Erro ao listar professores", e);
        }
        return lista;
    }

    public boolean existeCref(String cref) {
        String sql = "SELECT COUNT(*) FROM professores WHERE cref = ?";
        try (Connection conn = ConnectionFactory.getInst().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cref);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, "Erro ao verificar existência de CREF", e);
        }
        return false;
    }

    private Professor mapearProfessor(ResultSet rs) throws SQLException {
        Professor prof = new Professor();
        prof.setId(rs.getLong("id"));
        prof.setUsuarioId(rs.getLong("usuario_id"));
        prof.setCref(rs.getString("cref"));
        prof.setTelefone(rs.getString("telefone"));
        prof.setNome(rs.getString("nome"));
        prof.setEmail(rs.getString("email"));
        prof.setCriadoEm(rs.getTimestamp("criado_em"));
        return prof;
    }

    private void fecharConexao(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
