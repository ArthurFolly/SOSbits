package com.sosbits.helpdesk.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "perfil")
@Getter
@Setter
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_perfil")
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String nome; // ADMIN, SUPORTE, USUARIO
}
