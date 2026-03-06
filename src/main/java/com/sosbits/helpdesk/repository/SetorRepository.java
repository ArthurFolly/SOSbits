package com.sosbits.helpdesk.repository;

import com.sosbits.helpdesk.model.Setor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SetorRepository extends JpaRepository<Setor, Long> {

    List<Setor> findAllByDeletadoFalseOrderByIdDesc();

    List<Setor> findAllByDeletadoTrueOrderByIdDesc();

    Optional<Setor> findByIdAndDeletadoFalse(Long id);

    Optional<Setor> findByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndDeletadoFalse(String nome);
}