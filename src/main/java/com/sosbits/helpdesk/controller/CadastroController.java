package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CadastroController {

    @GetMapping("/cadastro")
    public String exibirTelaCadastro(Model model) {
        // Isso envia um objeto vazio para o HTML não quebrar (Erro 500)
        model.addAttribute("usuario", new Usuario());
        return "cadastro";
    }
}