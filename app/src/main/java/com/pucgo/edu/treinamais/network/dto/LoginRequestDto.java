package com.pucgo.edu.treinamais.network.dto;

import com.google.gson.annotations.SerializedName;

public class LoginRequestDto {

    @SerializedName("email")
    private String email;

    @SerializedName("senha")
    private String senha;

    public LoginRequestDto() {
    }

    public LoginRequestDto(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
