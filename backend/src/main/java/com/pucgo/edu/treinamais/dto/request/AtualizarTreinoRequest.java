package com.pucgo.edu.treinamais.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class AtualizarTreinoRequest {

    @NotBlank(message = "O nome do treino é obrigatório")
    private String nome;

    private String descricao;

    private String objetivo;

    private String status;

    @NotEmpty(message = "O treino deve possuir pelo menos 1 exercício cadastrado")
    @Valid
    private List<ExercicioRequest> exercicios = new ArrayList<>();

    public AtualizarTreinoRequest() {
    }

    public AtualizarTreinoRequest(String nome, String descricao, String objetivo, String status, List<ExercicioRequest> exercicios) {
        this.nome = nome;
        this.descricao = descricao;
        this.objetivo = objetivo;
        this.status = status;
        this.exercicios = exercicios;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ExercicioRequest> getExercicios() {
        return exercicios;
    }

    public void setExercicios(List<ExercicioRequest> exercicios) {
        this.exercicios = exercicios;
    }
}
