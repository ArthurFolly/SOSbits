package com.sosbits.helpdesk.repository;

import com.sosbits.helpdesk.model.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    // 5 chamados mais recentes
    List<Chamado> findFirst5ByOrderByDataCriacaoDesc();

    // Contagem por status
    long countByStatus(String status);

    // Contagem por prioridade
    long countByPrioridade(String prioridade);
}
