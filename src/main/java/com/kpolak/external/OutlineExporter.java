package com.kpolak.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpolak.api.CurveDTO;
import com.kpolak.api.CurveSectionDTO;
import com.kpolak.api.PointDTO;
import com.kpolak.external.api.JsonCurveDTO;
import com.kpolak.external.api.JsonCurveSectionDTO;
import com.kpolak.external.api.JsonDicomOutlineDTO;
import com.kpolak.external.api.JsonFrameDTO;
import com.kpolak.external.api.JsonPointDTO;
import com.kpolak.model.Dicom;
import com.kpolak.view.DisplayUnit;
import com.kpolak.view.FrameTraverser;
import com.kpolak.view.line.Curve;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;


public class OutlineExporter {
    private final FrameTraverser frameTraverser;
    private final ObjectMapper objectMapper;

    public OutlineExporter(FrameTraverser frameTraverser) {
        this.frameTraverser = frameTraverser;
        objectMapper = new ObjectMapper();
    }

    public void exportOutline(Dicom dicom) {
        List<JsonFrameDTO> jsonFrames = frameTraverser.getAllDisplayUnits().stream()
                .map(this::mapDisplayUnitToJsonFrame)
                .collect(Collectors.toList());
        JsonDicomOutlineDTO jsonDicomOutlineDTO = new JsonDicomOutlineDTO(jsonFrames);

        try {
            objectMapper.writeValue(new File(OutlinePathRetriever.retrieveJsonOutlinePath(dicom)), jsonDicomOutlineDTO);
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException("Error while writing curves to json file.", e);
        }
    }

    private JsonFrameDTO mapDisplayUnitToJsonFrame(DisplayUnit displayUnit) {
        List<JsonCurveDTO> jsonCurves = displayUnit.getCurves().stream()
                .map(this::mapCurveToJsonCurve)
                .collect(Collectors.toList());
        return new JsonFrameDTO(String.valueOf(displayUnit.getFrameId()), jsonCurves);
    }

    private JsonCurveDTO mapCurveToJsonCurve(Curve curve) {
        return mapCurveDTOToJsonCurve(curve.toCurveDTO());
    }

    private JsonCurveDTO mapCurveDTOToJsonCurve(CurveDTO curve) {
        List<JsonCurveSectionDTO> jsonCurveSections = curve.getCurveSectionDTOS().stream()
                .map(this::mapCurveSectionToJsonCurveSection)
                .collect(Collectors.toList());
        return new JsonCurveDTO(jsonCurveSections);
    }

    private JsonCurveSectionDTO mapCurveSectionToJsonCurveSection(CurveSectionDTO curveSection) {
        return new JsonCurveSectionDTO(
                mapPointToJsonPoint(curveSection.getStart()),
                mapPointToJsonPoint(curveSection.getEnd()),
                mapPointToJsonPoint(curveSection.getControl())
        );
    }

    private JsonPointDTO mapPointToJsonPoint(PointDTO point) {
        return new JsonPointDTO(point.getX(), point.getY());
    }
}