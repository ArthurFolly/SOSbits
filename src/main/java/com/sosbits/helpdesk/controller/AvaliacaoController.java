package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.service.AvaliacaoService;
import com.sosbits.helpdesk.service.ChamadoService;
import com.sosbits.helpdesk.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;
    private final ChamadoService chamadoService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String pagina(Model model) {
        model.addAttribute("usuarioNome", usuarioService.getNomeUsuarioLogado());
        model.addAttribute("avaliacoes", avaliacaoService.listarAtivas());
        model.addAttribute("avaliacoesExcluidas", avaliacaoService.listarExcluidas());

        // IMPORTANTÍSSIMO: para popular o combo do modal (só fechados e não avaliados)
        model.addAttribute("chamadosParaAvaliar", chamadoService.listarChamadosFechadosNaoAvaliados());

        return "avaliacoes";
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

    @PostMapping("/restaurar/{id}")
    public String restaurar(@PathVariable Long id) {
        avaliacaoService.restaurar(id);
        return "redirect:/avaliacoes";
    }
}