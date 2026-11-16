package com.pucgo.edu.treinamais;

import com.pucgo.edu.treinamais.dao.AlunoDAO_jv;
import com.pucgo.edu.treinamais.dao.ProfessorDAO_jv;
import com.pucgo.edu.treinamais.model.Aluno_jv;
import com.pucgo.edu.treinamais.model.ConnectionFactory;
import com.pucgo.edu.treinamais.model.Professor_jv;
import com.pucgo.edu.treinamais.model.Usuario_jv;

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

        ConnectionFactory.configurar("192.168.1.50", "5433", "treinamais_teste", "user_teste", "pass123");
        assertEquals("192.168.1.50", ConnectionFactory.getIp());
        assertEquals("5433", ConnectionFactory.getPorta());

        ConnectionFactory.configurar("10.0.2.2", "5432", "treinamais", "postgres", "postgres");
    }

    @Test
    public void testInstanciacaoDao() {
        ProfessorDAO_jv professorDAO = new ProfessorDAO_jv();
        assertNotNull(professorDAO);

        AlunoDAO_jv alunoDAO = new AlunoDAO_jv();
        assertNotNull(alunoDAO);
    }

    @Test
    public void testModelProfessor() {
        Professor_jv prof = new Professor_jv(1L, 10L, "123456-G/GO", "(62) 99999-1111", "Prof. Silva", "silva@treinamais.com");
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
        Aluno_jv aluno = new Aluno_jv(2L, 20L, 1L, "111.222.333-44", "2000-05-15", "(62) 98888-2222", "Aluno João", "joao@treinamais.com");
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
        Usuario_jv usuario = new Usuario_jv("Yuri Silva", "yuri@treinamais.com", "hash_senha", "ALUNO");
        assertEquals("Yuri Silva", usuario.getNome());
        assertEquals("yuri@treinamais.com", usuario.getEmail());
        assertEquals("hash_senha", usuario.getSenha());
        assertEquals("ALUNO", usuario.getTipo());
        assertEquals("ATIVO", usuario.getStatus());
    }
}
