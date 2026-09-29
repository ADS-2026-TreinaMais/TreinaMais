package com.pucgo.edu.treinamais.repository;

import com.pucgo.edu.treinamais.model.AlunoTreino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlunoTreinoRepository extends JpaRepository<AlunoTreino, Long> {
    List<AlunoTreino> findByAlunoId(Long alunoId);
    List<AlunoTreino> findByAlunoIdAndAtivoTrue(Long alunoId);
    List<AlunoTreino> findByTreinoId(Long treinoId);
}
