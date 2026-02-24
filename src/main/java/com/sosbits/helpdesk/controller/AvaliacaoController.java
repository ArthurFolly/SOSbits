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
    public String listar(Model model) {

        Usuario logado = usuarioService.getUsuarioLogado();

        model.addAttribute("avaliacoes", avaliacaoService.listarTodas());
        model.addAttribute("avaliacoesExcluidas", avaliacaoService.listarExcluidas());

        model.addAttribute("chamadosParaAvaliar",
                avaliacaoService.listarChamadosFechadosNaoAvaliados(logado.getId()));

        return "avaliacao"; // templates/avaliacao.html
    }

    // ✅ PADRÃO A: form fixo em /avaliacoes/salvar
    @PostMapping("/salvar")
    public String salvar(@RequestParam Long chamadoId,
                         @RequestParam Integer nota,
                         @RequestParam(required = false) String comentario,
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