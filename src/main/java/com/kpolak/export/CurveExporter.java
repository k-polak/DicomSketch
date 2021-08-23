package com.kpolak.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpolak.view.FrameTraverser;


public class CurveExporter {
    private FrameTraverser frameTraverser;

    public CurveExporter(FrameTraverser frameTraverser) {
        this.frameTraverser = frameTraverser;
        ObjectMapper objectMapper = new ObjectMapper();
    }
}
