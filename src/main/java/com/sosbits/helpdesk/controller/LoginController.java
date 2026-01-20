package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.util.Optional;

@Controller
public class LoginController {

    private final UsuarioRepository usuarioRepository;

    public LoginController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/")
    public String redirectLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String telaLogin() {
        return "index"; // Abre o index.html (seu formulário de login)
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String senha,
                        Model model,
                        HttpSession session) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty() || !usuarioOpt.get().getSenha().equals(senha)) {
            model.addAttribute("erro", "E-mail ou senha incorretos!");
            return "index";
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getAtivo() == null || !usuario.getAtivo()) {
            model.addAttribute("erro", "Usuário inativo!");
            return "index";
        }

        // Salva na sessão e REDIRECIONA para o outro Controller
        session.setAttribute("usuarioNome", usuario.getNome());
        return "redirect:/dashboard";
    }


}