package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Chamado;
import com.sosbits.helpdesk.repository.ChamadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails user) {
        // Se o usuário não estiver logado (null), evita erro de NullPointerException
        if (user != null) {
            model.addAttribute("usuarioNome", user.getUsername());
        } else {
            model.addAttribute("usuarioNome", "Convidado");
        }

        model.addAttribute("chamadosRecentes", repository.findFirst5ByOrderByDataCriacaoDesc());
        model.addAttribute("totalAbertos", repository.countByStatus("Aberto"));
        model.addAttribute("totalAndamento", repository.countByStatus("Em Andamento"));
        model.addAttribute("totalResolvidos", repository.countByStatus("Resolvido"));
        model.addAttribute("totalUrgentes", repository.countByStatus("Urgente"));

        return "dashboard"; // Abre o dashboard.html
    }

    @GetMapping("/chamados")
    public String listarTodos(Model model) {
        model.addAttribute("chamados", repository.findAll());
        return "chamados";
    }

    @PostMapping("/chamados/salvar")
    public String salvar(Chamado chamado) {
        chamado.setStatus("Aberto");
        chamado.setDataCriacao(LocalDateTime.now());
        repository.save(chamado);
        return "redirect:/dashboard"; // Mudei para voltar ao dashboard e ver o novo chamado
    }
}