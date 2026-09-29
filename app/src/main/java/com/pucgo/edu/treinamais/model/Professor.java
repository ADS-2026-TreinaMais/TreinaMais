package com.pucgo.edu.treinamais.model;

import java.util.Date;

public class Professor {

    private Long id;
    private Long usuarioId;
    private String cref;
    private String telefone;
    private Date criadoEm;

    private String nome;
    private String email;

    public Professor() {
    }

    public Professor(Long usuarioId, String cref, String telefone) {
        this.usuarioId = usuarioId;
        this.cref = cref;
        this.telefone = telefone;
    }

    public Professor(Long id, Long usuarioId, String cref, String telefone, String nome, String email) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.cref = cref;
        this.telefone = telefone;
        this.nome = nome;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getCref() {
        return cref;
    }

    public void setCref(String cref) {
        this.cref = cref;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Date getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Date criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Professor{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", cref='" + cref + '\'' +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}
