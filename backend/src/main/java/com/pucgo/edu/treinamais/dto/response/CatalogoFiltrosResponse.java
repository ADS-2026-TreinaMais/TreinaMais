package com.pucgo.edu.treinamais.dto.response;

import java.util.List;

public class CatalogoFiltrosResponse {

    private List<String> bodyParts;
    private List<String> muscles;
    private List<String> equipments;

    public CatalogoFiltrosResponse() {
    }

    public CatalogoFiltrosResponse(List<String> bodyParts, List<String> muscles, List<String> equipments) {
        this.bodyParts = bodyParts;
        this.muscles = muscles;
        this.equipments = equipments;
    }

    public List<String> getBodyParts() {
        return bodyParts;
    }

    public void setBodyParts(List<String> bodyParts) {
        this.bodyParts = bodyParts;
    }

    public List<String> getMuscles() {
        return muscles;
    }

    public void setMuscles(List<String> muscles) {
        this.muscles = muscles;
    }

    public List<String> getEquipments() {
        return equipments;
    }

    public void setEquipments(List<String> equipments) {
        this.equipments = equipments;
    }
}
