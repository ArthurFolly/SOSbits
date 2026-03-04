package com.sosbits.helpdesk.repository;

import com.sosbits.helpdesk.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Login / Security
    Optional<Usuario> findByEmail(String email);

    // Validações
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);

    // =========================
    // LISTAGENS (ordenadas por ID) - recomendadas
    // =========================
    List<Usuario> findByAtivoTrueOrderByIdAsc();
    List<Usuario> findByAtivoFalseOrderByIdAsc();

    // (Opcional) se você quiser sempre listar tudo ordenado também:
    List<Usuario> findAllByOrderByIdAsc();
}