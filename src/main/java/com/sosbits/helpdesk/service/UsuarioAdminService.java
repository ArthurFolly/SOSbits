package com.sosbits.helpdesk.service;

import com.sosbits.helpdesk.model.Perfil;
import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.repository.PerfilRepository;
import com.sosbits.helpdesk.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioAdminService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

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

        return usuarioRepository.findAllByAtivoAndPerfilIdOrderByIdAsc(ativo, perfilId);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));
    }


    public void salvar(Usuario usuario, Long perfilId, String senha) {

        Perfil perfil = perfilRepository.findById(perfilId)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado: " + perfilId));


        if (usuario.getId() != null) {
            Usuario atual = buscarPorId(usuario.getId());

            atual.setNome(usuario.getNome());
            atual.setEmail(usuario.getEmail());
            atual.setCpf(usuario.getCpf());
            atual.setTelefone(usuario.getTelefone());


            if (senha != null && !senha.isBlank()) {
                atual.setSenha(passwordEncoder.encode(senha));
            }


            atual.getPerfis().clear();
            atual.getPerfis().add(perfil);


            if (usuario.getAtivo() != null) {
                atual.setAtivo(usuario.getAtivo());
            }

            usuarioRepository.save(atual);
            return;
        }


        if (senha == null || senha.isBlank()) {
            throw new RuntimeException("Senha é obrigatória para criar usuário");
        }

        usuario.setSenha(passwordEncoder.encode(senha));

        if (usuario.getAtivo() == null) {
            usuario.setAtivo(true);
        }

        usuario.getPerfis().clear();
        usuario.getPerfis().add(perfil);

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