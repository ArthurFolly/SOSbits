package com.sosbits.helpdesk.repository;

import com.sosbits.helpdesk.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Lista categorias ativas ordenadas por ID decrescente
    List<Categoria> findByDeletadoFalseOrderByIdDesc();

    // Lista categorias excluídas (soft delete)
    List<Categoria> findByDeletadoTrueOrderByIdDesc();

    // Verifica se já existe categoria com o mesmo nome
    boolean existsByNomeIgnoreCaseAndDeletadoFalse(String nome);
}