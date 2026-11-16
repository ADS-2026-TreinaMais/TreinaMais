//ARQUIVO NO EXCLUDE DO BUILD.GRADLE

package com.pucgo.edu.treinamais.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.util.Date;

@Entity(primaryKeys = {"uid", "aluno_id"})
public class Aluno_jv {
    @NonNull
    @ColumnInfo(name = "uid")
    private Long uid;
    @NonNull
    @ColumnInfo(name = "aluno_id")
    private Long aluno_id;
    private String nome;
    private String email;
    private String cpf;
    private String dataNascimento;
    private String telefone;
    private String criadoEm;

    private Long prof_fk;

    public Aluno_jv() {
    }

    //ARQUIVO NO EXCLUDE DO BUILD.GRADLE
    public Aluno_jv(Long usuarioId, Long professorId, String cpf, String dataNascimento, String telefone) {
        this.usuarioId = usuarioId;
        this.professorId = professorId;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
    }

    public Aluno_jv(Long id, Long usuarioId, Long professorId, String cpf, String dataNascimento, String telefone, String nome, String email) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.professorId = professorId;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.nome = nome;
        this.email = email;
    }
    public Aluno_jv(String nome){
        this.nome = nome;
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

    public String getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(String criadoEm) {
        this.criadoEm = criadoEm;
    }

    public void setCriadoEm(Date criadoEm) {
        this.criadoEm = criadoEm != null ? criadoEm.toString() : null;
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
        return nomeProfessor != null ? nomeProfessor : professorNome;
    }

    public void setNomeProfessor(String nomeProfessor) {
        this.nomeProfessor = nomeProfessor;
    }

    public String getProfessorNome() {
        return getNomeProfessor();
    }

    public void setProfessorNome(String professorNome) {
        this.professorNome = professorNome;
        if (this.nomeProfessor == null) {
            this.nomeProfessor = professorNome;
        }
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
