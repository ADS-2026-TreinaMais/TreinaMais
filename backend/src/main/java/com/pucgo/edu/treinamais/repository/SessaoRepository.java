package com.pucgo.edu.treinamais.repository;

import com.pucgo.edu.treinamais.model.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, Long> {
    Optional<Sessao> findByTokenHash(String tokenHash);
    Optional<Sessao> findByTokenHashAndRevogadoEmIsNull(String tokenHash);
    List<Sessao> findByUsuarioId(Long usuarioId);
    List<Sessao> findByUsuarioIdAndRevogadoEmIsNull(Long usuarioId);
}
