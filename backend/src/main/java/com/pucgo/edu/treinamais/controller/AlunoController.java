package com.pucgo.edu.treinamais.controller;

import com.pucgo.edu.treinamais.dto.response.AlunoResponse;
import com.pucgo.edu.treinamais.service.AlunoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping("/me")
    public ResponseEntity<AlunoResponse> obterMeuPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        AlunoResponse aluno = alunoService.obterPerfilAluno(userDetails.getUsername());
        return ResponseEntity.ok(aluno);
    }

    @GetMapping
    public ResponseEntity<List<AlunoResponse>> listarAlunos(@AuthenticationPrincipal UserDetails userDetails) {
        List<AlunoResponse> alunos = alunoService.listarAlunos(userDetails.getUsername());
        return ResponseEntity.ok(alunos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponse> buscarPorId(@PathVariable Long id) {
        AlunoResponse aluno = alunoService.buscarPorId(id);
        return ResponseEntity.ok(aluno);
    }
}
