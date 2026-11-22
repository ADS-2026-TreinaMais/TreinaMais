//ARQUIVO NO EXCLUDE DO BUILD.GRADLE

package com.pucgo.edu.treinamais.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.util.Date;

@Entity(primaryKeys = {"uid", "aluno_id"})
public class Aluno {
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

    public Aluno() {
    }

    public Aluno(Long usuarioId, Long professorId, String cpf, String dataNascimento, String telefone) {
        this.uid = usuarioId;
        this.prof_fk = professorId;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
    }

    public Aluno(@NonNull Long id, Long usuarioId, Long professorId, String cpf, String dataNascimento, String telefone, String nome, String email) {
        this.aluno_id = id;
        this.uid = usuarioId;
        this.prof_fk = professorId;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.nome = nome;
        this.email = email;
    }
    public Aluno(String nome){
        this.nome = nome;
    }

    public Long getId() {
        return aluno_id;
    }
    public void setId(Long id) {
        this.aluno_id = id;
    }

    public Long getUsuarioId() {
        return uid;
    }
    public void setUsuarioId(Long usuarioId) {
        this.uid = usuarioId;
    }

    public Long getProfessorId() {
        return prof_fk;
    }
    public void setProfessorId(Long professorId) {
        this.prof_fk = professorId;
    }

    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDataNascimento() {return dataNascimento;}
    public void setDataNascimento(String dataNascimento) {this.dataNascimento = dataNascimento;}

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

//    public String getNomeProfessor() {
//        return nomeProfessor != null ? nomeProfessor : professorNome;
//    }

//    public void setNomeProfessor(String nomeProfessor) {
//        this.nomeProfessor = nomeProfessor;
//    }
//
//    public String getProfessorNome() {
//        return getNomeProfessor();
//    }
//
//    public void setProfessorNome(String professorNome) {
//        this.professorNome = professorNome;
//        if (this.nomeProfessor == null) {
//            this.nomeProfessor = professorNome;
//        }
//    }

    @Override
    public String toString() {
        return "Aluno{" +
                "id=" + aluno_id +
                ", usuarioId=" + uid +
                ", professorId=" + prof_fk +
                ", cpf='" + cpf + '\'' +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}
