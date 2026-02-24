package com.sosbits.helpdesk.repository;

import com.sosbits.helpdesk.model.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;
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
           select c from Chamado c
           left join fetch c.solicitante
           left join fetch c.atendente
           where c.id = :id and c.deletado = false
           """)
    Optional<Chamado> findByIdComUsuarios(@Param("id") Long id);


    @Query("""
        select c from Chamado c
        where c.deletado = false
          and lower(c.status) = lower(:statusFechado)
          and c.solicitante.id = :idUsuario
          and not exists (
              select 1 from Avaliacao a
              where a.chamado.id = c.id
                and a.ativa = true
          )
        order by c.dataCriacao desc
    """)
    List<Chamado> listarFechadosNaoAvaliadosDoSolicitante(
            @Param("idUsuario") Long idUsuario,
            @Param("statusFechado") String statusFechado
    );



    List<Chamado> findBySolicitanteIdAndDeletadoFalseOrderByDataCriacaoDesc(Long idUsuario);

}