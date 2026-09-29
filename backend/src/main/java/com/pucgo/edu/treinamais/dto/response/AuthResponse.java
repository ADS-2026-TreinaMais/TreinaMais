package com.pucgo.edu.treinamais.dto.response;

public class AuthResponse {

    private String token;
    private String tipoToken = "Bearer";
    private Long id;
    private String nome;
    private String email;
    private String tipo;
    private String status;
    private Long expiraEm;

    public AuthResponse() {
    }

    public AuthResponse(String token, Long id, String nome, String email, String tipo, String status) {
        this.token = token;
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipo = tipo;
        this.status = status;
    }

    public AuthResponse(String token, Long id, String nome, String email, String tipo, String status, Long expiraEm) {
        this.token = token;
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipo = tipo;
        this.status = status;
        this.expiraEm = expiraEm;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipoToken() {
        return tipoToken;
    }

    public void setTipoToken(String tipoToken) {
        this.tipoToken = tipoToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getExpiraEm() {
        return expiraEm;
    }

    public void setExpiraEm(Long expiraEm) {
        this.expiraEm = expiraEm;
    }
}
