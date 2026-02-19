package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.repository.UsuarioRepository;
import com.sosbits.helpdesk.service.AvaliacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/avaliacoes")
@RequiredArgsConstructor
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("avaliacoes", avaliacaoService.listarTodas());
        return "avaliacoes";
    }


    @PostMapping("/chamado/{idChamado}")
    public String avaliarChamado(@PathVariable Long idChamado,
                                 @RequestParam Integer nota,
                                 @RequestParam(required = false) String comentario,
                                 Principal principal,
                                 RedirectAttributes ra) {

        try {
            Usuario usuarioLogado = getUsuarioLogado(principal);

            avaliacaoService.avaliarChamado(
                    idChamado,
                    usuarioLogado,
                    nota,
                    comentario
            );

            ra.addFlashAttribute("sucesso", "Avaliação registrada com sucesso!");

        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/chamados/meus";
    }


    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {

        try {
            avaliacaoService.excluir(id);
            ra.addFlashAttribute("sucesso", "Avaliação excluída!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/avaliacoes";
    }


    private Usuario getUsuarioLogado(Principal principal) {

        if (principal == null) {
            throw new IllegalStateException("Usuário não autenticado.");
        }

        String email = principal.getName(); // Spring Security retorna username/email

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException("Usuário logado não encontrado."));
    }
}
