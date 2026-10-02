package com.pucgo.edu.treinamais.dto.response;

import java.time.LocalDateTime;

public class ProfessorResponse {

    private Long id;
    private Long usuarioId;
    private String nome;
    private String email;
    private String cref;
    private String telefone;
    private LocalDateTime criadoEm;

    public ProfessorResponse() {
    }

    public ProfessorResponse(Long id, Long usuarioId, String nome, String email, String cref, String telefone, LocalDateTime criadoEm) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.email = email;
        this.cref = cref;
        this.telefone = telefone;
        this.criadoEm = criadoEm;
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

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
