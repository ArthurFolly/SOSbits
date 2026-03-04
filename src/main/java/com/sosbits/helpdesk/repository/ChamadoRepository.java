package com.sosbits.helpdesk.repository;

import com.sosbits.helpdesk.model.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    // =========================
    // LISTAGENS PADRÃO
    // =========================

    List<Chamado> findAllByDeletadoFalseOrderByIdDesc();

    List<Chamado> findAllByDeletadoTrueOrderByIdDesc();

    List<Chamado> findFirst5ByDeletadoFalseOrderByDataCriacaoDesc();

    long countByStatusAndDeletadoFalse(String status);

    long countByPrioridadeAndDeletadoFalse(String prioridade);

    // =========================
    // BUSCAR COM RELACIONAMENTOS
    // =========================

    @Query("""
           SELECT c
           FROM Chamado c
           LEFT JOIN FETCH c.solicitante
           LEFT JOIN FETCH c.atendente
           WHERE c.id = :id
             AND c.deletado = false
           """)
    Optional<Chamado> findByIdComUsuarios(@Param("id") Long id);

    // =========================
    // CHAMADOS FECHADOS E NÃO AVALIADOS
    // =========================
    // usado no combo do modal Avaliação

    @Query("""
        SELECT c
        FROM Chamado c
        WHERE c.deletado = false
          AND c.solicitante.id = :idUsuario
          AND UPPER(TRIM(c.status)) IN ('FECHADO','ENCERRADO','FINALIZADO','RESOLVIDO')
          AND NOT EXISTS (
              SELECT 1
              FROM Avaliacao a
              WHERE a.chamado.id = c.id
                AND a.ativa = true
          )
        ORDER BY c.dataCriacao DESC
    """)
    List<Chamado> listarFechadosNaoAvaliadosDoSolicitante(
            @Param("idUsuario") Long idUsuario
    );

    // =========================
    // CHAMADOS POR SOLICITANTE
    // =========================

    List<Chamado> findBySolicitanteIdAndDeletadoFalseOrderByDataCriacaoDesc(Long idUsuario);
}