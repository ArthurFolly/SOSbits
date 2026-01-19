package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Abre a tela de cadastro
    @GetMapping("/cadastro")
    public String abrirCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro";
    }

    // Salva o usuário no banco
    @PostMapping("/salvar")
    public String cadastrar(@ModelAttribute Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return "redirect:/cadastro?error=email";
        }
        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            return "redirect:/cadastro?error=cpf";
        }

        usuarioRepository.save(usuario);
        return "redirect:/login"; // Redireciona para o outro Controller
    }
}