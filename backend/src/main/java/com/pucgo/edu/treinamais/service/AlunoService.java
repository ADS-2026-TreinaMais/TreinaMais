package com.pucgo.edu.treinamais.service;

import com.pucgo.edu.treinamais.dto.response.AlunoResponse;
import com.pucgo.edu.treinamais.exception.ResourceNotFoundException;
import com.pucgo.edu.treinamais.model.Aluno;
import com.pucgo.edu.treinamais.model.Professor;
import com.pucgo.edu.treinamais.model.TipoUsuario;
import com.pucgo.edu.treinamais.model.Usuario;
import com.pucgo.edu.treinamais.repository.AlunoRepository;
import com.pucgo.edu.treinamais.repository.ProfessorRepository;
import com.pucgo.edu.treinamais.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final UsuarioRepository usuarioRepository;

    public AlunoService(
            AlunoRepository alunoRepository,
            ProfessorRepository professorRepository,
            UsuarioRepository usuarioRepository) {
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public AlunoResponse obterPerfilAluno(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + email));

        Aluno aluno = alunoRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de aluno não encontrado"));

        return converterParaResponse(aluno);
    }

    @Transactional(readOnly = true)
    public List<AlunoResponse> listarAlunos(String emailSolicitante) {
        Usuario usuario = usuarioRepository.findByEmail(emailSolicitante)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (usuario.getTipo() == TipoUsuario.PROFESSOR) {
            Professor prof = professorRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil de professor não encontrado"));
            return alunoRepository.findByProfessorId(prof.getId()).stream()
                    .map(this::converterParaResponse)
                    .collect(Collectors.toList());
        }

        return alunoRepository.findAll().stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AlunoResponse buscarPorId(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com id: " + id));
        return converterParaResponse(aluno);
    }

    @Transactional
    public AlunoResponse vincularProfessor(Long alunoId, Long professorId) {
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com id: " + alunoId));

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com id: " + professorId));

        aluno.setProfessor(professor);
        Aluno salvo = alunoRepository.save(aluno);
        return converterParaResponse(salvo);
    }

    @Transactional
    public AlunoResponse desvincularProfessor(Long alunoId) {
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com id: " + alunoId));

        aluno.setProfessor(null);
        Aluno salvo = alunoRepository.save(aluno);
        return converterParaResponse(salvo);
    }

    public AlunoResponse converterParaResponse(Aluno aluno) {
        String nome = aluno.getUsuario() != null ? aluno.getUsuario().getNome() : "";
        String email = aluno.getUsuario() != null ? aluno.getUsuario().getEmail() : "";
        Long usuarioId = aluno.getUsuario() != null ? aluno.getUsuario().getId() : null;

        Long profId = aluno.getProfessor() != null ? aluno.getProfessor().getId() : null;
        String profNome = aluno.getProfessor() != null && aluno.getProfessor().getUsuario() != null
                ? aluno.getProfessor().getUsuario().getNome()
                : null;

        return new AlunoResponse(
                aluno.getId(),
                usuarioId,
                nome,
                email,
                aluno.getTelefone(),
                aluno.getCpf(),
                aluno.getDataNascimento(),
                profId,
                profNome,
                aluno.getCriadoEm()
        );
    }
}
