package com.pucgo.edu.treinamais.repository;

import com.pucgo.edu.treinamais.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByUsuarioId(Long usuarioId);
    Optional<Professor> findByCref(String cref);
    boolean existsByCref(String cref);
}
