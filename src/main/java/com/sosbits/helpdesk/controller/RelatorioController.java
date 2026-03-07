package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Chamado;
import com.sosbits.helpdesk.repository.AvaliacaoRepository;
import com.sosbits.helpdesk.service.ChamadoService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RelatorioController {

    private final ChamadoService chamadoService;
    private final AvaliacaoRepository avaliacaoRepository;

    public RelatorioController(ChamadoService chamadoService,
                               AvaliacaoRepository avaliacaoRepository) {
        this.chamadoService = chamadoService;
        this.avaliacaoRepository = avaliacaoRepository;
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
       RELATORIO POR AVALIACOES
       SOMENTE ADMIN
       ========================================== */

    @GetMapping("/relatorios/avaliacoes")
    public String relatorioAvaliacoes(Model model,
                                      @AuthenticationPrincipal UserDetails user) {

        String perfil = extrairPerfil(user);

        if (!perfil.equals("ADMIN")) {
            return "redirect:/chamados/dashboard";
        }

        long estrela1 = avaliacaoRepository.countByNotaAndAtivaTrue(1);
        long estrela2 = avaliacaoRepository.countByNotaAndAtivaTrue(2);
        long estrela3 = avaliacaoRepository.countByNotaAndAtivaTrue(3);
        long estrela4 = avaliacaoRepository.countByNotaAndAtivaTrue(4);
        long estrela5 = avaliacaoRepository.countByNotaAndAtivaTrue(5);

        long total = estrela1 + estrela2 + estrela3 + estrela4 + estrela5;

        model.addAttribute("usuarioPerfil", perfil);
        model.addAttribute("e1", estrela1);
        model.addAttribute("e2", estrela2);
        model.addAttribute("e3", estrela3);
        model.addAttribute("e4", estrela4);
        model.addAttribute("e5", estrela5);
        model.addAttribute("total", total);

        return "relatorio-avaliacoes";
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

    /* ==========================================
       RELATORIO POR USUARIO
       ========================================== */

    @GetMapping("/relatorios/usuarios")
    public String relatorioUsuarios(Model model,
                                    @AuthenticationPrincipal UserDetails user) {

        String perfil = extrairPerfil(user);

        if (!(perfil.equals("ADMIN") || perfil.equals("SUPORTE"))) {
            return "redirect:/chamados/dashboard";
        }

        List<Chamado> chamados = chamadoService.listarTodos();

        Map<String, Map<String, Long>> estatisticas = new LinkedHashMap<>();

        for (Chamado c : chamados) {

            if (c.getSolicitante() == null) continue;

            String nomeUsuario = c.getSolicitante().getNome();

            estatisticas.putIfAbsent(nomeUsuario, new HashMap<>());

            Map<String, Long> dados = estatisticas.get(nomeUsuario);

            dados.putIfAbsent("ABERTO", 0L);
            dados.putIfAbsent("EM_ANDAMENTO", 0L);
            dados.putIfAbsent("PENDENTE", 0L);
            dados.putIfAbsent("FECHADO", 0L);

            String status = normalizarStatus(c.getStatus());

            dados.put(status, dados.get(status) + 1);
        }

        model.addAttribute("usuarioPerfil", perfil);
        model.addAttribute("estatisticas", estatisticas);

        return "relatorio-usuarios";
    }
}