package com.sosbits.helpdesk.repository;

import com.sosbits.helpdesk.model.LogAcesso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LogAcessoRepository extends JpaRepository<LogAcesso, Long> {

    Optional<LogAcesso> findTopByUsuarioIdAndDataHoraLogoutIsNullOrderByDataHoraLoginDesc(Long usuarioId);

    List<LogAcesso> findAllByOrderByDataHoraLoginDesc();
}