package com.pucgo.edu.treinamais.dto.response;

public class MessageResponse {

    private String mensagem;

    public MessageResponse() {
    }

    public MessageResponse(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
