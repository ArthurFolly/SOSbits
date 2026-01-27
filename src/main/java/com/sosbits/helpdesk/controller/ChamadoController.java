package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Chamado;
import com.sosbits.helpdesk.service.ChamadoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/chamados")
public class ChamadoController {

    private final ChamadoService service;

    public ChamadoController(ChamadoService service) {
        this.service = service;
    }

    // ---------------- DASHBOARD ----------------
    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("usuarioNome", user != null ? user.getUsername() : "Convidado");

        model.addAttribute("chamadosRecentes", service.listarRecentes());
        model.addAttribute("totalAbertos", service.contarPorStatus("Aberto"));
        model.addAttribute("totalAndamento", service.contarPorStatus("Em Andamento"));
        model.addAttribute("totalResolvidos", service.contarPorStatus("Resolvido"));
        model.addAttribute("totalUrgentes", service.contarPorPrioridade("Alta"));

        return "dashboard";
    }

    // ---------------- VIEW (THYMELEAF) ----------------
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("chamados", service.listarTodos());
        return "chamados";
    }

    // ✅ SALVAR VIA FORM (THYMELEAF)  -> POST /chamados/salvar
    @PostMapping("/salvar")
    public String salvarForm(@ModelAttribute Chamado chamado) {
        service.salvar(chamado);
        return "redirect:/chamados";
    }

    // ✅ EXCLUIR VIA LINK (THYMELEAF) -> GET /chamados/excluir/{id}
    @GetMapping("/excluir/{id}")
    public String excluirForm(@PathVariable Long id) {
        service.excluir(id);
        return "redirect:/chamados";
    }

    // ---------------- API (AJAX) ----------------
    @GetMapping("/api")
    @ResponseBody
    public List<Chamado> listarApi() {
        return service.listarTodos();
    }
    // Se você quiser manter o CREATE via AJAX em /chamados (POST JSON)
    @PostMapping(consumes = "application/json")
    @ResponseBody
    public Chamado criar(@RequestBody Chamado chamado,
                         @AuthenticationPrincipal UserDetails user) {

        // Correção simples: não usa o "user" aqui (evita user null estourar no service.criar)
        // Salva usando o mesmo fluxo do form
        service.salvar(chamado);
        return chamado;
    }



    @GetMapping("/{id}")
    @ResponseBody
    public Chamado buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    @ResponseBody
    public Chamado atualizar(@PathVariable Long id,
                             @RequestBody Chamado chamado,
                             @AuthenticationPrincipal UserDetails user) {
        return service.atualizar(id, chamado, user);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
