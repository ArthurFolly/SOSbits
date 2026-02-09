package com.sosbits.helpdesk.controller;


import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioAdminController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("usuario", new Usuario()); // para o form
        return "/usuario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("usuario", usuarioService.buscarPorId(id));
        return "/usuario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario) {
        usuarioService.salvar(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
        return "redirect:/usuarios";
    }
}

