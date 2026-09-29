package com.pucgo.edu.treinamais.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "treinos")
public class Treino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    private Professor professor;

    @NotBlank(message = "O nome do treino é obrigatório")
    @Column(nullable = false, length = 100)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(length = 100)
    private String objetivo;

    @Column(nullable = false, length = 20)
    private String status = "ATIVO";

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @OneToMany(mappedBy = "treino", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExercicioTreino> exercicios = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
    }

    public Treino() {
    }

    public Treino(Professor professor, String nome, String descricao, String objetivo) {
        this.professor = professor;
        this.nome = nome;
        this.descricao = descricao;
        this.objetivo = objetivo;
    }

    public void adicionarExercicio(ExercicioTreino exercicio) {
        exercicios.add(exercicio);
        exercicio.setTreino(this);
    }

    public void removerExercicio(ExercicioTreino exercicio) {
        exercicios.remove(exercicio);
        exercicio.setTreino(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
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

    public List<ExercicioTreino> getExercicios() {
        return exercicios;
    }

    public void setExercicios(List<ExercicioTreino> exercicios) {
        this.exercicios = exercicios;
    }
}
