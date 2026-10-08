package com.pucgo.edu.treinamais.controller;

import com.pucgo.edu.treinamais.dto.request.AtualizarTreinoRequest;
import com.pucgo.edu.treinamais.dto.request.CriarTreinoRequest;
import com.pucgo.edu.treinamais.dto.response.MessageResponse;
import com.pucgo.edu.treinamais.dto.response.TreinoResponse;
import com.pucgo.edu.treinamais.service.TreinoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treinos")
public class TreinoController {

    private final TreinoService treinoService;

    public TreinoController(TreinoService treinoService) {
        this.treinoService = treinoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<TreinoResponse> criarTreino(
            @Valid @RequestBody CriarTreinoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        TreinoResponse response = treinoService.criarTreino(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TreinoResponse>> listarTreinos(@AuthenticationPrincipal UserDetails userDetails) {
        List<TreinoResponse> treinos = treinoService.listarTreinosDoUsuario(userDetails.getUsername());
        return ResponseEntity.ok(treinos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TreinoResponse> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        TreinoResponse treino = treinoService.buscarPorId(id, userDetails.getUsername());
        return ResponseEntity.ok(treino);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<TreinoResponse> atualizarTreino(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarTreinoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        TreinoResponse atualizado = treinoService.atualizarTreino(id, request, userDetails.getUsername());
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<MessageResponse> excluirTreino(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        treinoService.excluirTreino(id, userDetails.getUsername());
        return ResponseEntity.ok(new MessageResponse("Treino excluído com sucesso."));
    }

    @PostMapping("/{treinoId}/alunos/{alunoId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<MessageResponse> associarAluno(
            @PathVariable Long treinoId,
            @PathVariable Long alunoId,
            @AuthenticationPrincipal UserDetails userDetails) {
        treinoService.associarAlunoAoTreino(treinoId, alunoId, userDetails.getUsername());
        return ResponseEntity.ok(new MessageResponse("Aluno associado ao treino com sucesso."));
    }

    @DeleteMapping("/{treinoId}/alunos/{alunoId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<MessageResponse> desassociarAluno(
            @PathVariable Long treinoId,
            @PathVariable Long alunoId,
            @AuthenticationPrincipal UserDetails userDetails) {
        treinoService.desassociarAlunoDoTreino(treinoId, alunoId, userDetails.getUsername());
        return ResponseEntity.ok(new MessageResponse("Aluno desassociado do treino com sucesso."));
    }
}
