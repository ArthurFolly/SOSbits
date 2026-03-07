package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.service.AvaliacaoService;
import com.sosbits.helpdesk.service.ChamadoService;
import com.sosbits.helpdesk.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
@RequiredArgsConstructor
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;
    private final ChamadoService chamadoService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String pagina(Model model, @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("usuarioNome", usuarioService.getNomeUsuarioLogado());
        model.addAttribute("usuarioPerfil", extrairPerfil(user));
        model.addAttribute("avaliacoes", avaliacaoService.listarAtivasDoUsuarioLogado());
        model.addAttribute("avaliacoesExcluidas", avaliacaoService.listarExcluidasDoUsuarioLogado());
        model.addAttribute("chamadosParaAvaliar", chamadoService.listarChamadosFechadosNaoAvaliados());

        return "avaliacao";
    }

    @PostMapping("/criar")
    public String criar(@RequestParam Long idChamado,
                        @RequestParam Integer nota,
                        @RequestParam(required = false) String comentario) {
        avaliacaoService.criar(idChamado, nota, comentario);
        return "redirect:/avaliacoes";
    }

    @PostMapping("/atualizar")
    public String atualizar(@RequestParam Long idAvaliacao,
                            @RequestParam Integer nota,
                            @RequestParam(required = false) String comentario) {
        avaliacaoService.atualizar(idAvaliacao, nota, comentario);
        return "redirect:/avaliacoes";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        avaliacaoService.excluir(id);
        return "redirect:/avaliacoes";
    }

    @PostMapping("/{id}/restaurar")
    public String restaurar(@PathVariable Long id) {
        avaliacaoService.restaurar(id);
        return "redirect:/avaliacoes";
    }

    @PostMapping("/restaurar/{id}")
    public String restaurarLegado(@PathVariable Long id) {
        avaliacaoService.restaurar(id);
        return "redirect:/avaliacoes";
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