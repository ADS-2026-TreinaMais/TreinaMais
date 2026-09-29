package com.pucgo.edu.treinamais.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_sessions")
public class UserSessionEntity {

    @PrimaryKey
    @ColumnInfo(name = "user_id")
    private Long userId;

    @ColumnInfo(name = "nome")
    private String nome;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "tipo")
    private String tipo;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "token")
    private String token;

    @ColumnInfo(name = "expira_em")
    private Long expiraEm;

    @ColumnInfo(name = "salvo_em")
    private Long salvoEm;

    public UserSessionEntity() {
    }

    @androidx.room.Ignore
    public UserSessionEntity(Long userId, String nome, String email, String tipo, String status, String token, Long expiraEm) {
        this.userId = userId;
        this.nome = nome;
        this.email = email;
        this.tipo = tipo;
        this.status = status;
        this.token = token;
        this.expiraEm = expiraEm;
        this.salvoEm = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return expiraEm != null && System.currentTimeMillis() >= expiraEm;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpiraEm() {
        return expiraEm;
    }

    public void setExpiraEm(Long expiraEm) {
        this.expiraEm = expiraEm;
    }

    public Long getSalvoEm() {
        return salvoEm;
    }

    public void setSalvoEm(Long salvoEm) {
        this.salvoEm = salvoEm;
    }
}
