package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Perfil;
import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.repository.PerfilRepository;
import com.sosbits.helpdesk.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(
            UsuarioRepository usuarioRepository,
            PerfilRepository perfilRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/cadastro")
    public String abrirCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro";
    }


    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/chamados/dashboard";
    }


    @GetMapping("/usuario")
    public String paginaUsuarios() {
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/salvar")
    public String cadastrar(@ModelAttribute Usuario usuario) {

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return "redirect:/cadastro?error=email";
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        Perfil perfilUsuario = perfilRepository.findByNome("USER")
                .orElseThrow(() -> new RuntimeException("Perfil USER não encontrado no banco"));

        usuario.getPerfis().add(perfilUsuario);
        usuario.setAtivo(true);

        usuarioRepository.save(usuario);

        return "redirect:/?success";
    }
}
