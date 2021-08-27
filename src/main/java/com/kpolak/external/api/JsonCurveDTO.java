package com.kpolak.external.api;

import java.util.List;

public class JsonCurveDTO {
    List<JsonCurveSectionDTO> curveSections;

    public JsonCurveDTO(){
    }

    public JsonCurveDTO(List<JsonCurveSectionDTO> curveSections) {
        this.curveSections = curveSections;
    }

    public List<JsonCurveSectionDTO> getCurveSections() {
        return curveSections;
    }

    public void setCurveSections(List<JsonCurveSectionDTO> curveSections) {
        this.curveSections = curveSections;
    }

}
