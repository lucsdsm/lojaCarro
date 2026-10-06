package br.org.edu.ifrn.LojaCarro.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "log_sistema")
public class LogSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(nullable = false, length = 500)
    private String acao;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    protected LogSistema() {
    }

    public LogSistema(Long usuarioId, String acao) {
        this.usuarioId = usuarioId;
        this.acao = acao;
        this.criadoEm = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getAcao() {
        return acao;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}