package com.pucgo.edu.treinamais.repository;

import com.pucgo.edu.treinamais.model.Treino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreinoRepository extends JpaRepository<Treino, Long> {
    List<Treino> findByProfessorId(Long professorId);
    List<Treino> findByStatus(String status);
}
