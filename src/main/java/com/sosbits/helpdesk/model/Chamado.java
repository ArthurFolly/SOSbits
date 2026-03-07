package com.sosbits.helpdesk.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "chamado")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String tipo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String prioridade;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_solicitante", nullable = false)
    @JsonIgnore
    private Usuario solicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_atendente")
    @JsonIgnore
    private Usuario atendente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_setor")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Setor setor;

    @Column(name = "data_atendimento")
    private LocalDateTime dataAtendimento;

    @Column(nullable = false)
    private boolean deletado = false;

    @PrePersist
    public void prePersist() {
        if (dataCriacao == null) dataCriacao = LocalDateTime.now();
        if (status == null) status = "ABERTO";
        if (prioridade == null) prioridade = "BAIXA";
    }
}