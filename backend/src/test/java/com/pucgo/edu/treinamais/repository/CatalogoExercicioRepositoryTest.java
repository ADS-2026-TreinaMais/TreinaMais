package com.pucgo.edu.treinamais.repository;

import com.pucgo.edu.treinamais.localModel.CatalogoExercicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("dev")
class CatalogoExercicioRepositoryTest {

    @Autowired
    private CatalogoExercicioRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        CatalogoExercicio supino = new CatalogoExercicio();
        supino.setExternalId("EIeI8Vf");
        supino.setNome("barbell bench press");
        supino.setGifUrl("https://static.exercisedb.dev/media/EIeI8Vf.gif");
        supino.setBodyParts("chest");
        supino.setEquipments("barbell");
        supino.setTargetMuscles("pectorals");
        supino.setSecondaryMuscles("triceps,deltoids");
        supino.setInstrucoes("Step:1 Deite no banco.\nStep:2 Empurre a barra.");

        CatalogoExercicio remada = new CatalogoExercicio();
        remada.setExternalId("01qpYSe");
        remada.setNome("cable seated row");
        remada.setGifUrl("https://static.exercisedb.dev/media/01qpYSe.gif");
        remada.setBodyParts("back");
        remada.setEquipments("cable");
        remada.setTargetMuscles("latissimus dorsi");
        remada.setSecondaryMuscles("biceps");
        remada.setInstrucoes("Step:1 Puxe o cabo.");

        repository.saveAll(List.of(supino, remada));
    }

    @Test
    void deveBuscarPorExternalIdEPreencherAtualizadoEm() {
        Optional<CatalogoExercicio> encontrado = repository.findByExternalId("EIeI8Vf");

        assertTrue(encontrado.isPresent());
        assertEquals("barbell bench press", encontrado.get().getNome());
        assertNotNull(encontrado.get().getAtualizadoEm());
    }

    @Test
    void deveBuscarEmLotePorExternalIds() {
        List<CatalogoExercicio> lista = repository.findByExternalIdIn(List.of("EIeI8Vf", "01qpYSe", "inexistente"));

        assertEquals(2, lista.size());
    }

    @Test
    void deveFiltrarPorNomeBodyPartMuscleEEquipment() {
        Page<CatalogoExercicio> todos = repository.buscarPorFiltros("", "", "", "", PageRequest.of(0, 10));
        assertEquals(2, todos.getTotalElements());

        Page<CatalogoExercicio> porPeito = repository.buscarPorFiltros("bench", "chest", "pectorals", "barbell", PageRequest.of(0, 10));
        assertEquals(1, porPeito.getTotalElements());
        assertEquals("EIeI8Vf", porPeito.getContent().get(0).getExternalId());

        Page<CatalogoExercicio> semResultado = repository.buscarPorFiltros("squat", "", "", "", PageRequest.of(0, 10));
        assertEquals(0, semResultado.getTotalElements());
    }
}
