package com.pucgo.edu.treinamais.repository;

import com.pucgo.edu.treinamais.model.ExercicioTreino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExercicioTreinoRepository extends JpaRepository<ExercicioTreino, Long> {
    List<ExercicioTreino> findByTreinoIdOrderByOrdemAsc(Long treinoId);
}
