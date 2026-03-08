package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.service.LogAcessoService;
import com.sosbits.helpdesk.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/logs-acesso")
public class LogAcessoController {

    private final LogAcessoService logAcessoService;
    private final UsuarioService usuarioService;

    public LogAcessoController(
            LogAcessoService logAcessoService,
            UsuarioService usuarioService
    ) {
        this.logAcessoService = logAcessoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarLogs(@AuthenticationPrincipal UserDetails userDetails, Model model) {

        model.addAttribute("logs", logAcessoService.listarTodos());

        if (userDetails != null) {
            Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());

            if (usuario != null) {
                model.addAttribute("usuarioNome", usuario.getNome());
                model.addAttribute("usuarioPerfil", usuario.getPerfis()
                        .stream()
                        .findFirst()
                        .map(p -> p.getNome())
                        .orElse("USUARIO"));
            }
        }

        return "logs-acesso";
    }
}