package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.service.AvaliacaoService;
import com.sosbits.helpdesk.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/avaliacoes")
@RequiredArgsConstructor
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model, RedirectAttributes ra) {

        Usuario logado = usuarioService.getUsuarioLogado();
        if (logado == null || logado.getId() == null) {
            ra.addFlashAttribute("erro", "Usuário logado não encontrado. Faça login novamente.");
            return "redirect:/login";
        }

        model.addAttribute("avaliacoes", avaliacaoService.listarTodas());
        model.addAttribute("avaliacoesExcluidas", avaliacaoService.listarExcluidas());

        model.addAttribute("chamadosParaAvaliar",
                avaliacaoService.listarChamadosFechadosNaoAvaliados(logado.getId()));

        return "avaliacao";
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam(name = "chamadoId") Long chamadoId,
                         @RequestParam(name = "nota") Integer nota,
                         @RequestParam(name = "comentario", required = false) String comentario,
                         RedirectAttributes ra) {

        try {
            avaliacaoService.salvarAvaliacao(chamadoId, nota, comentario);
            ra.addFlashAttribute("sucesso", "Avaliação registrada com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/avaliacoes";
    }

    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            avaliacaoService.desativar(id);
            ra.addFlashAttribute("sucesso", "Avaliação desativada!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/avaliacoes";
    }

    @PostMapping("/{id}/restaurar")
    public String restaurar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            avaliacaoService.restaurar(id);
            ra.addFlashAttribute("sucesso", "Avaliação restaurada!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/avaliacoes";
    }
}