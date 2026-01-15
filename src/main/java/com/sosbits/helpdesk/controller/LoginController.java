package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    private final UsuarioRepository usuarioRepository;

    public LoginController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // GET - tela de login
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String erro,
            Model model
    ) {
        if (erro != null) {
            model.addAttribute("erro", "Email ou senha inválidos");
        }
        return "index";
    }

    // POST - autenticação
    @PostMapping("/auth/login")
    public String autenticar(
            @RequestParam String email,
            @RequestParam String password
    ) {

        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null) {
            return "redirect:/login?erro";
        }

        if (!usuario.getSenha().equals(password)) {
            return "redirect:/login?erro";
        }

        if (!usuario.isAtivo()) {
            return "redirect:/login?erro";
        }

        return "redirect:/dashboard";
    }
}
