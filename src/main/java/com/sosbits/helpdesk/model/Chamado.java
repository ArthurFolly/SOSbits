package com.sosbits.helpdesk.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "chamado")
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String tipo;        // EX: "Problema de Hardware"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;

    @Column(nullable = false)
    private String status;      // "Aberto", "Em Andamento", "Resolvido"

    @Column(nullable = false)
    private String prioridade;  // "Baixa", "Média", "Alta"

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        if (dataCriacao == null) dataCriacao = LocalDateTime.now();
        if (status == null) status = "Aberto";
        if (prioridade == null) prioridade = "Baixa";
    }
}
