package com.pucgo.edu.treinamais;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pucgo.edu.treinamais.client.ExerciseDbClient;
import com.pucgo.edu.treinamais.dto.external.ExerciseDbItemDto;
import com.pucgo.edu.treinamais.dto.external.ExerciseDbMetaDto;
import com.pucgo.edu.treinamais.dto.external.ExerciseDbPageDto;
import com.pucgo.edu.treinamais.dto.request.CriarTreinoRequest;
import com.pucgo.edu.treinamais.dto.request.ExercicioRequest;
import com.pucgo.edu.treinamais.dto.request.LoginRequest;
import com.pucgo.edu.treinamais.dto.request.RegisterRequest;
import com.pucgo.edu.treinamais.dto.response.AuthResponse;
import com.pucgo.edu.treinamais.exception.ExternalApiException;
import com.pucgo.edu.treinamais.model.TipoUsuario;
import com.pucgo.edu.treinamais.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class CatalogoExercicioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CatalogoExercicioRepository catalogoRepository;

    @Autowired
    private TreinoRepository treinoRepository;

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @MockBean
    private ExerciseDbClient exerciseDbClient;

    private String tokenProfessor;

    @BeforeEach
    void setUp() throws Exception {
        catalogoRepository.deleteAll();
        treinoRepository.deleteAll();
        sessaoRepository.deleteAll();
        alunoRepository.deleteAll();
        professorRepository.deleteAll();
        usuarioRepository.deleteAll();

        tokenProfessor = cadastrarELogarProfessor();
    }

    @Test
    void deveExigirAutenticacaoNosEndpointsDeCatalogo() throws Exception {
        mockMvc.perform(get("/api/catalogo/exercicios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveBuscarExerciciosDaApiEPersistirNoBancoEUsarCacheNoDetalhe() throws Exception {
        ExerciseDbItemDto item = new ExerciseDbItemDto();
        item.setExerciseId("EIeI8Vf");
        item.setName("barbell bench press");
        item.setGifUrl("https://static.exercisedb.dev/media/EIeI8Vf.gif");
        item.setBodyParts(List.of("chest"));
        item.setEquipments(List.of("barbell"));
        item.setTargetMuscles(List.of("pectorals"));
        item.setSecondaryMuscles(List.of("triceps"));
        item.setInstructions(List.of("Step:1 Deite no banco.", "Step:2 Empurre a barra."));

        ExerciseDbMetaDto meta = new ExerciseDbMetaDto();
        meta.setTotal(1);
        meta.setHasNextPage(false);

        ExerciseDbPageDto page = new ExerciseDbPageDto();
        page.setSuccess(true);
        page.setMeta(meta);
        page.setData(List.of(item));

        when(exerciseDbClient.buscarExercicios(eq("bench"), isNull(), isNull(), isNull(), eq(10), isNull()))
                .thenReturn(page);

        mockMvc.perform(get("/api/catalogo/exercicios")
                        .param("nome", "bench")
                        .param("limit", "10")
                        .header("Authorization", "Bearer " + tokenProfessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origem").value("API"))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.itens[0].externalId").value("EIeI8Vf"))
                .andExpect(jsonPath("$.itens[0].gifUrl").value("https://static.exercisedb.dev/media/EIeI8Vf.gif"));

        assertEquals(1, catalogoRepository.count());

        // Consulta de detalhe deve retornar diretamente do cache H2 sem chamar a API externa.
        mockMvc.perform(get("/api/catalogo/exercicios/EIeI8Vf")
                        .header("Authorization", "Bearer " + tokenProfessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("barbell bench press"))
                .andExpect(jsonPath("$.instrucoes[0]").value("Step:1 Deite no banco."));

        verify(exerciseDbClient, never()).buscarPorId("EIeI8Vf");
    }

    @Test
    void deveFazerFallbackParaCacheQuandoApiExternaFalhar() throws Exception {
        ExerciseDbItemDto item = new ExerciseDbItemDto();
        item.setExerciseId("0CXGHya");
        item.setName("Cable Crossover");
        item.setGifUrl("https://static.exercisedb.dev/media/0CXGHya.gif");
        item.setBodyParts(List.of("chest"));
        item.setEquipments(List.of("cable"));
        item.setTargetMuscles(List.of("pectorals"));
        item.setInstructions(List.of("Step:1 Ajuste as polias."));

        ExerciseDbPageDto page = new ExerciseDbPageDto();
        page.setSuccess(true);
        page.setData(List.of(item));

        when(exerciseDbClient.buscarExercicios(any(), any(), any(), any(), anyInt(), any()))
                .thenReturn(page)
                .thenThrow(new ExternalApiException("Rate limit 429"));

        mockMvc.perform(get("/api/catalogo/exercicios")
                        .param("bodyPart", "chest")
                        .header("Authorization", "Bearer " + tokenProfessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origem").value("API"));

        mockMvc.perform(get("/api/catalogo/exercicios")
                        .param("bodyPart", "chest")
                        .header("Authorization", "Bearer " + tokenProfessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origem").value("CACHE"))
                .andExpect(jsonPath("$.itens[0].externalId").value("0CXGHya"));
    }

    @Test
    void deveRetornarFiltrosDisponiveis() throws Exception {
        when(exerciseDbClient.listarBodyParts()).thenReturn(List.of("chest", "back"));
        when(exerciseDbClient.listarMuscles()).thenReturn(List.of("pectorals"));
        when(exerciseDbClient.listarEquipments()).thenReturn(List.of("barbell"));

        mockMvc.perform(get("/api/catalogo/filtros")
                        .header("Authorization", "Bearer " + tokenProfessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bodyParts[0]").value("chest"))
                .andExpect(jsonPath("$.muscles[0]").value("pectorals"))
                .andExpect(jsonPath("$.equipments[0]").value("barbell"));
    }

    @Test
    void deveCriarTreinoComExercicioImportadoDaApi() throws Exception {
        ExercicioRequest exReq = new ExercicioRequest("barbell bench press", 4, "8-10", "60kg", 90, 1, "Controlar descida");
        exReq.setExercicioExternoId("EIeI8Vf");
        exReq.setGifUrl("https://static.exercisedb.dev/media/EIeI8Vf.gif");
        exReq.setInstrucoes("Step:1 Deite no banco.\nStep:2 Empurre a barra.");

        CriarTreinoRequest treinoReq = new CriarTreinoRequest();
        treinoReq.setNome("Treino A - Peito");
        treinoReq.setObjetivo("Hipertrofia");
        treinoReq.setExercicios(List.of(exReq));

        mockMvc.perform(post("/api/treinos")
                        .header("Authorization", "Bearer " + tokenProfessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(treinoReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exercicios[0].exercicioExternoId").value("EIeI8Vf"))
                .andExpect(jsonPath("$.exercicios[0].gifUrl").value("https://static.exercisedb.dev/media/EIeI8Vf.gif"))
                .andExpect(jsonPath("$.exercicios[0].instrucoes").value("Step:1 Deite no banco.\nStep:2 Empurre a barra."));
    }

    private String cadastrarELogarProfessor() throws Exception {
        RegisterRequest reg = new RegisterRequest();
        reg.setNome("Prof. Catalogo");
        reg.setEmail("prof.catalogo@treinamais.com");
        reg.setSenha("senha123");
        reg.setTipo(TipoUsuario.PROFESSOR);
        reg.setCref("999888-G/GO");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest("prof.catalogo@treinamais.com", "senha123");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse auth = objectMapper.readValue(result.getResponse().getContentAsString(), AuthResponse.class);
        return auth.getToken();
    }
}
