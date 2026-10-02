package com.pucgo.edu.treinamais.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ExercicioRequest {

    @NotBlank(message = "O nome do exercício é obrigatório")
    private String nomeExercicio;

    @NotNull(message = "A quantidade de séries é obrigatória")
    @Min(value = 1, message = "A série deve ser maior que zero")
    private Integer series = 3;

    @NotBlank(message = "O número de repetições é obrigatório")
    private String repeticoes = "10-12";

    private String carga;

    private Integer descansoSegundos = 60;

    private Integer ordem = 1;

    private String observacoes;

    public ExercicioRequest() {
    }

    public ExercicioRequest(String nomeExercicio, Integer series, String repeticoes, String carga, Integer descansoSegundos, Integer ordem, String observacoes) {
        this.nomeExercicio = nomeExercicio;
        this.series = series;
        this.repeticoes = repeticoes;
        this.carga = carga;
        this.descansoSegundos = descansoSegundos;
        this.ordem = ordem;
        this.observacoes = observacoes;
    }

    public String getNomeExercicio() {
        return nomeExercicio;
    }

    public void setNomeExercicio(String nomeExercicio) {
        this.nomeExercicio = nomeExercicio;
    }

    public Integer getSeries() {
        return series;
    }

    public void setSeries(Integer series) {
        this.series = series;
    }

    public String getRepeticoes() {
        return repeticoes;
    }

    public void setRepeticoes(String repeticoes) {
        this.repeticoes = repeticoes;
    }

    public String getCarga() {
        return carga;
    }

    public void setCarga(String carga) {
        this.carga = carga;
    }

    public Integer getDescansoSegundos() {
        return descansoSegundos;
    }

    public void setDescansoSegundos(Integer descansoSegundos) {
        this.descansoSegundos = descansoSegundos;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
