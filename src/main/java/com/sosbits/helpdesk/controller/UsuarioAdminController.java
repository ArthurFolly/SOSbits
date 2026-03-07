package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Chamado;
import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.repository.PerfilRepository;
import com.sosbits.helpdesk.service.ChamadoService;
import com.sosbits.helpdesk.service.UsuarioAdminService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioAdminController {

    private final UsuarioAdminService usuarioAdminService;
    private final PerfilRepository perfilRepository;
    private final ChamadoService chamadoService;

    public UsuarioAdminController(UsuarioAdminService usuarioAdminService,
                                  PerfilRepository perfilRepository,
                                  ChamadoService chamadoService) {
        this.usuarioAdminService = usuarioAdminService;
        this.perfilRepository = perfilRepository;
        this.chamadoService = chamadoService;
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) Long perfilId,
            Model model,
            @AuthenticationPrincipal UserDetails user
    ) {
        model.addAttribute("usuarioPerfil", extrairPerfil(user));
        model.addAttribute("usuarios", usuarioAdminService.listarFiltrando(ativo, perfilId));
        model.addAttribute("usuariosExcluidos", usuarioAdminService.listarInativos());
        model.addAttribute("perfis", perfilRepository.findAll());
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("modoEdicao", false);

        carregarEstatisticasUsuarios(model);

        return "usuario";
    }

    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) Long perfilId,
            Model model,
            @AuthenticationPrincipal UserDetails user
    ) {
        model.addAttribute("usuarioPerfil", extrairPerfil(user));
        model.addAttribute("usuarios", usuarioAdminService.listarFiltrando(ativo, perfilId));
        model.addAttribute("usuariosExcluidos", usuarioAdminService.listarInativos());
        model.addAttribute("perfis", perfilRepository.findAll());
        model.addAttribute("usuario", usuarioAdminService.buscarPorId(id));
        model.addAttribute("modoEdicao", true);

        carregarEstatisticasUsuarios(model);

        return "usuario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario,
                         @RequestParam("perfilId") Long perfilId,
                         @RequestParam(value = "senha", required = false) String senha) {

        usuarioAdminService.salvar(usuario, perfilId, senha);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/desativar/{id}")
    public String desativar(@PathVariable Long id) {
        usuarioAdminService.desativar(id);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/restaurar/{id}")
    public String restaurar(@PathVariable Long id) {
        usuarioAdminService.restaurar(id);
        return "redirect:/admin/usuarios";
    }

    private void carregarEstatisticasUsuarios(Model model) {
        List<Chamado> chamados = chamadoService.listarTodos();

        Map<String, Map<String, Long>> estatisticasUsuarios = new LinkedHashMap<>();

        for (Chamado chamado : chamados) {

            if (chamado.getSolicitante() == null || chamado.getSolicitante().getNome() == null) {
                continue;
            }

            String nomeUsuario = chamado.getSolicitante().getNome().trim();
            String statusNormalizado = normalizarStatus(chamado.getStatus());

            estatisticasUsuarios.putIfAbsent(nomeUsuario, criarMapaZerado());

            Map<String, Long> dados = estatisticasUsuarios.get(nomeUsuario);

            dados.put(statusNormalizado, dados.getOrDefault(statusNormalizado, 0L) + 1L);
            dados.put("TOTAL", dados.getOrDefault("TOTAL", 0L) + 1L);
        }

        model.addAttribute("estatisticasUsuarios", estatisticasUsuarios);
    }

    private Map<String, Long> criarMapaZerado() {
        Map<String, Long> dados = new HashMap<>();
        dados.put("ABERTO", 0L);
        dados.put("EM_ANDAMENTO", 0L);
        dados.put("PENDENTE", 0L);
        dados.put("FECHADO", 0L);
        dados.put("TOTAL", 0L);
        return dados;
    }

    private String normalizarStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "ABERTO";
        }

        String valor = status.trim().toUpperCase();

        if (valor.equals("ABERTO")) {
            return "ABERTO";
        }

        if (valor.equals("EM_ANDAMENTO") || valor.equals("EM ANDAMENTO")) {
            return "EM_ANDAMENTO";
        }

        if (valor.equals("PENDENTE")) {
            return "PENDENTE";
        }

        if (valor.equals("FECHADO") || valor.equals("RESOLVIDO")) {
            return "FECHADO";
        }

        return "ABERTO";
    }

    private String extrairPerfil(UserDetails user) {
        if (user == null || user.getAuthorities() == null || user.getAuthorities().isEmpty()) {
            return "CONVIDADO";
        }

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            if ("ROLE_ADMIN".equals(role)) {
                return "ADMIN";
            }
            if ("ROLE_SUPORTE".equals(role)) {
                return "SUPORTE";
            }
            if ("ROLE_USUARIO".equals(role)) {
                return "USUARIO";
            }
        }

        return authorities.iterator().next().getAuthority().replace("ROLE_", "");
    }
}