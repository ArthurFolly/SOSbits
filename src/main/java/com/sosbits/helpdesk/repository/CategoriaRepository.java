package com.sosbits.helpdesk.repository;

import com.sosbits.helpdesk.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByDeletadoFalseOrderByIdDesc();

    List<Categoria> findByDeletadoTrueOrderByIdDesc();

    boolean existsByNomeIgnoreCaseAndDeletadoFalse(String nome);

    boolean existsByNomeIgnoreCaseAndDeletadoFalseAndIdNot(String nome, Long id);
}