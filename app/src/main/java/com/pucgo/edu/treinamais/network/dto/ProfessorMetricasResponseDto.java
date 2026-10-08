package com.pucgo.edu.treinamais.network.dto;

public class ProfessorMetricasResponseDto {

    private long totalAlunos;
    private long totalTreinos;
    private long alunosAtivos;

    public ProfessorMetricasResponseDto() {
    }

    public ProfessorMetricasResponseDto(long totalAlunos, long totalTreinos, long alunosAtivos) {
        this.totalAlunos = totalAlunos;
        this.totalTreinos = totalTreinos;
        this.alunosAtivos = alunosAtivos;
    }

    public long getTotalAlunos() {
        return totalAlunos;
    }

    public void setTotalAlunos(long totalAlunos) {
        this.totalAlunos = totalAlunos;
    }

    public long getTotalTreinos() {
        return totalTreinos;
    }

    public void setTotalTreinos(long totalTreinos) {
        this.totalTreinos = totalTreinos;
    }

    public long getAlunosAtivos() {
        return alunosAtivos;
    }

    public void setAlunosAtivos(long alunosAtivos) {
        this.alunosAtivos = alunosAtivos;
    }
}
