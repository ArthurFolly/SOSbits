package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Setor;
import com.sosbits.helpdesk.service.SetorService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/setores")
public class SetorController {

    private final SetorService service;

    public SetorController(SetorService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("setores", service.listarAtivos());
        model.addAttribute("setor", new Setor());
        return "setor";
    }

    @PostMapping("/salvar")
    public String salvarForm(@ModelAttribute Setor setor) {
        service.criar(setor);
        return "redirect:/setores";
    }

    @GetMapping("/excluir/{id}")
    public String excluirForm(@PathVariable Long id) {
        service.excluir(id);
        return "redirect:/setores";
    }

    @GetMapping("/excluidos")
    @ResponseBody
    public List<Setor> listarExcluidos() {
        return service.listarDeletados();
    }

    @PostMapping("/restaurar")
    @ResponseBody
    public ResponseEntity<String> restaurar(@RequestParam Long id) {
        service.restaurar(id);
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/editar")
    @ResponseBody
    public ResponseEntity<String> editar(@RequestParam Long id,
                                         @RequestParam String nome,
                                         @RequestParam(required = false) String descricao) {
        Setor setor = new Setor();
        setor.setNome(nome);
        setor.setDescricao(descricao);

        service.atualizar(id, setor);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/buscar/{id}")
    @ResponseBody
    public Setor buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }
}