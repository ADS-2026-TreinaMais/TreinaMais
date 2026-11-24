package com.pucgo.edu.treinamais.client;

import com.pucgo.edu.treinamais.dto.external.ExerciseDbItemDto;
import com.pucgo.edu.treinamais.dto.external.ExerciseDbNameListDto;
import com.pucgo.edu.treinamais.dto.external.ExerciseDbPageDto;
import com.pucgo.edu.treinamais.dto.external.ExerciseDbSingleDto;
import com.pucgo.edu.treinamais.exception.ExternalApiException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ExerciseDbClient {

    private final RestClient restClient;

    public ExerciseDbClient(@Qualifier("exerciseDbRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    // Na ExerciseDB V1 (oss.exercisedb.dev), todos os filtros e paginação por cursor passam via /exercises.
    public ExerciseDbPageDto buscarExercicios(
            String nome,
            String bodyPart,
            String muscle,
            String equipment,
            Integer limit,
            String after) {
        try {
            ExerciseDbPageDto response = restClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/exercises");
                        if (hasText(nome)) uriBuilder.queryParam("name", nome.trim());
                        if (hasText(bodyPart)) uriBuilder.queryParam("bodyParts", bodyPart.trim());
                        if (hasText(muscle)) uriBuilder.queryParam("targetMuscles", muscle.trim());
                        if (hasText(equipment)) uriBuilder.queryParam("equipments", equipment.trim());
                        if (limit != null && limit > 0) uriBuilder.queryParam("limit", limit);
                        if (hasText(after)) uriBuilder.queryParam("after", after.trim());
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .body(ExerciseDbPageDto.class);

            if (response == null) {
                throw new ExternalApiException("Resposta vazia da API ExerciseDB.");
            }
            return response;
        } catch (RestClientResponseException ex) {
            throw new ExternalApiException("Falha na API ExerciseDB (HTTP " + ex.getStatusCode().value() + ").", ex);
        } catch (RestClientException ex) {
            throw new ExternalApiException("Erro de comunicação com a API ExerciseDB.", ex);
        }
    }

    public Optional<ExerciseDbItemDto> buscarPorId(String externalId) {
        try {
            ExerciseDbSingleDto response = restClient.get()
                    .uri("/exercises/{id}", externalId)
                    .retrieve()
                    .body(ExerciseDbSingleDto.class);

            return Optional.ofNullable(response).map(ExerciseDbSingleDto::getData);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            throw new ExternalApiException("Falha ao consultar exercício na ExerciseDB (HTTP " + ex.getStatusCode().value() + ").", ex);
        } catch (RestClientException ex) {
            throw new ExternalApiException("Erro de comunicação com a API ExerciseDB.", ex);
        }
    }

    public List<String> listarBodyParts() {
        return listarNomes("/bodyparts");
    }

    public List<String> listarMuscles() {
        return listarNomes("/muscles");
    }

    public List<String> listarEquipments() {
        return listarNomes("/equipments");
    }

    private List<String> listarNomes(String path) {
        try {
            ExerciseDbNameListDto response = restClient.get()
                    .uri(path)
                    .retrieve()
                    .body(ExerciseDbNameListDto.class);

            if (response == null || response.getData() == null) {
                return Collections.emptyList();
            }
            return response.getData().stream()
                    .map(ExerciseDbNameListDto.NameItem::getName)
                    .filter(this::hasText)
                    .collect(Collectors.toList());
        } catch (RestClientResponseException ex) {
            throw new ExternalApiException("Falha ao listar filtros da ExerciseDB (HTTP " + ex.getStatusCode().value() + ").", ex);
        } catch (RestClientException ex) {
            throw new ExternalApiException("Erro de comunicação com a API ExerciseDB.", ex);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
