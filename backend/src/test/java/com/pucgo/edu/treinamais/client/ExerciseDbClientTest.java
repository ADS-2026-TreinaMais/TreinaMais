package com.pucgo.edu.treinamais.client;

import com.pucgo.edu.treinamais.dto.external.ExerciseDbItemDto;
import com.pucgo.edu.treinamais.dto.external.ExerciseDbPageDto;
import com.pucgo.edu.treinamais.exception.ExternalApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ExerciseDbClientTest {

    private static final String BASE_URL = "https://oss.exercisedb.dev/api/v1";

    private MockRestServiceServer server;
    private ExerciseDbClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        server = MockRestServiceServer.bindTo(builder).build();
        client = new ExerciseDbClient(builder.build());
    }

    @Test
    void deveBuscarExerciciosComFiltrosEPaginacaoPorCursor() {
        String json = """
                {
                  "success": true,
                  "meta": {
                    "total": 87,
                    "hasNextPage": true,
                    "hasPreviousPage": false,
                    "nextCursor": "3d7wHyd"
                  },
                  "data": [
                    {
                      "exerciseId": "0CXGHya",
                      "name": "Cable Crossover",
                      "gifUrl": "https://static.exercisedb.dev/media/0CXGHya.gif",
                      "bodyParts": ["chest"],
                      "equipments": ["cable"],
                      "targetMuscles": ["pectorals"],
                      "secondaryMuscles": ["deltoids", "triceps"],
                      "instructions": ["Step:1 Adjust pulleys.", "Step:2 Bring handles together."]
                    }
                  ]
                }
                """;

        server.expect(requestTo(BASE_URL + "/exercises?name=bench&bodyParts=chest&targetMuscles=pectorals&equipments=cable&limit=10&after=03lzqwk"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        ExerciseDbPageDto page = client.buscarExercicios("bench", "chest", "pectorals", "cable", 10, "03lzqwk");

        server.verify();
        assertTrue(Boolean.TRUE.equals(page.getSuccess()));
        assertEquals(87, page.getMeta().getTotal());
        assertEquals("3d7wHyd", page.getMeta().getNextCursor());
        assertEquals(1, page.getData().size());
        ExerciseDbItemDto item = page.getData().get(0);
        assertEquals("0CXGHya", item.getExerciseId());
        assertEquals("Cable Crossover", item.getName());
        assertEquals("https://static.exercisedb.dev/media/0CXGHya.gif", item.getGifUrl());
        assertEquals(List.of("chest"), item.getBodyParts());
        assertEquals(2, item.getInstructions().size());
    }

    @Test
    void deveBuscarExercicioPorId() {
        String json = """
                {
                  "success": true,
                  "data": {
                    "exerciseId": "01qpYSe",
                    "name": "upward facing dog",
                    "gifUrl": "https://static.exercisedb.dev/media/01qpYSe.gif",
                    "bodyParts": ["back"],
                    "equipments": ["bodyweight"],
                    "targetMuscles": ["erector spinae"],
                    "secondaryMuscles": ["deltoids"],
                    "instructions": ["Step:1 Lie face down."]
                  }
                }
                """;

        server.expect(requestTo(BASE_URL + "/exercises/01qpYSe"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        Optional<ExerciseDbItemDto> result = client.buscarPorId("01qpYSe");

        server.verify();
        assertTrue(result.isPresent());
        assertEquals("upward facing dog", result.get().getName());
    }

    @Test
    void deveRetornarVazioQuandoExercicioNaoEncontrado() {
        server.expect(requestTo(BASE_URL + "/exercises/inexistente"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        Optional<ExerciseDbItemDto> result = client.buscarPorId("inexistente");

        server.verify();
        assertTrue(result.isEmpty());
    }

    @Test
    void deveListarFiltrosDeBodyPartsMusclesEEquipments() {
        server.expect(requestTo(BASE_URL + "/bodyparts"))
                .andRespond(withSuccess("{\"success\":true,\"data\":[{\"name\":\"chest\"},{\"name\":\"back\"}]}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(BASE_URL + "/muscles"))
                .andRespond(withSuccess("{\"success\":true,\"data\":[{\"name\":\"pectorals\"}]}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(BASE_URL + "/equipments"))
                .andRespond(withSuccess("{\"success\":true,\"data\":[{\"name\":\"barbell\"}]}", MediaType.APPLICATION_JSON));

        assertEquals(List.of("chest", "back"), client.listarBodyParts());
        assertEquals(List.of("pectorals"), client.listarMuscles());
        assertEquals(List.of("barbell"), client.listarEquipments());
        server.verify();
    }

    @Test
    void deveLancarExternalApiExceptionQuandoAtingirRateLimitOuErro500() {
        server.expect(requestTo(BASE_URL + "/exercises?limit=5"))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        server.expect(requestTo(BASE_URL + "/exercises/01qpYSe"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        ExternalApiException ex429 = assertThrows(ExternalApiException.class,
                () -> client.buscarExercicios(null, null, null, null, 5, null));
        assertTrue(ex429.getMessage().contains("429"));

        ExternalApiException ex500 = assertThrows(ExternalApiException.class,
                () -> client.buscarPorId("01qpYSe"));
        assertTrue(ex500.getMessage().contains("500"));

        server.verify();
    }
}
