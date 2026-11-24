package com.pucgo.edu.treinamais.dto.response;

import java.util.List;

public class CatalogoExercicioResponse {

    private String externalId;
    private String nome;
    private String gifUrl;
    private List<String> bodyParts;
    private List<String> equipments;
    private List<String> targetMuscles;
    private List<String> secondaryMuscles;
    private List<String> instrucoes;

    public CatalogoExercicioResponse() {
    }

    public CatalogoExercicioResponse(
            String externalId,
            String nome,
            String gifUrl,
            List<String> bodyParts,
            List<String> equipments,
            List<String> targetMuscles,
            List<String> secondaryMuscles,
            List<String> instrucoes) {
        this.externalId = externalId;
        this.nome = nome;
        this.gifUrl = gifUrl;
        this.bodyParts = bodyParts;
        this.equipments = equipments;
        this.targetMuscles = targetMuscles;
        this.secondaryMuscles = secondaryMuscles;
        this.instrucoes = instrucoes;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGifUrl() {
        return gifUrl;
    }

    public void setGifUrl(String gifUrl) {
        this.gifUrl = gifUrl;
    }

    public List<String> getBodyParts() {
        return bodyParts;
    }

    public void setBodyParts(List<String> bodyParts) {
        this.bodyParts = bodyParts;
    }

    public List<String> getEquipments() {
        return equipments;
    }

    public void setEquipments(List<String> equipments) {
        this.equipments = equipments;
    }

    public List<String> getTargetMuscles() {
        return targetMuscles;
    }

    public void setTargetMuscles(List<String> targetMuscles) {
        this.targetMuscles = targetMuscles;
    }

    public List<String> getSecondaryMuscles() {
        return secondaryMuscles;
    }

    public void setSecondaryMuscles(List<String> secondaryMuscles) {
        this.secondaryMuscles = secondaryMuscles;
    }

    public List<String> getInstrucoes() {
        return instrucoes;
    }

    public void setInstrucoes(List<String> instrucoes) {
        this.instrucoes = instrucoes;
    }
}
