package com.sosbits.helpdesk.service;

import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    public List<Usuario> listarAtivos() {
        return usuarioRepository.findByAtivoTrue();
    }


    public List<Usuario> listarExcluidos() {
        return usuarioRepository.findByAtivoFalse();
    }


    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }


    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
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
