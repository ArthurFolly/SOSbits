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

    // =========================
    // LISTAR (ATIVOS + EXCLUÍDOS)
    // =========================
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarAtivos());               // ativos (tabela principal)
        model.addAttribute("usuariosExcluidos", usuarioService.listarExcluidos());   // inativos (modal excluídos)
        model.addAttribute("usuario", new Usuario());                                // form (novo usuário)
        return "usuario"; // sem barra
    }

    // =========================
    // EDITAR (mesma tela, mas carregando o usuário no form)
    // =========================
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("usuarios", usuarioService.listarAtivos());
        model.addAttribute("usuariosExcluidos", usuarioService.listarExcluidos());
        model.addAttribute("usuario", usuarioService.buscarPorId(id));
        return "usuario";
    }

    // =========================
    // SALVAR
    // =========================
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario) {
        usuarioService.salvar(usuario);
        return "redirect:/admin/usuarios";
    }

    // =========================
    // DESATIVAR (SOFT DELETE)  ✅ substitui o "excluir"
    // =========================
    @GetMapping("/desativar/{id}")
    public String desativar(@PathVariable Long id) {
        usuarioService.desativar(id);
        return "redirect:/admin/usuarios";
    }

    // =========================
    // RESTAURAR (volta a ser ativo)
    // =========================
    @GetMapping("/restaurar/{id}")
    public String restaurar(@PathVariable Long id) {
        usuarioService.restaurar(id);
        return "redirect:/admin/usuarios";
    }
}
