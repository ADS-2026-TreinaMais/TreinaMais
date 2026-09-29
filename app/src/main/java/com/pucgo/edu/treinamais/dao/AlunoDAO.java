package com.pucgo.edu.treinamais.dao;

import android.util.Log;

import com.pucgo.edu.treinamais.model.Aluno;
import com.pucgo.edu.treinamais.model.ConnectionFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {

    private static final String TAG = "AlunoDAO";

    public boolean inserir(Aluno aluno, String senhaHash) {
        String sqlUsuario = "INSERT INTO usuarios (nome, email, senha_hash, tipo, status) VALUES (?, ?, ?, 'ALUNO', 'ATIVO')";
        String sqlAluno = "INSERT INTO alunos (usuario_id, professor_id, cpf, data_nascimento, telefone) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = ConnectionFactory.getInst().getConn();
            conn.setAutoCommit(false);

            long usuarioId;
            try (PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                stmtUsuario.setString(1, aluno.getNome());
                stmtUsuario.setString(2, aluno.getEmail().toLowerCase().trim());
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

            try (PreparedStatement stmtAluno = conn.prepareStatement(sqlAluno, Statement.RETURN_GENERATED_KEYS)) {
                stmtAluno.setLong(1, usuarioId);

                if (aluno.getProfessorId() != null) {
                    stmtAluno.setLong(2, aluno.getProfessorId());
                } else {
                    stmtAluno.setNull(2, Types.BIGINT);
                }

                stmtAluno.setString(3, aluno.getCpf() != null ? aluno.getCpf().trim() : null);

                if (aluno.getDataNascimento() != null && !aluno.getDataNascimento().isEmpty()) {
                    try {
                        stmtAluno.setDate(4, Date.valueOf(aluno.getDataNascimento()));
                    } catch (IllegalArgumentException e) {
                        stmtAluno.setNull(4, Types.DATE);
                    }
                } else {
                    stmtAluno.setNull(4, Types.DATE);
                }

                stmtAluno.setString(5, aluno.getTelefone());

                int affected = stmtAluno.executeUpdate();
                if (affected == 0) {
                    conn.rollback();
                    return false;
                }

                try (ResultSet generatedKeys = stmtAluno.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        aluno.setId(generatedKeys.getLong(1));
                    }
                }
            }

            aluno.setUsuarioId(usuarioId);
            conn.commit();
            return true;

        } catch (SQLException e) {
            Log.e(TAG, "Erro ao inserir aluno no banco de dados", e);
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

    public boolean atualizar(Aluno aluno) {
        String sqlAluno = "UPDATE alunos SET professor_id = ?, cpf = ?, data_nascimento = ?, telefone = ? WHERE id = ?";
        String sqlUser = "UPDATE usuarios SET nome = ?, email = ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = ConnectionFactory.getInst().getConn();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtAluno = conn.prepareStatement(sqlAluno)) {
                if (aluno.getProfessorId() != null) {
                    stmtAluno.setLong(1, aluno.getProfessorId());
                } else {
                    stmtAluno.setNull(1, Types.BIGINT);
                }

                stmtAluno.setString(2, aluno.getCpf());

                if (aluno.getDataNascimento() != null && !aluno.getDataNascimento().isEmpty()) {
                    try {
                        stmtAluno.setDate(3, Date.valueOf(aluno.getDataNascimento()));
                    } catch (IllegalArgumentException e) {
                        stmtAluno.setNull(3, Types.DATE);
                    }
                } else {
                    stmtAluno.setNull(3, Types.DATE);
                }

                stmtAluno.setString(4, aluno.getTelefone());
                stmtAluno.setLong(5, aluno.getId());
                stmtAluno.executeUpdate();
            }

            if (aluno.getUsuarioId() != null) {
                try (PreparedStatement stmtUser = conn.prepareStatement(sqlUser)) {
                    stmtUser.setString(1, aluno.getNome());
                    stmtUser.setString(2, aluno.getEmail());
                    stmtUser.setLong(3, aluno.getUsuarioId());
                    stmtUser.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            Log.e(TAG, "Erro ao atualizar aluno", e);
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            fecharConexao(conn);
        }
    }

    public boolean excluir(Long id) {
        String sql = "DELETE FROM alunos WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInst().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            Log.e(TAG, "Erro ao excluir aluno ID: " + id, e);
            return false;
        }
    }

    public Aluno buscarPorId(Long id) {
        String sql = "SELECT a.id, a.usuario_id, a.professor_id, a.cpf, a.data_nascimento, a.telefone, a.criado_em, " +
                     "u.nome, u.email, up.nome as nome_professor " +
                     "FROM alunos a " +
                     "JOIN usuarios u ON a.usuario_id = u.id " +
                     "LEFT JOIN professores p ON a.professor_id = p.id " +
                     "LEFT JOIN usuarios up ON p.usuario_id = up.id " +
                     "WHERE a.id = ?";

        try (Connection conn = ConnectionFactory.getInst().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearAluno(rs);
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar aluno por ID: " + id, e);
        }
        return null;
    }

    public Aluno buscarPorCpf(String cpf) {
        String sql = "SELECT a.id, a.usuario_id, a.professor_id, a.cpf, a.data_nascimento, a.telefone, a.criado_em, " +
                     "u.nome, u.email, up.nome as nome_professor " +
                     "FROM alunos a " +
                     "JOIN usuarios u ON a.usuario_id = u.id " +
                     "LEFT JOIN professores p ON a.professor_id = p.id " +
                     "LEFT JOIN usuarios up ON p.usuario_id = up.id " +
                     "WHERE a.cpf = ?";

        try (Connection conn = ConnectionFactory.getInst().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearAluno(rs);
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar aluno por CPF: " + cpf, e);
        }
        return null;
    }

    public List<Aluno> listarTodos() {
        List<Aluno> lista = new ArrayList<>();
        String sql = "SELECT a.id, a.usuario_id, a.professor_id, a.cpf, a.data_nascimento, a.telefone, a.criado_em, " +
                     "u.nome, u.email, up.nome as nome_professor " +
                     "FROM alunos a " +
                     "JOIN usuarios u ON a.usuario_id = u.id " +
                     "LEFT JOIN professores p ON a.professor_id = p.id " +
                     "LEFT JOIN usuarios up ON p.usuario_id = up.id " +
                     "ORDER BY u.nome ASC";

        try (Connection conn = ConnectionFactory.getInst().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearAluno(rs));
            }

        } catch (SQLException e) {
            Log.e(TAG, "Erro ao listar todos os alunos", e);
        }
        return lista;
    }

    public List<Aluno> listarPorProfessor(Long professorId) {
        List<Aluno> lista = new ArrayList<>();
        String sql = "SELECT a.id, a.usuario_id, a.professor_id, a.cpf, a.data_nascimento, a.telefone, a.criado_em, " +
                     "u.nome, u.email, up.nome as nome_professor " +
                     "FROM alunos a " +
                     "JOIN usuarios u ON a.usuario_id = u.id " +
                     "LEFT JOIN professores p ON a.professor_id = p.id " +
                     "LEFT JOIN usuarios up ON p.usuario_id = up.id " +
                     "WHERE a.professor_id = ? " +
                     "ORDER BY u.nome ASC";

        try (Connection conn = ConnectionFactory.getInst().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, professorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAluno(rs));
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, "Erro ao listar alunos por professor ID: " + professorId, e);
        }
        return lista;
    }

    public boolean vincularProfessor(Long alunoId, Long professorId) {
        String sql = "UPDATE alunos SET professor_id = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInst().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (professorId != null) {
                stmt.setLong(1, professorId);
            } else {
                stmt.setNull(1, Types.BIGINT);
            }
            stmt.setLong(2, alunoId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            Log.e(TAG, "Erro ao vincular professor ao aluno", e);
            return false;
        }
    }

    private Aluno mapearAluno(ResultSet rs) throws SQLException {
        Aluno aluno = new Aluno();
        aluno.setId(rs.getLong("id"));
        aluno.setUsuarioId(rs.getLong("usuario_id"));

        long profId = rs.getLong("professor_id");
        if (!rs.wasNull()) {
            aluno.setProfessorId(profId);
        }

        aluno.setCpf(rs.getString("cpf"));

        Date dataNasc = rs.getDate("data_nascimento");
        if (dataNasc != null) {
            aluno.setDataNascimento(dataNasc.toString());
        }

        aluno.setTelefone(rs.getString("telefone"));
        aluno.setNome(rs.getString("nome"));
        aluno.setEmail(rs.getString("email"));
        aluno.setNomeProfessor(rs.getString("nome_professor"));
        aluno.setCriadoEm(rs.getTimestamp("criado_em"));

        return aluno;
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
