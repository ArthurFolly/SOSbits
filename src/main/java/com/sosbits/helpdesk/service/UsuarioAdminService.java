// =========================================================
// 1) UsuarioAdminService.java  (ARQUIVO COMPLETO)
// Caminho: src/main/java/com/sosbits/helpdesk/service/UsuarioAdminService.java
// =========================================================
package com.sosbits.helpdesk.service;

import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioAdminService {

    private final UsuarioRepository usuarioRepository;

    public List<Usuario> listarAtivos() {
        return usuarioRepository.findByAtivoTrueOrderByIdAsc();
    }

    public List<Usuario> listarInativos() {
        return usuarioRepository.findByAtivoFalseOrderByIdAsc();
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAllByOrderByIdAsc();
    }

    public List<Usuario> listarFiltrando(Boolean ativo, Long perfilId) {


        if (ativo == null && perfilId == null) {
            return listarTodos();
        }

        if (ativo != null && perfilId == null) {
            return ativo ? listarAtivos() : listarInativos();
        }


        if (ativo == null) {
            return usuarioRepository.findAllByPerfilIdOrderByIdAsc(perfilId);
        }

        // 4) ativo + perfil
        return usuarioRepository.findAllByAtivoAndPerfilIdOrderByIdAsc(ativo, perfilId);
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
}