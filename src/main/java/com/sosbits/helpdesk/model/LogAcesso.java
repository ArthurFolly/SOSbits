package com.sosbits.helpdesk.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs_acesso")
@Getter
@Setter
public class LogAcesso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log")
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "nome_usuario", nullable = false, length = 150)
    private String nomeUsuario;

    @Column(name = "email_usuario", nullable = false, length = 200)
    private String emailUsuario;

    @Column(name = "perfil_usuario", nullable = false, length = 50)
    private String perfilUsuario;

    @Column(name = "data_hora_login", nullable = false)
    private LocalDateTime dataHoraLogin;

    @Column(name = "data_hora_logout")
    private LocalDateTime dataHoraLogout;

    @Column(name = "observacao", length = 255)
    private String observacao;

    /*
     --------------------------------------------------------
     CAMPO CALCULADO (NÃO EXISTE NO BANCO)
     Calcula tempo de acesso em minutos
     --------------------------------------------------------
     */

    @Transient
    public Long getTempoAcessoMinutos() {

        if (dataHoraLogin == null || dataHoraLogout == null) {
            return null;
        }

        return Duration.between(dataHoraLogin, dataHoraLogout).toMinutes();
    }
}