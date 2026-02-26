package com.sosbits.helpdesk.repository;

import com.sosbits.helpdesk.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByDeletadoFalseOrderByNomeAsc();

    Optional<Categoria> findByIdAndDeletadoFalse(Long id);

    boolean existsByNomeIgnoreCaseAndDeletadoFalse(String nome);
}