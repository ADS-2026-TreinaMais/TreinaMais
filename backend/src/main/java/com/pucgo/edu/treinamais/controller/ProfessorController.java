package com.pucgo.edu.treinamais.controller;

import com.pucgo.edu.treinamais.dto.response.AlunoResponse;
import com.pucgo.edu.treinamais.dto.response.ProfessorMetricasResponse;
import com.pucgo.edu.treinamais.dto.response.ProfessorResponse;
import com.pucgo.edu.treinamais.service.ProfessorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professores")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @GetMapping("/me")
    public ResponseEntity<ProfessorResponse> obterMeuPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        ProfessorResponse professor = professorService.obterPerfilProfessor(userDetails.getUsername());
        return ResponseEntity.ok(professor);
    }

    @GetMapping("/me/metricas")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ProfessorMetricasResponse> obterMinhasMetricas(@AuthenticationPrincipal UserDetails userDetails) {
        ProfessorMetricasResponse metricas = professorService.obterMetricas(userDetails.getUsername());
        return ResponseEntity.ok(metricas);
    }

    @GetMapping
    public ResponseEntity<List<ProfessorResponse>> listarProfessores() {
        List<ProfessorResponse> professores = professorService.listarProfessores();
        return ResponseEntity.ok(professores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessorResponse> buscarPorId(@PathVariable Long id) {
        ProfessorResponse professor = professorService.buscarPorId(id);
        return ResponseEntity.ok(professor);
    }

    @PostMapping("/alunos/{alunoId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<AlunoResponse> vincularAluno(
            @PathVariable Long alunoId,
            @AuthenticationPrincipal UserDetails userDetails) {
        AlunoResponse aluno = professorService.vincularAluno(alunoId, userDetails.getUsername());
        return ResponseEntity.ok(aluno);
    }

    @DeleteMapping("/alunos/{alunoId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<AlunoResponse> desvincularAluno(
            @PathVariable Long alunoId,
            @AuthenticationPrincipal UserDetails userDetails) {
        AlunoResponse aluno = professorService.desvincularAluno(alunoId, userDetails.getUsername());
        return ResponseEntity.ok(aluno);
    }
}
