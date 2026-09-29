package com.pucgo.edu.treinamais;

import com.google.gson.Gson;
import com.pucgo.edu.treinamais.model.entity.UserSessionEntity;
import com.pucgo.edu.treinamais.network.dto.AuthResponseDto;
import com.pucgo.edu.treinamais.network.dto.LoginRequestDto;
import com.pucgo.edu.treinamais.network.dto.RegisterRequestDto;

import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class SessionSecurityTest {

    private final Gson gson = new Gson();

    @Test
    public void testUserSessionEntityExpiracao() {
        long agora = System.currentTimeMillis();

        UserSessionEntity sessaoAtiva = new UserSessionEntity(
                1L, "Professor Teste", "prof@teste.com", "PROFESSOR", "ATIVO",
                "jwt.token.valido", agora + 3600000L
        );
        assertFalse("Sessão válida não deve estar expirada", sessaoAtiva.isExpired());

        UserSessionEntity sessaoExpirada = new UserSessionEntity(
                2L, "Aluno Teste", "aluno@teste.com", "ALUNO", "ATIVO",
                "jwt.token.expirado", agora - 5000L
        );
        assertTrue("Sessão passada deve ser detectada como expirada", sessaoExpirada.isExpired());
    }

    @Test
    public void testManifestPermissoesDeclaradas() throws Exception {
        File manifestFile = new File("src/main/AndroidManifest.xml");
        assertTrue("Arquivo AndroidManifest.xml deve existir", manifestFile.exists());

        String conteudo = new String(Files.readAllBytes(manifestFile.toPath()));
        assertTrue("Permissão INTERNET deve estar declarada", conteudo.contains("android.permission.INTERNET"));
        assertTrue("Permissão ACCESS_NETWORK_STATE deve estar declarada", conteudo.contains("android.permission.ACCESS_NETWORK_STATE"));
        assertTrue("Configuração usesCleartextTraffic deve estar habilitada para comunicação local", conteudo.contains("android:usesCleartextTraffic=\"true\""));
    }

    @Test
    public void testSerializationAuthResponseDto() {
        String json = "{\n" +
                "  \"token\": \"mock_jwt_token_12345\",\n" +
                "  \"tipoToken\": \"Bearer\",\n" +
                "  \"id\": 10,\n" +
                "  \"nome\": \"Dr. Yuri\",\n" +
                "  \"email\": \"yuri@treinamais.com\",\n" +
                "  \"tipo\": \"PROFESSOR\",\n" +
                "  \"status\": \"ATIVO\",\n" +
                "  \"expiraEm\": 1790794676000\n" +
                "}";

        AuthResponseDto dto = gson.fromJson(json, AuthResponseDto.class);
        assertNotNull(dto);
        assertEquals("mock_jwt_token_12345", dto.getToken());
        assertEquals("Bearer", dto.getTipoToken());
        assertEquals(Long.valueOf(10L), dto.getId());
        assertEquals("Dr. Yuri", dto.getNome());
        assertEquals("yuri@treinamais.com", dto.getEmail());
        assertEquals("PROFESSOR", dto.getTipo());
        assertEquals("ATIVO", dto.getStatus());
        assertEquals(Long.valueOf(1790794676000L), dto.getExpiraEm());
    }

    @Test
    public void testSerializationLoginRequestDto() {
        LoginRequestDto req = new LoginRequestDto("aluno@treinamais.com", "senhaForte@123");
        String json = gson.toJson(req);

        assertTrue(json.contains("\"email\":\"aluno@treinamais.com\""));
        assertTrue(json.contains("\"senha\":\"senhaForte@123\""));
    }

    @Test
    public void testSerializationRegisterRequestDto() {
        RegisterRequestDto req = new RegisterRequestDto(
                "Carlos Aluno",
                "carlos@treinamais.com",
                "senha123",
                "ALUNO",
                null,
                "123.456.789-00",
                "(62) 98888-0000"
        );
        String json = gson.toJson(req);

        assertTrue(json.contains("\"tipo\":\"ALUNO\""));
        assertTrue(json.contains("\"cpf\":\"123.456.789-00\""));
    }

    @Test
    public void testLogoutSimulacaoLimpezaSessao() {
        UserSessionEntity sessao = new UserSessionEntity(
                1L, "Usuario Teste", "teste@treinamais.com", "ALUNO", "ATIVO",
                "token_ativo", System.currentTimeMillis() + 60000L
        );
        assertFalse(sessao.isExpired());

        sessao.setToken(null);
        sessao.setExpiraEm(0L);

        assertNull("Token deve ser nulo após logout", sessao.getToken());
        assertTrue("Sessão deve estar expirada/inválida após logout", sessao.isExpired());
    }
}
