package com.pucgo.edu.treinamais.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TreinoResponse {

    private Long id;
    private String nome;
    private String descricao;
    private String objetivo;
    private String status;
    private LocalDateTime criadoEm;
    private Long professorId;
    private String professorNome;
    private List<ExercicioResponse> exercicios = new ArrayList<>();
    private List<Long> alunosIds = new ArrayList<>();

    public TreinoResponse() {
    }

    public TreinoResponse(Long id, String nome, String descricao, String objetivo, String status, LocalDateTime criadoEm, Long professorId, String professorNome, List<ExercicioResponse> exercicios, List<Long> alunosIds) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.objetivo = objetivo;
        this.status = status;
        this.criadoEm = criadoEm;
        this.professorId = professorId;
        this.professorNome = professorNome;
        this.exercicios = exercicios;
        this.alunosIds = alunosIds;
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

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
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

    public List<ExercicioResponse> getExercicios() {
        return exercicios;
    }

    public void setExercicios(List<ExercicioResponse> exercicios) {
        this.exercicios = exercicios;
    }

    public List<Long> getAlunosIds() {
        return alunosIds;
    }

    public void setAlunosIds(List<Long> alunosIds) {
        this.alunosIds = alunosIds;
    }
}
