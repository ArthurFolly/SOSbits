package com.sosbits.helpdesk.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "categoria")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer id; // ✅ AGORA 'id' é o id_categoria do banco

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "descricao", columnDefinition = "text")
    private String descricao;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "deletado")
    private Boolean deletado;

    // Se esse campo 'id' bigint do banco for "id do usuario/tenant", renomeie ele:
    // @Column(name = "id")
    // private Long idUsuario;
}