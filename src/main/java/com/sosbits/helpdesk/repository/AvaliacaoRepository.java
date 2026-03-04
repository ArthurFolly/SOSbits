package com.sosbits.helpdesk.repository;

import com.sosbits.helpdesk.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findByAtivaTrueOrderByDataAvaliacaoDesc();
    List<Avaliacao> findByAtivaFalseOrderByDataDesativacaoDesc();

    Optional<Avaliacao> findByChamadoId(Long chamadoId);

    boolean existsByChamadoId(Long chamadoId);
}