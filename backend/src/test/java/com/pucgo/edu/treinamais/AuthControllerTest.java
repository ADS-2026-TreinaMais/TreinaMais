package com.pucgo.edu.treinamais;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pucgo.edu.treinamais.dto.request.LoginRequest;
import com.pucgo.edu.treinamais.dto.request.RefreshTokenRequest;
import com.pucgo.edu.treinamais.dto.request.RegisterRequest;
import com.pucgo.edu.treinamais.dto.response.AuthResponse;
import com.pucgo.edu.treinamais.model.StatusUsuario;
import com.pucgo.edu.treinamais.model.TipoUsuario;
import com.pucgo.edu.treinamais.model.Usuario;
import com.pucgo.edu.treinamais.repository.AlunoRepository;
import com.pucgo.edu.treinamais.repository.ProfessorRepository;
import com.pucgo.edu.treinamais.repository.SessaoRepository;
import com.pucgo.edu.treinamais.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        sessaoRepository.deleteAll();
        alunoRepository.deleteAll();
        professorRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve cadastrar um Aluno com sucesso e salvar senha criptografada (hash BCrypt)")
    void deveCadastrarAlunoComSucesso() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setNome("Lucas Aluno");
        request.setEmail("lucas@teste.com");
        request.setSenha("senha123");
        request.setTipo(TipoUsuario.ALUNO);
        request.setCpf("123.456.789-00");
        request.setTelefone("(62) 99999-0000");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensagem").value("Usuário cadastrado com sucesso!"));

        Usuario usuario = usuarioRepository.findByEmail("lucas@teste.com").orElse(null);
        assertNotNull(usuario);
        assertEquals("Lucas Aluno", usuario.getNome());
        assertEquals(TipoUsuario.ALUNO, usuario.getTipo());
        assertEquals(StatusUsuario.ATIVO, usuario.getStatus());
        assertTrue(passwordEncoder.matches("senha123", usuario.getSenhaHash()), "A senha deve ser compatível com BCrypt");
        assertNotEquals("senha123", usuario.getSenhaHash(), "A senha pura nunca deve ser salva no banco");

        assertTrue(alunoRepository.existsByCpf("123.456.789-00"));
    }

    @Test
    @DisplayName("Deve cadastrar um Professor com sucesso exigindo CREF")
    void deveCadastrarProfessorComSucesso() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setNome("Prof. Carlos Silva");
        request.setEmail("carlos@treinamais.com");
        request.setSenha("prof1234");
        request.setTipo(TipoUsuario.PROFESSOR);
        request.setCref("123456-G/GO");
        request.setTelefone("(62) 98888-7777");

        mockMvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensagem").value("Usuário cadastrado com sucesso!"));

        assertTrue(professorRepository.existsByCref("123456-G/GO"));
    }

    @Test
    @DisplayName("Deve recusar cadastro de professor sem CREF")
    void deveRecusarCadastroProfessorSemCref() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setNome("Prof. Sem CREF");
        request.setEmail("invalido@treinamais.com");
        request.setSenha("prof1234");
        request.setTipo(TipoUsuario.PROFESSOR);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Erro: O CREF é obrigatório para cadastro de professores."));
    }

    @Test
    @DisplayName("Deve recusar cadastro com email duplicado")
    void deveRecusarCadastroComEmailDuplicado() throws Exception {
        RegisterRequest req1 = new RegisterRequest("Usuario 1", "duplicado@teste.com", "senha123", TipoUsuario.ALUNO);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated());

        RegisterRequest req2 = new RegisterRequest("Usuario 2", "duplicado@teste.com", "outrasenha", TipoUsuario.ALUNO);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Erro: E-mail já cadastrado!"));
    }

    @Test
    @DisplayName("Deve autenticar (login) com sucesso, gerar token JWT e registrar sessão ativa no banco")
    void deveRealizarLoginComSucesso() throws Exception {
        RegisterRequest regReq = new RegisterRequest("Mariana", "mariana@teste.com", "segredo123", TipoUsuario.ALUNO);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated());

        LoginRequest loginReq = new LoginRequest("mariana@teste.com", "segredo123");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.email").value("mariana@teste.com"))
                .andExpect(jsonPath("$.tipo").value("ALUNO"))
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(result.getResponse().getContentAsString(), AuthResponse.class);
        assertNotNull(authResponse.getToken());

        assertEquals(1, sessaoRepository.count(), "Deve existir 1 registro de sessão no banco");
        var sessao = sessaoRepository.findAll().get(0);
        assertTrue(sessao.isValida(), "Sessão deve estar válida");
        assertNull(sessao.getRevogadoEm(), "Sessão nova não deve estar revogada");
        assertNotNull(sessao.getTokenHash(), "Hash do token deve estar presente");
    }

    @Test
    @DisplayName("Deve recusar login com senha incorreta")
    void deveRecusarLoginComSenhaIncorreta() throws Exception {
        RegisterRequest regReq = new RegisterRequest("Mariana", "mariana@teste.com", "segredo123", TipoUsuario.ALUNO);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated());

        LoginRequest loginReq = new LoginRequest("mariana@teste.com", "senha_errada");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").value("E-mail ou senha inválidos"));
    }

    @Test
    @DisplayName("Deve renovar (refresh) token JWT com sucesso, revogando sessão anterior e criando nova")
    void deveRenovarTokenComSucesso() throws Exception {
        RegisterRequest regReq = new RegisterRequest("Renato", "renato@teste.com", "senha123", TipoUsuario.ALUNO);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated());

        LoginRequest loginReq = new LoginRequest("renato@teste.com", "senha123");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse loginAuth = objectMapper.readValue(loginResult.getResponse().getContentAsString(), AuthResponse.class);
        String tokenOriginal = loginAuth.getToken();

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh")
                        .header("Authorization", "Bearer " + tokenOriginal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andReturn();

        AuthResponse refreshAuth = objectMapper.readValue(refreshResult.getResponse().getContentAsString(), AuthResponse.class);
        String novoToken = refreshAuth.getToken();
        assertNotEquals(tokenOriginal, novoToken, "O novo token deve ser diferente do anterior");

        assertEquals(2, sessaoRepository.count());
        long sessoesAtivas = sessaoRepository.findAll().stream().filter(s -> s.isValida()).count();
        assertEquals(1, sessoesAtivas, "Deve haver exatamente 1 sessão ativa após renovação");
    }

    @Test
    @DisplayName("Deve realizar logout, revogar a sessão no banco e bloquear acessos subsequentes")
    void deveRealizarLogoutERevogarSessao() throws Exception {
        RegisterRequest regReq = new RegisterRequest("Luciana", "luciana@teste.com", "senha123", TipoUsuario.ALUNO);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated());

        LoginRequest loginReq = new LoginRequest("luciana@teste.com", "senha123");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse loginAuth = objectMapper.readValue(loginResult.getResponse().getContentAsString(), AuthResponse.class);
        String token = loginAuth.getToken();

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("luciana@teste.com"));

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Logout realizado com sucesso! Sessão encerrada."));

        var sessao = sessaoRepository.findAll().get(0);
        assertNotNull(sessao.getRevogadoEm(), "A sessão deve ter data de revogação");
        assertFalse(sessao.isValida(), "A sessão não deve mais ser válida");

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }
}
