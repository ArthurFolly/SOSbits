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
}