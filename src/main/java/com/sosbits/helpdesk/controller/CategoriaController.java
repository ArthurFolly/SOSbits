package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Categoria;
import com.sosbits.helpdesk.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaService.listarAtivas());
        model.addAttribute("categoria", new Categoria()); // para formulário/modal
        return "categorias/categorias";
    }

    @PostMapping("/criar")
    public String criar(@ModelAttribute Categoria categoria, RedirectAttributes ra) {
        try {
            categoriaService.criar(categoria);
            ra.addFlashAttribute("sucesso", "Categoria criada com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/categorias";
    }

    @PostMapping("/{id}/atualizar")
    public String atualizar(@PathVariable Long id, @ModelAttribute Categoria categoria, RedirectAttributes ra) {
        try {
            categoriaService.atualizar(id, categoria);
            ra.addFlashAttribute("sucesso", "Categoria atualizada com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/categorias";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            categoriaService.excluir(id);
            ra.addFlashAttribute("sucesso", "Categoria excluída com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/categorias";
    }
}