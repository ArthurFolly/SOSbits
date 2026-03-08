package com.sosbits.helpdesk.service;

import com.sosbits.helpdesk.model.LogAcesso;
import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.repository.LogAcessoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogAcessoService {

    private final LogAcessoRepository repository;

    public LogAcessoService(LogAcessoRepository repository) {
        this.repository = repository;
    }

    public void registrarLogin(Usuario usuario, String perfil) {
        LogAcesso log = new LogAcesso();
        log.setUsuarioId(usuario.getId());
        log.setNomeUsuario(usuario.getNome());
        log.setEmailUsuario(usuario.getEmail());
        log.setPerfilUsuario(perfil);
        log.setDataHoraLogin(LocalDateTime.now());
        repository.save(log);
    }

    public void registrarLogout(Long usuarioId) {
        repository.findTopByUsuarioIdAndDataHoraLogoutIsNullOrderByDataHoraLoginDesc(usuarioId)
                .ifPresent(log -> {
                    log.setDataHoraLogout(LocalDateTime.now());
                    repository.save(log);
                });
    }

    public List<LogAcesso> listarTodos() {
        return repository.findAllByOrderByDataHoraLoginDesc();
    }
}