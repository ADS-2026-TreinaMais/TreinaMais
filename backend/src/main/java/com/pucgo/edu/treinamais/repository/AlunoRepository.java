package com.pucgo.edu.treinamais.repository;

import com.pucgo.edu.treinamais.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Optional<Aluno> findByUsuarioId(Long usuarioId);
    Optional<Aluno> findByCpf(String cpf);
    List<Aluno> findByProfessorId(Long professorId);
    boolean existsByCpf(String cpf);
}
