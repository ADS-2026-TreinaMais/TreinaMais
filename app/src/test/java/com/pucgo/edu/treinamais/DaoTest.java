package com.pucgo.edu.treinamais;

import com.pucgo.edu.treinamais.dao.AlunoDAO;
import com.pucgo.edu.treinamais.dao.ProfessorDAO;
import com.pucgo.edu.treinamais.model.Aluno;
import com.pucgo.edu.treinamais.model.ConnectionFactory;
import com.pucgo.edu.treinamais.model.Professor;
import com.pucgo.edu.treinamais.model.Usuario;

import org.junit.Test;

import static org.junit.Assert.*;

public class DaoTest {

    @Test
    public void testConnectionFactoryConfiguracao() {
        assertNotNull(ConnectionFactory.getInst());
        assertEquals("10.0.2.2", ConnectionFactory.getIp());
        assertEquals("5432", ConnectionFactory.getPorta());
        assertEquals("treinamais", ConnectionFactory.getDbNome());
        assertEquals("postgres", ConnectionFactory.getUsuario());

        // Testar configuração customizada
        ConnectionFactory.configurar("192.168.1.50", "5433", "treinamais_teste", "user_teste", "pass123");
        assertEquals("192.168.1.50", ConnectionFactory.getIp());
        assertEquals("5433", ConnectionFactory.getPorta());

        // Restaurar padrão
        ConnectionFactory.configurar("10.0.2.2", "5432", "treinamais", "postgres", "postgres");
    }

    @Test
    public void testInstanciacaoDao() {
        ProfessorDAO professorDAO = new ProfessorDAO();
        assertNotNull(professorDAO);

        AlunoDAO alunoDAO = new AlunoDAO();
        assertNotNull(alunoDAO);
    }

    @Test
    public void testModelProfessor() {
        Professor prof = new Professor(1L, 10L, "123456-G/GO", "(62) 99999-1111", "Prof. Silva", "silva@treinamais.com");
        assertEquals(Long.valueOf(1L), prof.getId());
        assertEquals(Long.valueOf(10L), prof.getUsuarioId());
        assertEquals("123456-G/GO", prof.getCref());
        assertEquals("(62) 99999-1111", prof.getTelefone());
        assertEquals("Prof. Silva", prof.getNome());
        assertEquals("silva@treinamais.com", prof.getEmail());
        assertTrue(prof.toString().contains("123456-G/GO"));
    }

    @Test
    public void testModelAluno() {
        Aluno aluno = new Aluno(2L, 20L, 1L, "111.222.333-44", "2000-05-15", "(62) 98888-2222", "Aluno João", "joao@treinamais.com");
        aluno.setNomeProfessor("Prof. Silva");

        assertEquals(Long.valueOf(2L), aluno.getId());
        assertEquals(Long.valueOf(20L), aluno.getUsuarioId());
        assertEquals(Long.valueOf(1L), aluno.getProfessorId());
        assertEquals("111.222.333-44", aluno.getCpf());
        assertEquals("2000-05-15", aluno.getDataNascimento());
        assertEquals("(62) 98888-2222", aluno.getTelefone());
        assertEquals("Aluno João", aluno.getNome());
        assertEquals("Prof. Silva", aluno.getNomeProfessor());
        assertTrue(aluno.toString().contains("111.222.333-44"));
    }

    @Test
    public void testModelUsuario() {
        Usuario usuario = new Usuario("Yuri Silva", "yuri@treinamais.com", "hash_senha", "ALUNO");
        assertEquals("Yuri Silva", usuario.getNome());
        assertEquals("yuri@treinamais.com", usuario.getEmail());
        assertEquals("hash_senha", usuario.getSenha());
        assertEquals("ALUNO", usuario.getTipo());
        assertEquals("ATIVO", usuario.getStatus());
    }
}
