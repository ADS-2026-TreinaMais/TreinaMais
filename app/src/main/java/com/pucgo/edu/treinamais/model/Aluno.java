package com.pucgo.edu.treinamais.model;

import java.util.Date;

public class Aluno {

    private Long id;
    private Long usuarioId;
    private Long professorId;
    private String cpf;
    private String dataNascimento;
    private String telefone;
    private Date criadoEm;

    private String nome;
    private String email;
    private String nomeProfessor;

    public Aluno() {
    }

    public Aluno(Long usuarioId, Long professorId, String cpf, String dataNascimento, String telefone) {
        this.usuarioId = usuarioId;
        this.professorId = professorId;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
    }

    public Aluno(Long id, Long usuarioId, Long professorId, String cpf, String dataNascimento, String telefone, String nome, String email) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.professorId = professorId;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.nome = nome;
        this.email = email;
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

    public Long getProfessorId() {
        return professorId;
    }

    public void setProfessorId(Long professorId) {
        this.professorId = professorId;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Date getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Date criadoEm) {
        this.criadoEm = criadoEm;
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

    public String getNomeProfessor() {
        return nomeProfessor;
    }

    public void setNomeProfessor(String nomeProfessor) {
        this.nomeProfessor = nomeProfessor;
    }

    @Override
    public String toString() {
        return "Aluno{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", professorId=" + professorId +
                ", cpf='" + cpf + '\'' +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}
