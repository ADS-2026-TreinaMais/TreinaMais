package com.pucgo.edu.treinamais.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExerciseDbSingleDto {

    private Boolean success;
    private ExerciseDbItemDto data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public ExerciseDbItemDto getData() {
        return data;
    }

    public void setData(ExerciseDbItemDto data) {
        this.data = data;
    }
}
