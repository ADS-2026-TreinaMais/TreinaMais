package com.pucgo.edu.treinamais.service;

import com.pucgo.edu.treinamais.client.ExerciseDbClient;
import com.pucgo.edu.treinamais.dto.external.ExerciseDbItemDto;
import com.pucgo.edu.treinamais.dto.external.ExerciseDbMetaDto;
import com.pucgo.edu.treinamais.dto.external.ExerciseDbPageDto;
import com.pucgo.edu.treinamais.dto.response.CatalogoExercicioPageResponse;
import com.pucgo.edu.treinamais.dto.response.CatalogoExercicioResponse;
import com.pucgo.edu.treinamais.dto.response.CatalogoFiltrosResponse;
import com.pucgo.edu.treinamais.exception.ExternalApiException;
import com.pucgo.edu.treinamais.exception.ResourceNotFoundException;
import com.pucgo.edu.treinamais.localModel.CatalogoExercicio;
import com.pucgo.edu.treinamais.repository.CatalogoExercicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogoExercicioServiceTest {

    @Mock
    private ExerciseDbClient exerciseDbClient;

    @Mock
    private CatalogoExercicioRepository repository;

    private CatalogoExercicioService service;

    @BeforeEach
    void setUp() {
        service = new CatalogoExercicioService(exerciseDbClient, repository, 24L);
    }

    @Test
    void deveUsarCacheSemChamarApiQuandoDetalheDentroDoTtl() {
        CatalogoExercicio cache = criarEntidade("EIeI8Vf", "barbell bench press", LocalDateTime.now().minusHours(2));
        when(repository.findByExternalId("EIeI8Vf")).thenReturn(Optional.of(cache));

        CatalogoExercicioResponse response = service.buscarPorExternalId("EIeI8Vf");

        assertEquals("barbell bench press", response.getNome());
        assertEquals(List.of("chest"), response.getBodyParts());
        assertEquals(List.of("Step:1 Deite.", "Step:2 Empurre."), response.getInstrucoes());
        verifyNoInteractions(exerciseDbClient);
    }

    @Test
    void deveBuscarNaApiESalvarQuandoDetalheNaoEstaNoCache() {
        ExerciseDbItemDto externo = criarItemExterno("01qpYSe", "upward facing dog");
        when(repository.findByExternalId("01qpYSe")).thenReturn(Optional.empty());
        when(exerciseDbClient.buscarPorId("01qpYSe")).thenReturn(Optional.of(externo));
        when(repository.save(any(CatalogoExercicio.class))).thenAnswer(inv -> inv.getArgument(0));

        CatalogoExercicioResponse response = service.buscarPorExternalId("01qpYSe");

        assertEquals("01qpYSe", response.getExternalId());
        assertEquals("upward facing dog", response.getNome());
        verify(repository).save(any(CatalogoExercicio.class));
    }

    @Test
    void deveRetornarCacheExpiradoQuandoApiFalhaNoDetalhe() {
        CatalogoExercicio expirado = criarEntidade("EIeI8Vf", "barbell bench press", LocalDateTime.now().minusHours(48));
        when(repository.findByExternalId("EIeI8Vf")).thenReturn(Optional.of(expirado));
        when(exerciseDbClient.buscarPorId("EIeI8Vf")).thenThrow(new ExternalApiException("HTTP 429"));

        CatalogoExercicioResponse response = service.buscarPorExternalId("EIeI8Vf");

        assertEquals("EIeI8Vf", response.getExternalId());
        verify(repository, never()).save(any());
    }

    @Test
    void deveLancarResourceNotFoundQuandoDetalheNaoExiste() {
        when(repository.findByExternalId("inexistente")).thenReturn(Optional.empty());
        when(exerciseDbClient.buscarPorId("inexistente")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorExternalId("inexistente"));
    }

    @Test
    void deveFazerUpsertEmLoteSemDuplicarExistentes() {
        CatalogoExercicio existente = criarEntidade("EIeI8Vf", "old name", LocalDateTime.now().minusDays(2));
        existente.setId(10L);

        ExerciseDbItemDto atualizado = criarItemExterno("EIeI8Vf", "barbell bench press");
        ExerciseDbItemDto novo = criarItemExterno("0CXGHya", "cable crossover");

        ExerciseDbMetaDto meta = new ExerciseDbMetaDto();
        meta.setTotal(2);
        meta.setHasNextPage(false);

        ExerciseDbPageDto pageDto = new ExerciseDbPageDto();
        pageDto.setSuccess(true);
        pageDto.setMeta(meta);
        pageDto.setData(List.of(atualizado, novo));

        when(exerciseDbClient.buscarExercicios("bench", null, null, null, 20, null)).thenReturn(pageDto);
        when(repository.findByExternalIdIn(List.of("EIeI8Vf", "0CXGHya"))).thenReturn(List.of(existente));
        when(repository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        CatalogoExercicioPageResponse response = service.buscarExercicios("bench", null, null, null, null, null, false);

        assertEquals("API", response.getOrigem());
        assertEquals(2, response.getItens().size());
        assertEquals("barbell bench press", existente.getNome());
        assertEquals(10L, existente.getId());
    }

    @Test
    void deveFazerFallbackParaCacheLocalQuandoApiFalhaNaBusca() {
        CatalogoExercicio cache = criarEntidade("EIeI8Vf", "barbell bench press", LocalDateTime.now());
        when(exerciseDbClient.buscarExercicios(any(), any(), any(), any(), anyInt(), any()))
                .thenThrow(new ExternalApiException("HTTP 429"));
        when(repository.buscarPorFiltros(eq("bench"), eq(""), eq(""), eq(""), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(cache)));

        CatalogoExercicioPageResponse response = service.buscarExercicios("bench", null, null, null, 10, null, false);

        assertEquals("CACHE", response.getOrigem());
        assertEquals(1, response.getItens().size());
        assertEquals("EIeI8Vf", response.getItens().get(0).getExternalId());
    }

    @Test
    void deveConsultarApenasCacheQuandoSomenteCacheTrue() {
        CatalogoExercicio cache = criarEntidade("EIeI8Vf", "barbell bench press", LocalDateTime.now());
        when(repository.buscarPorFiltros(eq(""), eq("chest"), eq(""), eq(""), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(cache)));

        CatalogoExercicioPageResponse response = service.buscarExercicios(null, "chest", null, null, 15, null, true);

        assertEquals("CACHE", response.getOrigem());
        assertEquals(1, response.getItens().size());
        verifyNoInteractions(exerciseDbClient);
    }

    @Test
    void deveCachearFiltrosEmMemoriaDentroDoTtl() {
        when(exerciseDbClient.listarBodyParts()).thenReturn(List.of("chest", "back"));
        when(exerciseDbClient.listarMuscles()).thenReturn(List.of("pectorals"));
        when(exerciseDbClient.listarEquipments()).thenReturn(List.of("barbell"));

        CatalogoFiltrosResponse primeira = service.obterFiltros();
        CatalogoFiltrosResponse segunda = service.obterFiltros();

        assertSame(primeira, segunda);
        assertEquals(List.of("chest", "back"), segunda.getBodyParts());
        verify(exerciseDbClient, times(1)).listarBodyParts();
        verify(exerciseDbClient, times(1)).listarMuscles();
        verify(exerciseDbClient, times(1)).listarEquipments();
    }

    private CatalogoExercicio criarEntidade(String externalId, String nome, LocalDateTime atualizadoEm) {
        CatalogoExercicio c = new CatalogoExercicio();
        c.setExternalId(externalId);
        c.setNome(nome);
        c.setGifUrl("https://static.exercisedb.dev/media/" + externalId + ".gif");
        c.setBodyParts("chest");
        c.setEquipments("barbell");
        c.setTargetMuscles("pectorals");
        c.setSecondaryMuscles("triceps,deltoids");
        c.setInstrucoes("Step:1 Deite.\nStep:2 Empurre.");
        c.setAtualizadoEm(atualizadoEm);
        return c;
    }

    private ExerciseDbItemDto criarItemExterno(String externalId, String nome) {
        ExerciseDbItemDto dto = new ExerciseDbItemDto();
        dto.setExerciseId(externalId);
        dto.setName(nome);
        dto.setGifUrl("https://static.exercisedb.dev/media/" + externalId + ".gif");
        dto.setBodyParts(List.of("chest"));
        dto.setEquipments(List.of("barbell"));
        dto.setTargetMuscles(List.of("pectorals"));
        dto.setSecondaryMuscles(List.of("triceps"));
        dto.setInstructions(List.of("Step:1 Posicione-se."));
        return dto;
    }
}
