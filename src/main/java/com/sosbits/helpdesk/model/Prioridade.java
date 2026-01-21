package com.sosbits.helpdesk.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "prioridade")
@Getter @Setter
public class Prioridade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_prioridade;

    @Column(nullable = false, length = 20)
    private String nome; // Aqui ficarão: Baixa, Normal, Alta, Urgente
}