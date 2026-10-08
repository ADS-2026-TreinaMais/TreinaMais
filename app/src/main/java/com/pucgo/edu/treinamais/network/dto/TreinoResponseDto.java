package com.pucgo.edu.treinamais.network.dto;

import java.util.ArrayList;
import java.util.List;

public class TreinoResponseDto {

    private Long id;
    private String nome;
    private String descricao;
    private String objetivo;
    private String status;
    private String criadoEm;
    private Long professorId;
    private String professorNome;
    private List<ExercicioResponseDto> exercicios = new ArrayList<>();
    private List<Long> alunosIds = new ArrayList<>();

    public TreinoResponseDto() {
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

    public String getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(String criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Long getProfessorId() {
        return professorId;
    }

    public void setProfessorId(Long professorId) {
        this.professorId = professorId;
    }

    public String getProfessorNome() {
        return professorNome;
    }

    public void setProfessorNome(String professorNome) {
        this.professorNome = professorNome;
    }

    public List<ExercicioResponseDto> getExercicios() {
        return exercicios;
    }

    public void setExercicios(List<ExercicioResponseDto> exercicios) {
        this.exercicios = exercicios;
    }

    public List<Long> getAlunosIds() {
        return alunosIds;
    }

    public void setAlunosIds(List<Long> alunosIds) {
        this.alunosIds = alunosIds;
    }
}
