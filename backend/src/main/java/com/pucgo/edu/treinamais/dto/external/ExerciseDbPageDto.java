package com.pucgo.edu.treinamais.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExerciseDbPageDto {

    private Boolean success;
    private ExerciseDbMetaDto meta;
    private List<ExerciseDbItemDto> data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public ExerciseDbMetaDto getMeta() {
        return meta;
    }

    public void setMeta(ExerciseDbMetaDto meta) {
        this.meta = meta;
    }

    public List<ExerciseDbItemDto> getData() {
        return data;
    }

    public void setData(List<ExerciseDbItemDto> data) {
        this.data = data;
    }
}
