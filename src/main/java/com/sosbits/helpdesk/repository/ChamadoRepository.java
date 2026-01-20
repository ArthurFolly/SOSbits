package com.sosbits.helpdesk.repository;

import com.sosbits.helpdesk.model.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {
    // Busca os 5 mais recentes para o dashboard
    List<Chamado> findFirst5ByOrderByDataCriacaoDesc();

    // Conta por status para os cards do dashboard
    long countByStatus(String status);
}