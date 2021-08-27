package com.kpolak.external.api;

public class JsonCurveSectionDTO {
    private JsonPointDTO start;
    private JsonPointDTO end;
    private JsonPointDTO control;

    public JsonCurveSectionDTO(){
    }

    public JsonCurveSectionDTO(JsonPointDTO start, JsonPointDTO end, JsonPointDTO control) {
        this.start = start;
        this.end = end;
        this.control = control;
    }

    public JsonPointDTO getStart() {
        return start;
    }

    public void setStart(JsonPointDTO start) {
        this.start = start;
    }

    public JsonPointDTO getEnd() {
        return end;
    }

    public void setEnd(JsonPointDTO end) {
        this.end = end;
    }

    public JsonPointDTO getControl() {
        return control;
    }

    public void setControl(JsonPointDTO control) {
        this.control = control;
    }
}
