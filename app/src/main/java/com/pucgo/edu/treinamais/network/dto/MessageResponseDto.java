package com.pucgo.edu.treinamais.network.dto;

import com.google.gson.annotations.SerializedName;

public class MessageResponseDto {

    @SerializedName("mensagem")
    private String mensagem;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("status")
    private Integer status;

    public MessageResponseDto() {
    }

    public MessageResponseDto(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
