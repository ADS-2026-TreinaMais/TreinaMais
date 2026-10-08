package com.pucgo.edu.treinamais.service;

import com.pucgo.edu.treinamais.dto.request.AtualizarTreinoRequest;
import com.pucgo.edu.treinamais.dto.request.CriarTreinoRequest;
import com.pucgo.edu.treinamais.dto.request.ExercicioRequest;
import com.pucgo.edu.treinamais.dto.response.ExercicioResponse;
import com.pucgo.edu.treinamais.dto.response.TreinoResponse;
import com.pucgo.edu.treinamais.exception.ResourceNotFoundException;
import com.pucgo.edu.treinamais.model.*;
import com.pucgo.edu.treinamais.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TreinoService {

    private final TreinoRepository treinoRepository;
    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;
    private final AlunoTreinoRepository alunoTreinoRepository;
    private final UsuarioRepository usuarioRepository;

    public TreinoService(
            TreinoRepository treinoRepository,
            ProfessorRepository professorRepository,
            AlunoRepository alunoRepository,
            AlunoTreinoRepository alunoTreinoRepository,
            UsuarioRepository usuarioRepository) {
        this.treinoRepository = treinoRepository;
        this.professorRepository = professorRepository;
        this.alunoRepository = alunoRepository;
        this.alunoTreinoRepository = alunoTreinoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public TreinoResponse criarTreino(CriarTreinoRequest request, String emailUsuario) {
        Professor professor = obterProfessorPorEmail(emailUsuario);

        validarRegrasExercicios(request.getExercicios());

        Treino treino = new Treino(professor, request.getNome(), request.getDescricao(), request.getObjetivo());

        for (ExercicioRequest exReq : request.getExercicios()) {
            ExercicioTreino ex = new ExercicioTreino(
                    treino,
                    exReq.getNomeExercicio(),
                    exReq.getSeries(),
                    exReq.getRepeticoes(),
                    exReq.getCarga(),
                    exReq.getDescansoSegundos(),
                    exReq.getOrdem()
            );
            ex.setObservacoes(exReq.getObservacoes());
            treino.adicionarExercicio(ex);
        }

        Treino salvo = treinoRepository.save(treino);

        if (request.getAlunoIds() != null && !request.getAlunoIds().isEmpty()) {
            for (Long alunoId : request.getAlunoIds()) {
                Aluno aluno = alunoRepository.findById(alunoId)
                        .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com id: " + alunoId));
                AlunoTreino vinculo = new AlunoTreino(aluno, salvo, LocalDate.now());
                alunoTreinoRepository.save(vinculo);
            }
        }

        return converterParaResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<TreinoResponse> listarTreinosDoUsuario(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (usuario.getTipo() == TipoUsuario.PROFESSOR) {
            Professor prof = professorRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil de professor não encontrado"));
            return treinoRepository.findByProfessorId(prof.getId()).stream()
                    .map(this::converterParaResponse)
                    .collect(Collectors.toList());
        } else {
            Aluno aluno = alunoRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil de aluno não encontrado"));
            return alunoTreinoRepository.findByAlunoIdAndAtivoTrue(aluno.getId()).stream()
                    .map(AlunoTreino::getTreino)
                    .map(this::converterParaResponse)
                    .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public TreinoResponse buscarPorId(Long id, String emailUsuario) {
        Treino treino = treinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado com id: " + id));

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (usuario.getTipo() == TipoUsuario.PROFESSOR) {
            Professor prof = professorRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil de professor não encontrado"));
            if (!treino.getProfessor().getId().equals(prof.getId())) {
                throw new IllegalArgumentException("Acesso negado: você só pode visualizar treinos criados por você mesmo (RN05).");
            }
        } else {
            Aluno aluno = alunoRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil de aluno não encontrado"));
            boolean associado = alunoTreinoRepository.existsByAlunoIdAndTreinoIdAndAtivoTrue(aluno.getId(), id);
            if (!associado) {
                throw new IllegalArgumentException("Acesso negado: o aluno só pode visualizar treinos a ele atribuídos (RN02).");
            }
        }

        return converterParaResponse(treino);
    }

    @Transactional
    public TreinoResponse atualizarTreino(Long id, AtualizarTreinoRequest request, String emailUsuario) {
        Treino treino = treinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado com id: " + id));

        Professor professor = obterProfessorPorEmail(emailUsuario);
        if (!treino.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException("Acesso negado: você só pode editar treinos criados por você mesmo (RN05).");
        }

        validarRegrasExercicios(request.getExercicios());

        treino.setNome(request.getNome());
        treino.setDescricao(request.getDescricao());
        treino.setObjetivo(request.getObjetivo());
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            treino.setStatus(request.getStatus());
        }

        treino.getExercicios().clear();
        for (ExercicioRequest exReq : request.getExercicios()) {
            ExercicioTreino ex = new ExercicioTreino(
                    treino,
                    exReq.getNomeExercicio(),
                    exReq.getSeries(),
                    exReq.getRepeticoes(),
                    exReq.getCarga(),
                    exReq.getDescansoSegundos(),
                    exReq.getOrdem()
            );
            ex.setObservacoes(exReq.getObservacoes());
            treino.adicionarExercicio(ex);
        }

        Treino atualizado = treinoRepository.save(treino);
        return converterParaResponse(atualizado);
    }

    @Transactional
    public void excluirTreino(Long id, String emailUsuario) {
        Treino treino = treinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado com id: " + id));

        Professor professor = obterProfessorPorEmail(emailUsuario);
        if (!treino.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException("Acesso negado: você só pode excluir treinos criados por você mesmo (RN05).");
        }

        treinoRepository.delete(treino);
    }

    @Transactional
    public void associarAlunoAoTreino(Long treinoId, Long alunoId, String emailUsuario) {
        Treino treino = treinoRepository.findById(treinoId)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado com id: " + treinoId));

        Professor professor = obterProfessorPorEmail(emailUsuario);
        if (!treino.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException("Acesso negado: você só pode associar alunos aos seus próprios treinos (RN05).");
        }

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com id: " + alunoId));

        if (!alunoTreinoRepository.existsByAlunoIdAndTreinoIdAndAtivoTrue(alunoId, treinoId)) {
            AlunoTreino vinculo = new AlunoTreino(aluno, treino, LocalDate.now());
            alunoTreinoRepository.save(vinculo);
        }
    }

    @Transactional
    public void desassociarAlunoDoTreino(Long treinoId, Long alunoId, String emailUsuario) {
        Treino treino = treinoRepository.findById(treinoId)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado com id: " + treinoId));

        Professor professor = obterProfessorPorEmail(emailUsuario);
        if (!treino.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException("Acesso negado: você só pode desassociar alunos dos seus próprios treinos (RN05).");
        }

        alunoTreinoRepository.deleteByAlunoIdAndTreinoId(alunoId, treinoId);
    }

    private Professor obterProfessorPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + email));
        if (usuario.getTipo() != TipoUsuario.PROFESSOR) {
            throw new IllegalArgumentException("Somente usuários com perfil professor podem gerenciar treinos (RN01).");
        }
        return professorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de professor não encontrado para o usuário: " + email));
    }

    private void validarRegrasExercicios(List<ExercicioRequest> exercicios) {
        if (exercicios == null || exercicios.isEmpty()) {
            throw new IllegalArgumentException("Um treino só pode ser salvo se tiver pelo menos 1 exercício cadastrado (RN03).");
        }

        for (ExercicioRequest ex : exercicios) {
            if (ex.getSeries() == null || ex.getSeries() <= 0) {
                throw new IllegalArgumentException("Todo exercício deve ter série maior que zero (RN04).");
            }
            if (ex.getRepeticoes() == null || ex.getRepeticoes().isBlank()) {
                throw new IllegalArgumentException("Todo exercício deve ter repetição definida (RN04).");
            }
        }
    }

    private TreinoResponse converterParaResponse(Treino treino) {
        List<ExercicioResponse> exerciciosResp = treino.getExercicios().stream()
                .map(e -> new ExercicioResponse(
                        e.getId(),
                        e.getNomeExercicio(),
                        e.getSeries(),
                        e.getRepeticoes(),
                        e.getCarga(),
                        e.getDescansoSegundos(),
                        e.getOrdem(),
                        e.getObservacoes()
                ))
                .collect(Collectors.toList());

        List<Long> alunosIds = alunoTreinoRepository.findByTreinoId(treino.getId()).stream()
                .map(at -> at.getAluno().getId())
                .collect(Collectors.toList());

        String profNome = treino.getProfessor() != null && treino.getProfessor().getUsuario() != null
                ? treino.getProfessor().getUsuario().getNome()
                : "Professor";

        Long profId = treino.getProfessor() != null ? treino.getProfessor().getId() : null;

        return new TreinoResponse(
                treino.getId(),
                treino.getNome(),
                treino.getDescricao(),
                treino.getObjetivo(),
                treino.getStatus(),
                treino.getCriadoEm(),
                profId,
                profNome,
                exerciciosResp,
                alunosIds
        );
    }
}
