package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/cadastro")
    public String abrirCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro"; // Abre o cadastro.html
    }

    @PostMapping("/salvar")
    public String cadastrar(@ModelAttribute Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return "redirect:/cadastro?error=email";
        }
        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            return "redirect:/cadastro?error=cpf";
        }
        usuarioRepository.save(usuario);
        return "redirect:/?success";
    }
}