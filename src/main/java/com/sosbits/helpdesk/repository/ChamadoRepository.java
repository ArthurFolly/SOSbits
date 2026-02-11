package com.sosbits.helpdesk.repository;

import com.sosbits.helpdesk.model.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    /* =========================
       LISTAGENS
       ========================= */

    // ✅ Ativos (deletado = false)
    List<Chamado> findAllByDeletadoFalseOrderByIdDesc();

    // ✅ Excluídos (deletado = true)
    List<Chamado> findAllByDeletadoTrueOrderByIdDesc();

    // ✅ 5 mais recentes (somente ativos)
    List<Chamado> findFirst5ByDeletadoFalseOrderByDataCriacaoDesc();

    /* =========================
       CONTADORES (somente ativos)
       ========================= */

    long countByStatusAndDeletadoFalse(String status);

    long countByPrioridadeAndDeletadoFalse(String prioridade);
}
