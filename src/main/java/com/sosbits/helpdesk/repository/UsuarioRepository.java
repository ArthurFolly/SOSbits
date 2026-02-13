package com.sosbits.helpdesk.repository;

import com.sosbits.helpdesk.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    List<Usuario> findByAtivoTrue();

    List<Usuario> findByAtivoFalse();
}
