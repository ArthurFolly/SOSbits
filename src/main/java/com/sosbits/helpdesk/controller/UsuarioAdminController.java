package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.repository.PerfilRepository;
import com.sosbits.helpdesk.service.UsuarioAdminService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioAdminController {

    private final UsuarioAdminService usuarioAdminService;
    private final PerfilRepository perfilRepository;

    public UsuarioAdminController(UsuarioAdminService usuarioAdminService,
                                  PerfilRepository perfilRepository) {
        this.usuarioAdminService = usuarioAdminService;
        this.perfilRepository = perfilRepository;
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