package com.sosbits.helpdesk.controller;

import com.sosbits.helpdesk.model.Chamado;
import com.sosbits.helpdesk.service.CategoriaService;
import com.sosbits.helpdesk.service.ChamadoService;
import com.sosbits.helpdesk.service.SetorService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/chamados")
public class ChamadoController {

    private final ChamadoService service;
    private final CategoriaService categoriaService;
    private final SetorService setorService;

    public ChamadoController(ChamadoService service,
                             CategoriaService categoriaService,
                             SetorService setorService) {
        this.service = service;
        this.categoriaService = categoriaService;
        this.setorService = setorService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails user) {

        String usuarioNome = user != null ? user.getUsername() : "Convidado";
        String usuarioPerfil = extrairPerfil(user);

        List<Chamado> chamadosVisiveis = buscarChamadosVisiveis(user);

        long totalAbertos = chamadosVisiveis.stream()
                .filter(c -> normalizarStatus(c.getStatus()).equals("ABERTO"))
                .count();

        long totalAndamento = chamadosVisiveis.stream()
                .filter(c -> normalizarStatus(c.getStatus()).equals("EM_ANDAMENTO"))
                .count();

        long totalResolvidos = chamadosVisiveis.stream()
                .filter(c -> normalizarStatus(c.getStatus()).equals("FECHADO"))
                .count();

        long totalPendentes = chamadosVisiveis.stream()
                .filter(c -> normalizarStatus(c.getStatus()).equals("PENDENTE"))
                .count();

        List<Chamado> chamadosRecentes = chamadosVisiveis.stream()
                .limit(5)
                .toList();

        model.addAttribute("usuarioNome", usuarioNome);
        model.addAttribute("usuarioPerfil", usuarioPerfil);

        model.addAttribute("chamadosRecentes", chamadosRecentes);
        model.addAttribute("totalAbertos", totalAbertos);
        model.addAttribute("totalAndamento", totalAndamento);
        model.addAttribute("totalResolvidos", totalResolvidos);
        model.addAttribute("totalPendentes", totalPendentes);

        return "dashboard";
    }

