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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CatalogoExercicioService {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 50;

    private final ExerciseDbClient exerciseDbClient;
    private final CatalogoExercicioRepository repository;
    private final long cacheTtlHours;

    private volatile CatalogoFiltrosResponse filtrosCache;
    private volatile LocalDateTime filtrosAtualizadoEm;

    public CatalogoExercicioService(
            ExerciseDbClient exerciseDbClient,
            CatalogoExercicioRepository repository,
            @Value("${exercisedb.cache.ttl-hours:24}") long cacheTtlHours) {
        this.exerciseDbClient = exerciseDbClient;
        this.repository = repository;
        this.cacheTtlHours = cacheTtlHours;
    }

    @Transactional
    public CatalogoExercicioPageResponse buscarExercicios(
            String nome,
            String bodyPart,
            String muscle,
            String equipment,
            Integer limit,
            String after,
            boolean somenteCache) {
        int pageSize = normalizarLimit(limit);

        if (somenteCache) {
            return buscarNoCacheLocal(nome, bodyPart, muscle, equipment, pageSize);
        }

        try {
            ExerciseDbPageDto paginaExterna = exerciseDbClient.buscarExercicios(
                    nome, bodyPart, muscle, equipment, pageSize, after);
            List<ExerciseDbItemDto> dados = paginaExterna.getData() != null
                    ? paginaExterna.getData()
                    : Collections.emptyList();

            List<CatalogoExercicio> salvos = upsertEmLote(dados);
            List<CatalogoExercicioResponse> itens = salvos.stream()
                    .map(this::converterParaResponse)
                    .collect(Collectors.toList());

            ExerciseDbMetaDto meta = paginaExterna.getMeta();
            long total = meta != null && meta.getTotal() != null ? meta.getTotal() : itens.size();
            boolean hasNext = meta != null && Boolean.TRUE.equals(meta.getHasNextPage());
            String nextCursor = meta != null ? meta.getNextCursor() : null;

            return new CatalogoExercicioPageResponse(itens, total, hasNext, nextCursor, "API");
        } catch (ExternalApiException ex) {
            // Fallback automático para o banco local em caso de rate limit (429), timeout ou indisponibilidade.
            return buscarNoCacheLocal(nome, bodyPart, muscle, equipment, pageSize);
        }
    }

    @Transactional
    public CatalogoExercicioResponse buscarPorExternalId(String externalId) {
        Optional<CatalogoExercicio> emCache = repository.findByExternalId(externalId);
        if (emCache.isPresent() && estaDentroDoTtl(emCache.get().getAtualizadoEm())) {
            return converterParaResponse(emCache.get());
        }

        try {
            ExerciseDbItemDto itemExterno = exerciseDbClient.buscarPorId(externalId)
                    .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado no catálogo: " + externalId));

            CatalogoExercicio entidade = emCache.orElseGet(CatalogoExercicio::new);
            preencherEntidade(entidade, itemExterno);
            return converterParaResponse(repository.save(entidade));
        } catch (ExternalApiException ex) {
            if (emCache.isPresent()) {
                return converterParaResponse(emCache.get());
            }
            throw ex;
        }
    }

    public CatalogoFiltrosResponse obterFiltros() {
        if (filtrosCache != null && estaDentroDoTtl(filtrosAtualizadoEm)) {
            return filtrosCache;
        }

        try {
            CatalogoFiltrosResponse atualizados = new CatalogoFiltrosResponse(
                    exerciseDbClient.listarBodyParts(),
                    exerciseDbClient.listarMuscles(),
                    exerciseDbClient.listarEquipments()
            );
            this.filtrosCache = atualizados;
            this.filtrosAtualizadoEm = LocalDateTime.now();
            return atualizados;
        } catch (ExternalApiException ex) {
            if (filtrosCache != null) {
                return filtrosCache;
            }
            throw ex;
        }
    }

    private CatalogoExercicioPageResponse buscarNoCacheLocal(
            String nome,
            String bodyPart,
            String muscle,
            String equipment,
            int pageSize) {
        Page<CatalogoExercicio> page = repository.buscarPorFiltros(
                normalizarFiltro(nome),
                normalizarFiltro(bodyPart),
                normalizarFiltro(muscle),
                normalizarFiltro(equipment),
                PageRequest.of(0, pageSize)
        );

        List<CatalogoExercicioResponse> itens = page.getContent().stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());

        return new CatalogoExercicioPageResponse(itens, page.getTotalElements(), page.hasNext(), null, "CACHE");
    }

    private List<CatalogoExercicio> upsertEmLote(List<ExerciseDbItemDto> itensExternos) {
        if (itensExternos.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> ids = itensExternos.stream()
                .map(ExerciseDbItemDto::getExerciseId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<String, CatalogoExercicio> existentesPorId = repository.findByExternalIdIn(ids).stream()
                .collect(Collectors.toMap(CatalogoExercicio::getExternalId, Function.identity()));

        List<CatalogoExercicio> paraSalvar = new ArrayList<>();
        for (ExerciseDbItemDto dto : itensExternos) {
            if (dto.getExerciseId() == null || dto.getExerciseId().isBlank()) {
                continue;
            }
            CatalogoExercicio entidade = existentesPorId.getOrDefault(dto.getExerciseId(), new CatalogoExercicio());
            preencherEntidade(entidade, dto);
            existentesPorId.put(dto.getExerciseId(), entidade);
            paraSalvar.add(entidade);
        }

        return repository.saveAll(paraSalvar);
    }

    private void preencherEntidade(CatalogoExercicio entidade, ExerciseDbItemDto dto) {
        entidade.setExternalId(dto.getExerciseId());
        entidade.setNome(dto.getName());
        entidade.setGifUrl(dto.getGifUrl());
        entidade.setBodyParts(joinList(dto.getBodyParts(), ","));
        entidade.setEquipments(joinList(dto.getEquipments(), ","));
        entidade.setTargetMuscles(joinList(dto.getTargetMuscles(), ","));
        entidade.setSecondaryMuscles(joinList(dto.getSecondaryMuscles(), ","));
        entidade.setInstrucoes(joinList(dto.getInstructions(), "\n"));
        entidade.setAtualizadoEm(LocalDateTime.now());
    }

    private CatalogoExercicioResponse converterParaResponse(CatalogoExercicio entidade) {
        return new CatalogoExercicioResponse(
                entidade.getExternalId(),
                entidade.getNome(),
                entidade.getGifUrl(),
                splitList(entidade.getBodyParts(), ","),
                splitList(entidade.getEquipments(), ","),
                splitList(entidade.getTargetMuscles(), ","),
                splitList(entidade.getSecondaryMuscles(), ","),
                splitList(entidade.getInstrucoes(), "\n")
        );
    }

    private boolean estaDentroDoTtl(LocalDateTime atualizadoEm) {
        return atualizadoEm != null && atualizadoEm.plusHours(cacheTtlHours).isAfter(LocalDateTime.now());
    }

    private int normalizarLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private String normalizarFiltro(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private String joinList(List<String> values, String delimiter) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return values.stream()
                .filter(v -> v != null && !v.isBlank())
                .map(String::trim)
                .collect(Collectors.joining(delimiter));
    }

    private List<String> splitList(String raw, String delimiter) {
        if (raw == null || raw.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(raw.split(delimiter))
                .map(String::trim)
                .filter(v -> !v.isEmpty())
                .collect(Collectors.toList());
    }
}
