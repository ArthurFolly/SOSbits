package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Chamado;
import com.sosbits.helpdesk.repository.ChamadoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.time.LocalDateTime;

@Controller
public class ChamadoController {

    @Autowired
    private ChamadoRepository repository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        String nome = (String) session.getAttribute("usuarioNome");
        if (nome == null) return "redirect:/login";

        model.addAttribute("usuarioNome", nome);
        model.addAttribute("chamadosRecentes", repository.findFirst5ByOrderByDataCriacaoDesc());
        model.addAttribute("totalAbertos", repository.countByStatus("Aberto"));
        model.addAttribute("totalAndamento", repository.countByStatus("Em Andamento"));
        model.addAttribute("totalResolvidos", repository.countByStatus("Resolvido"));
        model.addAttribute("totalUrgentes", repository.countByStatus("Urgente"));
        return "dashboard";
    }

    @GetMapping("/chamados")
    public String listarTodos(Model model, HttpSession session) {
        if (session.getAttribute("usuarioNome") == null) return "redirect:/login";
        model.addAttribute("chamados", repository.findAll());
        return "chamados";
    }

    // MÉTODO PARA SALVAR O CHAMADO VINDO DO MODAL
    @PostMapping("/chamados/salvar")
    public String salvar(Chamado chamado) {
        chamado.setStatus("Aberto");
        chamado.setDataCriacao(LocalDateTime.now());
        repository.save(chamado);
        return "redirect:/chamados"; // Após salvar, volta para a lista de chamados
    }
}