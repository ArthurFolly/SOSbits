package com.sosbits.helpdesk.service;

import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /* =========================
       LISTAR TODOS
       ========================= */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /* =========================
       BUSCAR POR ID
       ========================= */
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    /* =========================
       SALVAR / ATUALIZAR
       ========================= */
    public void salvar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    /* =========================
       EXCLUIR
       ========================= */
    public void excluir(Long id) {
        usuarioRepository.deleteById(id);
    }
}
