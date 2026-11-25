package com.pucgo.edu.treinamais.repository;

import com.pucgo.edu.treinamais.localModel.CatalogoExercicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CatalogoExercicioRepository extends JpaRepository<CatalogoExercicio, Long> {

    Optional<CatalogoExercicio> findByExternalId(String externalId);

    List<CatalogoExercicio> findByExternalIdIn(Collection<String> externalIds);

    // Parâmetros vazios ("") em vez de null evitam erro de inferência de tipo do PostgreSQL em LIKE.
    @Query("SELECT c FROM CatalogoExercicio c WHERE " +
            "(:nome = '' OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
            "(:bodyPart = '' OR LOWER(COALESCE(c.bodyParts, '')) LIKE LOWER(CONCAT('%', :bodyPart, '%'))) AND " +
            "(:muscle = '' OR LOWER(COALESCE(c.targetMuscles, '')) LIKE LOWER(CONCAT('%', :muscle, '%'))) AND " +
            "(:equipment = '' OR LOWER(COALESCE(c.equipments, '')) LIKE LOWER(CONCAT('%', :equipment, '%')))")
    Page<CatalogoExercicio> buscarPorFiltros(
            @Param("nome") String nome,
            @Param("bodyPart") String bodyPart,
            @Param("muscle") String muscle,
            @Param("equipment") String equipment,
            Pageable pageable
    );
}
