package com.sosbits.helpdesk.service;

import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public Usuario getUsuarioLogado() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            throw new RuntimeException("Usuário não autenticado");
        }

        String email = auth.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado no banco: " + email));
    }

    public String getNomeUsuarioLogado() {
        Usuario u = getUsuarioLogado();

        if (u.getNome() != null && !u.getNome().trim().isEmpty()) {
            return u.getNome().trim();
        }

        return u.getEmail();
    }

    public Long getIdUsuarioLogado() {
        return getUsuarioLogado().getId();
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public List<Usuario> listarAtivos() {
        return usuarioRepository.findByAtivoTrueOrderByIdAsc();
    }

    public List<Usuario> listarExcluidos() {
        return usuarioRepository.findByAtivoFalseOrderByIdAsc();
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAllByOrderByIdAsc();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));
    }

    public void salvar(Usuario usuario) {
        if (usuario.getAtivo() == null) {
            usuario.setAtivo(true);
        }
        usuarioRepository.save(usuario);
    }

    public void desativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    public void restaurar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }

    public void excluirFisico(Long id) {
        usuarioRepository.deleteById(id);
    }
}