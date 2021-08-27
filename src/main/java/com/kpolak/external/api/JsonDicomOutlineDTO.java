package com.kpolak.external.api;

import java.util.List;

public class JsonDicomOutlineDTO {
    List<JsonFrameDTO> frames;

    public JsonDicomOutlineDTO() {
    }

    public JsonDicomOutlineDTO(List<JsonFrameDTO> frames) {
        this.frames = frames;
    }

    public List<JsonFrameDTO> getFrames() {
        return frames;
    }

    public void setFrames(List<JsonFrameDTO> frames) {
        this.frames = frames;
    }
}
