package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.service.UsuarioAdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioAdminController {

    private final UsuarioAdminService usuarioAdminService;

    public UsuarioAdminController(UsuarioAdminService usuarioAdminService) {
        this.usuarioAdminService = usuarioAdminService;
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) Long perfilId,
            Model model
    ) {
        model.addAttribute("usuarios", usuarioAdminService.listarFiltrando(ativo, perfilId));
        model.addAttribute("usuariosExcluidos", usuarioAdminService.listarInativos());

        model.addAttribute("usuario", new Usuario());
        model.addAttribute("modoEdicao", false);

        return "usuario";
    }

    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) Long perfilId,
            Model model
    ) {
        model.addAttribute("usuarios", usuarioAdminService.listarFiltrando(ativo, perfilId));
        model.addAttribute("usuariosExcluidos", usuarioAdminService.listarInativos());

        model.addAttribute("usuario", usuarioAdminService.buscarPorId(id));
        model.addAttribute("modoEdicao", true);

        return "usuario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario) {
        usuarioAdminService.salvar(usuario);
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
}