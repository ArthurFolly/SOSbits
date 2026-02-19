package com.sosbits.helpdesk.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "avaliacao")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avaliacao")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_chamado", nullable = false)
    private Chamado chamado;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private Integer nota;

    @Column(columnDefinition = "text")
    private String comentario;

    @Column(name = "data_avaliacao", nullable = false)
    private LocalDateTime dataAvaliacao;

    /* =========================
       SOFT DELETE
       ========================= */
    @Column(name = "ativa", nullable = false)
    private Boolean ativa = true;

    @Column(name = "data_desativacao")
    private LocalDateTime dataDesativacao;

    @ManyToOne
    @JoinColumn(name = "desativada_por")
    private Usuario desativadaPor;

    @PrePersist
    public void prePersist() {
        if (dataAvaliacao == null) {
            dataAvaliacao = LocalDateTime.now();
        }
        if (ativa == null) {
            ativa = true;
        }
    }
}
