package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Categoria;
import com.sosbits.helpdesk.service.CategoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(value = "deleted", required = false) Integer deleted,
                         Model model) {

        boolean modoExcluidos = (deleted != null && deleted == 1);

        model.addAttribute("categorias",
                modoExcluidos ? service.listarDeletadas() : service.listarAtivas());

        model.addAttribute("modoExcluidos", modoExcluidos);
        model.addAttribute("categoria", new Categoria());

        // templates/categoria.html
        return "categoria";
    }

    @PostMapping("/salvar")
    public String salvarForm(@ModelAttribute Categoria categoria) {
        service.salvar(categoria);
        return "redirect:/categorias";
    }

    @GetMapping("/excluir/{id}")
    public String excluirForm(@PathVariable Long id) {
        service.excluir(id);
        return "redirect:/categorias";
    }

    @GetMapping("/restaurar/{id}")
    public String restaurarForm(@PathVariable Long id) {
        service.restaurar(id);
        return "redirect:/categorias?deleted=1";
    }

    @GetMapping("/api")
    @ResponseBody
    public List<Categoria> listarApi(@RequestParam(value = "deleted", required = false) Integer deleted) {
        boolean modoExcluidos = (deleted != null && deleted == 1);
        return modoExcluidos ? service.listarDeletadas() : service.listarAtivas();
    }

    @GetMapping("/api/excluidas")
    @ResponseBody
    public List<Categoria> listarExcluidasApi() {
        return service.listarDeletadas();
    }


    @PostMapping(consumes = "application/json")
    @ResponseBody
    public Categoria criar(@RequestBody Categoria categoria) {
        return service.criar(categoria);
    }


    @GetMapping("/{id}")
    @ResponseBody
    public Categoria buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public Categoria buscarApi(@PathVariable Long id) {
        return service.buscarPorId(id);
    }
    @PutMapping(value = "/{id}", consumes = "application/json")
    @ResponseBody
    public Categoria atualizar(@PathVariable Long id,
                               @RequestBody Categoria categoria) {
        return service.atualizar(id, categoria);
    }
    @PutMapping(value = "/api/{id}", consumes = "application/json")
    @ResponseBody
    public Categoria atualizarApi(@PathVariable Long id,
                                  @RequestBody Categoria categoria) {
        return service.atualizar(id, categoria);
    }
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> excluirAjax(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/{id}/restaurar")
    @ResponseBody
    public ResponseEntity<Void> restaurarAjax(@PathVariable Long id) {
        service.restaurar(id);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/api/{id}/restaurar")
    @ResponseBody
    public ResponseEntity<Void> restaurarAjaxCompat(@PathVariable Long id) {
        service.restaurar(id);
        return ResponseEntity.ok().build();
    }
}