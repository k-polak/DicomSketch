package com.kpolak.external.api;

import java.util.List;

public class JsonFrameDTO {
    String frameNumber;

    List<JsonCurveDTO> curves;

    public JsonFrameDTO(){}

    public JsonFrameDTO(String frameNumber, List<JsonCurveDTO> curves) {
        this.frameNumber = frameNumber;
        this.curves = curves;
    }

    public String getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(String frameNumber) {
        this.frameNumber = frameNumber;
    }

    public List<JsonCurveDTO> getCurves() {
        return curves;
    }

    public void setCurves(List<JsonCurveDTO> curves) {
        this.curves = curves;
    }
}
