package com.sosbits.helpdesk.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String assunto;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private String status; // "Aberto", "Em Andamento", "Resolvido"

    private String prioridade; // "Baixa", "Média", "Alta"

    private LocalDateTime dataCriacao = LocalDateTime.now();
}