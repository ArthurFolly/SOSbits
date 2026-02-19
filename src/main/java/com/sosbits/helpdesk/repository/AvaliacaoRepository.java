package com.sosbits.helpdesk.repository;

import com.sosbits.helpdesk.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    // buscar avaliação por chamado (se você quiser limitar 1 avaliação por chamado)
    Optional<Avaliacao> findByChamadoId(Long idChamado);

    // listar avaliações de um usuário (se quiser)
    List<Avaliacao> findByUsuarioIdOrderByDataAvaliacaoDesc(Long idUsuario);
}
