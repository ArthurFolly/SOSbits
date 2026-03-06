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

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("usuarioNome", user != null ? user.getUsername() : "Convidado");

        model.addAttribute("chamadosRecentes", service.listarRecentes());
        model.addAttribute("totalAbertos", service.contarPorStatus("Aberto"));
        model.addAttribute("totalAndamento", service.contarPorStatus("Em Andamento"));
        model.addAttribute("totalResolvidos", service.contarPorStatus("Resolvido"));
        model.addAttribute("totalPendentes", service.contarPorStatus("Pendente"));
        model.addAttribute("totalUrgentes", service.contarPorPrioridade("Alta"));

        return "dashboard";
    }

    @GetMapping
    public String listar(@RequestParam(value = "deleted", required = false) Integer deleted,
                         @RequestParam(value = "novo", required = false) Integer novo,
                         Model model) {

        boolean modoExcluidos = (deleted != null && deleted == 1);
        boolean abrirModal = (novo != null && novo == 1);

        model.addAttribute("chamados", modoExcluidos ? service.listarDeletados() : service.listarTodos());
        model.addAttribute("modoExcluidos", modoExcluidos);
        model.addAttribute("chamado", new Chamado());
        model.addAttribute("abrirModal", abrirModal);

        return "chamados";
    }

    @GetMapping("/novo")
    public String novo() {
        return "redirect:/chamados?novo=1";
    }


    @PostMapping("/salvar")
    public String salvarForm(@ModelAttribute Chamado chamado) {
        service.salvar(chamado);
        return "redirect:/chamados";
    }

    @GetMapping("/excluir/{id}")
    public String excluirForm(@PathVariable Long id) {
        service.excluir(id);
        return "redirect:/chamados";
    }

    @GetMapping("/restaurar/{id}")
    public String restaurarForm(@PathVariable Long id) {
        service.restaurar(id);
        return "redirect:/chamados?deleted=1";
    }

    @GetMapping("/api")
    @ResponseBody
    public List<Chamado> listarApi(@RequestParam(value = "deleted", required = false) Integer deleted) {
        boolean modoExcluidos = (deleted != null && deleted == 1);
        return modoExcluidos ? service.listarDeletados() : service.listarTodos();
    }

    @PostMapping(consumes = "application/json")
    @ResponseBody
    public Chamado criar(@RequestBody Chamado chamado,
                         @AuthenticationPrincipal UserDetails user) {
        return service.criar(chamado, user);
    }

    @GetMapping("/{id:\\d+}")
    @ResponseBody
    public Chamado buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping(value = "/{id:\\d+}", consumes = "application/json")
    @ResponseBody
    public Chamado atualizar(@PathVariable Long id,
                             @RequestBody Chamado chamado,
                             @AuthenticationPrincipal UserDetails user) {
        return service.atualizar(id, chamado, user);
    }

    @DeleteMapping("/{id:\\d+}")
    @ResponseBody
    public void excluirAjax(@PathVariable Long id) {
        service.excluir(id);
    }

    @PatchMapping("/{id:\\d+}/restaurar")
    @ResponseBody
    public void restaurarAjax(@PathVariable Long id) {
        service.restaurar(id);
    }
}