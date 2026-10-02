package com.pucgo.edu.treinamais.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class CriarTreinoRequest {

    @NotBlank(message = "O nome do treino é obrigatório")
    private String nome;

    private String descricao;

    private String objetivo;

    @NotEmpty(message = "O treino deve possuir pelo menos 1 exercício cadastrado")
    @Valid
    private List<ExercicioRequest> exercicios = new ArrayList<>();

    private List<Long> alunoIds = new ArrayList<>();

    public CriarTreinoRequest() {
    }

    public CriarTreinoRequest(String nome, String descricao, String objetivo, List<ExercicioRequest> exercicios, List<Long> alunoIds) {
        this.nome = nome;
        this.descricao = descricao;
        this.objetivo = objetivo;
        this.exercicios = exercicios;
        this.alunoIds = alunoIds;
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

    public List<ExercicioRequest> getExercicios() {
        return exercicios;
    }

    public void setExercicios(List<ExercicioRequest> exercicios) {
        this.exercicios = exercicios;
    }

    public List<Long> getAlunoIds() {
        return alunoIds;
    }

    public void setAlunoIds(List<Long> alunoIds) {
        this.alunoIds = alunoIds;
    }
}
