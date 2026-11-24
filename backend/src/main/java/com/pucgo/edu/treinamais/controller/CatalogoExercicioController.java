package com.pucgo.edu.treinamais.controller;

import com.pucgo.edu.treinamais.dto.response.CatalogoExercicioPageResponse;
import com.pucgo.edu.treinamais.dto.response.CatalogoExercicioResponse;
import com.pucgo.edu.treinamais.dto.response.CatalogoFiltrosResponse;
import com.pucgo.edu.treinamais.service.CatalogoExercicioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogoExercicioController {

    private final CatalogoExercicioService catalogoService;

    public CatalogoExercicioController(CatalogoExercicioService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/exercicios")
    public ResponseEntity<CatalogoExercicioPageResponse> listarExercicios(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String bodyPart,
            @RequestParam(required = false) String muscle,
            @RequestParam(required = false) String equipment,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(required = false) String after,
            @RequestParam(defaultValue = "false") boolean somenteCache) {
        CatalogoExercicioPageResponse response = catalogoService.buscarExercicios(
                nome, bodyPart, muscle, equipment, limit, after, somenteCache);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/exercicios/{externalId}")
    public ResponseEntity<CatalogoExercicioResponse> buscarPorExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(catalogoService.buscarPorExternalId(externalId));
    }

    @GetMapping("/filtros")
    public ResponseEntity<CatalogoFiltrosResponse> obterFiltros() {
        return ResponseEntity.ok(catalogoService.obterFiltros());
    }
}
