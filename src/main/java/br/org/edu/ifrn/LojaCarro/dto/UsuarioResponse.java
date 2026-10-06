package br.org.edu.ifrn.LojaCarro.dto;

import br.org.edu.ifrn.LojaCarro.model.Papel;
import br.org.edu.ifrn.LojaCarro.model.Usuario;

public class UsuarioResponse {

    private Long id;
    private String nome;
    private String email;
    private Papel papel;
    private boolean ativo;

    public UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.papel = usuario.getPapel();
        this.ativo = usuario.isAtivo();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public Papel getPapel() {
        return papel;
    }

    public boolean isAtivo() {
        return ativo;
    }
}