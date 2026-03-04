package com.sosbits.helpdesk.repository;

import com.sosbits.helpdesk.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);

    List<Usuario> findByAtivoTrueOrderByIdAsc();

    List<Usuario> findByAtivoFalseOrderByIdAsc();

    List<Usuario> findAllByOrderByIdAsc();

    @Query("""
        select distinct u
        from Usuario u
        join u.perfis p
        where p.id = :perfilId
        order by u.id asc
    """)
    List<Usuario> findAllByPerfilIdOrderByIdAsc(@Param("perfilId") Long perfilId);

    @Query("""
        select distinct u
        from Usuario u
        join u.perfis p
        where u.ativo = :ativo
        and p.id = :perfilId
        order by u.id asc
    """)
    List<Usuario> findAllByAtivoAndPerfilIdOrderByIdAsc(
            @Param("ativo") Boolean ativo,
            @Param("perfilId") Long perfilId
    );
}