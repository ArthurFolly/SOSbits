package com.sosbits.helpdesk.security;

import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.repository.UsuarioRepository;
import com.sosbits.helpdesk.service.LogAcessoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class LogoutHandlerCustom implements LogoutHandler {

    private final UsuarioRepository usuarioRepository;
    private final LogAcessoService logAcessoService;

    public LogoutHandlerCustom(
            UsuarioRepository usuarioRepository,
            LogAcessoService logAcessoService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.logAcessoService = logAcessoService;
    }

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        if (authentication == null) {
            return;
        }

        String email = authentication.getName();

        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario != null) {
            logAcessoService.registrarLogout(usuario.getId());
        }
    }
}