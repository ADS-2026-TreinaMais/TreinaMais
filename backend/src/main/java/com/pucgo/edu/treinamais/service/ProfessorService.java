package com.pucgo.edu.treinamais.service;

import com.pucgo.edu.treinamais.dto.response.AlunoResponse;
import com.pucgo.edu.treinamais.dto.response.ProfessorMetricasResponse;
import com.pucgo.edu.treinamais.dto.response.ProfessorResponse;
import com.pucgo.edu.treinamais.exception.ResourceNotFoundException;
import com.pucgo.edu.treinamais.model.Aluno;
import com.pucgo.edu.treinamais.model.Professor;
import com.pucgo.edu.treinamais.model.TipoUsuario;
import com.pucgo.edu.treinamais.model.Usuario;
import com.pucgo.edu.treinamais.repository.AlunoRepository;
import com.pucgo.edu.treinamais.repository.ProfessorRepository;
import com.pucgo.edu.treinamais.repository.TreinoRepository;
import com.pucgo.edu.treinamais.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;
    private final TreinoRepository treinoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlunoService alunoService;

    public ProfessorService(
            ProfessorRepository professorRepository,
            AlunoRepository alunoRepository,
            TreinoRepository treinoRepository,
            UsuarioRepository usuarioRepository,
            AlunoService alunoService) {
        this.professorRepository = professorRepository;
        this.alunoRepository = alunoRepository;
        this.treinoRepository = treinoRepository;
        this.usuarioRepository = usuarioRepository;
        this.alunoService = alunoService;
    }

    @Transactional(readOnly = true)
    public ProfessorResponse obterPerfilProfessor(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + email));

        Professor professor = professorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de professor não encontrado"));

        return converterParaResponse(professor);
    }

    @Transactional(readOnly = true)
    public List<ProfessorResponse> listarProfessores() {
        return professorRepository.findAll().stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProfessorResponse buscarPorId(Long id) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com id: " + id));
        return converterParaResponse(professor);
    }

    @Transactional(readOnly = true)
    public ProfessorMetricasResponse obterMetricas(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + email));

        Professor professor = professorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de professor não encontrado"));

        List<Aluno> alunos = alunoRepository.findByProfessorId(professor.getId());
        long totalAlunos = alunos.size();
        long totalTreinos = treinoRepository.findByProfessorId(professor.getId()).size();
        long alunosAtivos = alunos.stream()
                .filter(a -> a.getUsuario() != null && "ATIVO".equalsIgnoreCase(a.getUsuario().getStatus().name()))
                .count();

        return new ProfessorMetricasResponse(totalAlunos, totalTreinos, alunosAtivos);
    }

    @Transactional
    public AlunoResponse vincularAluno(Long alunoId, String emailProfessor) {
        Usuario usuario = usuarioRepository.findByEmail(emailProfessor)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + emailProfessor));
        if (usuario.getTipo() != TipoUsuario.PROFESSOR) {
            throw new IllegalArgumentException("Apenas professores podem vincular alunos (RN01).");
        }

        Professor professor = professorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de professor não encontrado"));

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com id: " + alunoId));

        aluno.setProfessor(professor);
        Aluno salvo = alunoRepository.save(aluno);
        return alunoService.converterParaResponse(salvo);
    }

    @Transactional
    public AlunoResponse desvincularAluno(Long alunoId, String emailProfessor) {
        Usuario usuario = usuarioRepository.findByEmail(emailProfessor)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + emailProfessor));

        Professor professor = professorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de professor não encontrado"));

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com id: " + alunoId));

        if (aluno.getProfessor() != null && !aluno.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException("Você só pode desvincular alunos vinculados a você mesmo.");
        }

        aluno.setProfessor(null);
        Aluno salvo = alunoRepository.save(aluno);
        return alunoService.converterParaResponse(salvo);
    }

    private ProfessorResponse converterParaResponse(Professor professor) {
        String nome = professor.getUsuario() != null ? professor.getUsuario().getNome() : "";
        String email = professor.getUsuario() != null ? professor.getUsuario().getEmail() : "";
        Long usuarioId = professor.getUsuario() != null ? professor.getUsuario().getId() : null;

        return new ProfessorResponse(
                professor.getId(),
                usuarioId,
                nome,
                email,
                professor.getCref(),
                professor.getTelefone(),
                professor.getCriadoEm()
        );
    }
}
