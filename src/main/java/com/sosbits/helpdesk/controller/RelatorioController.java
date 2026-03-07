package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Chamado;
import com.sosbits.helpdesk.service.ChamadoService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;
import java.util.List;

@Controller
public class RelatorioController {

    private final ChamadoService chamadoService;

    public RelatorioController(ChamadoService chamadoService) {
        this.chamadoService = chamadoService;
    }

    /* ==========================================
       RELATORIO GERAL
       ========================================== */

    @GetMapping("/relatorios")
    public String relatorioGeral(Model model,
                                 @AuthenticationPrincipal UserDetails user) {

        String perfil = extrairPerfil(user);

        List<Chamado> chamados;

        if (perfil.equals("ADMIN") || perfil.equals("SUPORTE")) {
            chamados = chamadoService.listarTodos();
        } else {
            chamados = chamadoService.listarDoUsuarioLogado();
        }

        model.addAttribute("usuarioPerfil", perfil);
        model.addAttribute("chamados", chamados);

        return "relatorios";
    }

    /* ==========================================
       RELATORIO POR STATUS
       ========================================== */

    @GetMapping("/relatorios/status")
    public String relatorioStatus(Model model,
                                  @AuthenticationPrincipal UserDetails user) {

        String perfil = extrairPerfil(user);

        List<Chamado> chamados;

        if (perfil.equals("ADMIN") || perfil.equals("SUPORTE")) {
            chamados = chamadoService.listarTodos();
        } else {
            chamados = chamadoService.listarDoUsuarioLogado();
        }

        long abertos = chamados.stream()
                .filter(c -> normalizarStatus(c.getStatus()).equals("ABERTO"))
                .count();

        long andamento = chamados.stream()
                .filter(c -> normalizarStatus(c.getStatus()).equals("EM_ANDAMENTO"))
                .count();

        long pendentes = chamados.stream()
                .filter(c -> normalizarStatus(c.getStatus()).equals("PENDENTE"))
                .count();

        long fechados = chamados.stream()
                .filter(c -> normalizarStatus(c.getStatus()).equals("FECHADO"))
                .count();

        model.addAttribute("usuarioPerfil", perfil);

        model.addAttribute("abertos", abertos);
        model.addAttribute("andamento", andamento);
        model.addAttribute("pendentes", pendentes);
        model.addAttribute("fechados", fechados);

        return "relatorio-status";
    }

    /* ==========================================
       PERFIL DO USUARIO
       ========================================== */

    private String extrairPerfil(UserDetails user) {

        if (user == null) return "CONVIDADO";

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        for (GrantedAuthority authority : authorities) {

            String role = authority.getAuthority();

            if (role.equals("ROLE_ADMIN")) return "ADMIN";
            if (role.equals("ROLE_SUPORTE")) return "SUPORTE";
            if (role.equals("ROLE_USUARIO")) return "USUARIO";
        }

        return "USUARIO";
    }

    /* ==========================================
       NORMALIZA STATUS
       ========================================== */

    private String normalizarStatus(String status) {

        if (status == null) return "";

        String s = status.trim().toUpperCase();

        if (s.equals("ABERTO")) return "ABERTO";
        if (s.equals("EM ANDAMENTO") || s.equals("EM_ANDAMENTO")) return "EM_ANDAMENTO";
        if (s.equals("PENDENTE")) return "PENDENTE";
        if (s.equals("FECHADO") || s.equals("RESOLVIDO")) return "FECHADO";

        return s;
    }
}