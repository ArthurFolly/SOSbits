package com.sosbits.helpdesk.security;

import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.repository.UsuarioRepository;
import com.sosbits.helpdesk.service.LogAcessoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;
    private final LogAcessoService logAcessoService;

    public LoginSuccessHandler(
            UsuarioRepository usuarioRepository,
            LogAcessoService logAcessoService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.logAcessoService = logAcessoService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        String email = authentication.getName();

        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario != null) {

            String perfil = usuario.getPerfis()
                    .stream()
                    .findFirst()
                    .map(p -> p.getNome())
                    .orElse("USUARIO");

            logAcessoService.registrarLogin(usuario, perfil);
        }

        response.sendRedirect("/chamados/dashboard");
    }
}