    @GetMapping
    public String listar(@RequestParam(value = "deleted", required = false) Integer deleted,
                         @RequestParam(value = "novo", required = false) Integer novo,
                         Model model,
                         @AuthenticationPrincipal UserDetails user) {

        boolean modoExcluidos = (deleted != null && deleted == 1);
        boolean abrirModal = (novo != null && novo == 1);

        List<Chamado> chamados;

        if (modoExcluidos) {
            chamados = isAdminOuSuporte(user) ? service.listarDeletados() : List.of();
        } else {
            chamados = buscarChamadosVisiveis(user);
        }

        model.addAttribute("usuarioNome", user != null ? user.getUsername() : "Convidado");
        model.addAttribute("usuarioPerfil", extrairPerfil(user));

        model.addAttribute("chamados", chamados);
        model.addAttribute("modoExcluidos", modoExcluidos);
        model.addAttribute("chamado", new Chamado());
        model.addAttribute("abrirModal", abrirModal);

        model.addAttribute("categorias", categoriaService.listarAtivas());
        model.addAttribute("setores", setorService.listarAtivos());

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
    public List<Chamado> listarApi(@RequestParam(value = "deleted", required = false) Integer deleted,
                                   @AuthenticationPrincipal UserDetails user) {

        boolean modoExcluidos = (deleted != null && deleted == 1);

        if (modoExcluidos) {
            return isAdminOuSuporte(user) ? service.listarDeletados() : List.of();
        }

        return buscarChamadosVisiveis(user);
    }

    @PostMapping(consumes = "application/json")
    @ResponseBody
    public Chamado criar(@RequestBody Chamado chamado,
                         @AuthenticationPrincipal UserDetails user) {
        return service.criar(chamado, user);
    }

    @GetMapping("/{id:\\d+}")
    @ResponseBody
    public Chamado buscar(@PathVariable Long id,
                          @AuthenticationPrincipal UserDetails user) {

        Chamado chamado = service.buscarPorId(id);

        if (!podeVisualizarChamado(user, chamado)) {
            throw new RuntimeException("Você não tem permissão para visualizar este chamado.");
        }

        return chamado;
    }

    @PutMapping(value = "/{id:\\d+}", consumes = "application/json")
    @ResponseBody
    public Chamado atualizar(@PathVariable Long id,
                             @RequestBody Chamado chamado,
                             @AuthenticationPrincipal UserDetails user) {

        Chamado existente = service.buscarPorId(id);

        if (!podeVisualizarChamado(user, existente)) {
            throw new RuntimeException("Você não tem permissão para editar este chamado.");
        }

        return service.atualizar(id, chamado, user);
    }

    @DeleteMapping("/{id:\\d+}")
    @ResponseBody
    public void excluirAjax(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetails user) {

        Chamado chamado = service.buscarPorId(id);

        if (!podeVisualizarChamado(user, chamado)) {
            throw new RuntimeException("Você não tem permissão para excluir este chamado.");
        }

        service.excluir(id);
    }

    @PatchMapping("/{id:\\d+}/restaurar")
    @ResponseBody
    public void restaurarAjax(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails user) {

        if (!isAdminOuSuporte(user)) {
            throw new RuntimeException("Você não tem permissão para restaurar chamados.");
        }

        service.restaurar(id);
    }

    private List<Chamado> buscarChamadosVisiveis(UserDetails user) {
        if (isAdminOuSuporte(user)) {
            return service.listarTodos();
        }
        return service.listarDoUsuarioLogado();
    }

    private boolean isAdminOuSuporte(UserDetails user) {
        if (user == null) {
            return false;
        }

        return user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth ->
                        auth.equals("ROLE_ADMIN") ||
                                auth.equals("ROLE_SUPORTE"));
    }

    private String extrairPerfil(UserDetails user) {
        if (user == null || user.getAuthorities() == null || user.getAuthorities().isEmpty()) {
            return "CONVIDADO";
        }

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            if ("ROLE_ADMIN".equals(role)) {
                return "ADMIN";
            }
            if ("ROLE_SUPORTE".equals(role)) {
                return "SUPORTE";
            }
            if ("ROLE_USUARIO".equals(role)) {
                return "USUARIO";
            }
        }

        return authorities.iterator().next().getAuthority().replace("ROLE_", "");
    }

    private boolean podeVisualizarChamado(UserDetails user, Chamado chamado) {
        if (isAdminOuSuporte(user)) {
            return true;
        }

        if (user == null || chamado == null || chamado.getSolicitante() == null) {
            return false;
        }

        String emailLogado = user.getUsername();
        String emailSolicitante = chamado.getSolicitante().getEmail();

        return emailLogado != null && emailLogado.equalsIgnoreCase(emailSolicitante);
    }

    private String normalizarStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "ABERTO";
        }

        String valor = status.trim();

        if (valor.equalsIgnoreCase("ABERTO") || valor.equalsIgnoreCase("Aberto")) {
            return "ABERTO";
        }

        if (valor.equalsIgnoreCase("EM_ANDAMENTO")
                || valor.equalsIgnoreCase("Em Andamento")
                || valor.equalsIgnoreCase("EM ANDAMENTO")) {
            return "EM_ANDAMENTO";
        }

        if (valor.equalsIgnoreCase("PENDENTE") || valor.equalsIgnoreCase("Pendente")) {
            return "PENDENTE";
        }

        if (valor.equalsIgnoreCase("FECHADO")
                || valor.equalsIgnoreCase("Fechado")
                || valor.equalsIgnoreCase("Resolvido")
                || valor.equalsIgnoreCase("RESOLVIDO")) {
            return "FECHADO";
        }

        return "ABERTO";
    }
}