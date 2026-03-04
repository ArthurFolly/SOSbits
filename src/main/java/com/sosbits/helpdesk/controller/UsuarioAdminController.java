package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioAdminController {

    private final UsuarioService usuarioService;

    public UsuarioAdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarAtivos());
        model.addAttribute("usuariosExcluidos", usuarioService.listarExcluidos());
        model.addAttribute("usuario", new Usuario());
        return "usuario"; // sem barra
    }


    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("usuarios", usuarioService.listarAtivos());
        model.addAttribute("usuariosExcluidos", usuarioService.listarExcluidos());
        model.addAttribute("usuario", usuarioService.buscarPorId(id));
        return "usuario";
    }


    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario) {
        usuarioService.salvar(usuario);
        return "redirect:/admin/usuarios";
    }


    @GetMapping("/desativar/{id}")
    public String desativar(@PathVariable Long id) {
        usuarioService.desativar(id);
        return "redirect:/admin/usuarios";
    }


    @GetMapping("/restaurar/{id}")
    public String restaurar(@PathVariable Long id) {
        usuarioService.restaurar(id);
        return "redirect:/admin/usuarios";
    }
}
