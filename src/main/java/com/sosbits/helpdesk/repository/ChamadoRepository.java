package com.sosbits.helpdesk.repository;

import com.sosbits.helpdesk.model.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    List<Chamado> findAllByDeletadoFalseOrderByIdDesc();

    List<Chamado> findAllByDeletadoTrueOrderByIdDesc();

    List<Chamado> findFirst5ByDeletadoFalseOrderByDataCriacaoDesc();

    long countByStatusAndDeletadoFalse(String status);

    long countByPrioridadeAndDeletadoFalse(String prioridade);

    @Query("""
           SELECT c
           FROM Chamado c
           LEFT JOIN FETCH c.solicitante
           LEFT JOIN FETCH c.atendente
           LEFT JOIN FETCH c.setor
           WHERE c.id = :id
           """)
    Optional<Chamado> findByIdComUsuarios(@Param("id") Long id);

    @Query("""
           SELECT c
           FROM Chamado c
           LEFT JOIN FETCH c.setor
           WHERE c.deletado = false
           ORDER BY c.id DESC
           """)
    List<Chamado> buscarTodosComSetor();

    @Query("""
           SELECT c
           FROM Chamado c
           LEFT JOIN FETCH c.setor
           WHERE c.deletado = true
           ORDER BY c.id DESC
           """)
    List<Chamado> buscarExcluidosComSetor();

    @Query("""
           SELECT c
           FROM Chamado c
           WHERE c.deletado = false
             AND c.status = 'FECHADO'
             AND c.solicitante.id = :idUsuario
             AND NOT EXISTS (
                 SELECT 1
                 FROM Avaliacao a
                 WHERE a.chamado.id = c.id
             )
           ORDER BY c.dataCriacao DESC
           """)
    List<Chamado> listarFechadosNaoAvaliadosDoSolicitante(@Param("idUsuario") Long idUsuario);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           UPDATE Chamado c
              SET c.deletado = true
            WHERE c.id = :id
           """)
    int excluirLogico(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           UPDATE Chamado c
              SET c.deletado = false
            WHERE c.id = :id
           """)
    int restaurarLogico(@Param("id") Long id);
}