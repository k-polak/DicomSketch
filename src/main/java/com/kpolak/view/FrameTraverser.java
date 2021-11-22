package com.kpolak.view;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.kpolak.model.curve.CurveDTO;
import com.kpolak.model.curve.CurveSectionDTO;
import com.kpolak.model.curve.PointDTO;
import com.kpolak.external.api.JsonCurveDTO;
import com.kpolak.external.api.JsonCurveSectionDTO;
import com.kpolak.external.api.JsonDicomOutlineDTO;
import com.kpolak.external.api.JsonPointDTO;
import com.kpolak.model.dicom.Dicom;
import com.kpolak.view.line.Curve;

import java.awt.image.BufferedImage;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class FrameTraverser {
    MainDisplay mainDisplay;
    Dicom dicom;
    long size;
    long notNullFrames;
    int currentIndex;
    TreeMap<Integer, DisplayUnit> loadedFrames;

    public FrameTraverser(MainDisplay mainDisplay, Dicom dicom) {
        this.mainDisplay = mainDisplay;
        this.dicom = dicom;
        loadedFrames = ((TreeMap<Integer, BufferedImage>) dicom.getFrames()).entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<Integer, DisplayUnit>(entry.getKey(), new DisplayUnit(entry.getValue(), entry.getKey(), mainDisplay)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o1, o2) -> {
                    System.out.println("[ERROR] Found duplicate while creating frame traverser");
                    return o2;
                }, TreeMap::new));

        size = loadedFrames.lastKey();
        notNullFrames = loadedFrames.values().stream().filter(Objects::nonNull).count();
        currentIndex = loadedFrames.firstKey();
        dicom.getDicomOutlineDTO().ifPresent(this::loadOutline);
    }

    public DisplayUnit current() {
        return loadedFrames.get(currentIndex);
    }

    public DisplayUnit next() {
        DisplayUnit currentDisplayUnit = current();
        Integer higherKey = loadedFrames.higherKey(currentIndex);
        if (higherKey != null) {
            currentIndex = higherKey;
        }
        DisplayUnit nextDisplayUnit = current();
        if (!currentDisplayUnit.isEmpty() && nextDisplayUnit.isEmpty()) {
            Curve currentFocusedCurve = currentDisplayUnit.getFocusedCurve();
            Optional<String> curveUUID = currentFocusedCurve == null ? Optional.empty() : currentFocusedCurve.getId();
            nextDisplayUnit.withCurves(currentDisplayUnit.getCurvesDTO(), curveUUID);
        }
        return current();
    }

    public DisplayUnit previous() {
        Integer lowerKey = loadedFrames.lowerKey(currentIndex);
        if (lowerKey != null) {
            currentIndex = lowerKey;
        }
        return current();
    }

    public Collection<DisplayUnit> getAllDisplayUnits() {
        return loadedFrames.values();
    }

    public int getCurrentPositionInLoadedFrames() {
        return getNumberOfLoadedFrames() - Iterables.indexOf(loadedFrames.descendingKeySet(), id -> id.equals(current().frameId));
    }

    public int getNumberOfLoadedFrames() {
        return loadedFrames.size();
    }

    public void loadAnotherFrame(Dicom dicom) {
        Set<Integer> newFrames = Sets.difference(dicom.getFrames().keySet(), loadedFrames.keySet());

        if (newFrames.size() > 1) {
            throw new RuntimeException("Updating FrameTraverser with multiframe dicom");
        } else if (newFrames.isEmpty()) {
            throw new RuntimeException("Error while loading next frame into existing frameTraverser");
        }

        Integer frameId = newFrames.iterator().next();
        loadedFrames.put(frameId, new DisplayUnit(dicom.getFrames().get(frameId), frameId, mainDisplay));
    }

    private void loadOutline(JsonDicomOutlineDTO outline) {
        outline.getFrames().stream()
                .filter(jsonFrame -> !jsonFrame.getCurves().isEmpty())
                .forEach(frame -> {
                    DisplayUnit displayUnit = loadedFrames.get(Integer.parseInt(frame.getFrameNumber()));
                    if (displayUnit != null) {
                        displayUnit.clearCurves();
                        displayUnit.withCurvesFromFile(mapToCurvesDTO(frame.getCurves()));
                    }
                });
    }

    private List<CurveDTO> mapToCurvesDTO(List<JsonCurveDTO> jsonCurves) {
        return jsonCurves.stream()
                .map(this::mapToCurvesDTO)
                .collect(Collectors.toList());
    }

    private CurveDTO mapToCurvesDTO(JsonCurveDTO jsonCurve) {
        List<CurveSectionDTO> curveSection = jsonCurve.getCurveSections().stream()
                .map(this::mapToCurveSectionDTO)
                .collect(Collectors.toList());
        return new CurveDTO(curveSection, Optional.empty());
    }

    private CurveSectionDTO mapToCurveSectionDTO(JsonCurveSectionDTO jsonCurveSection) {
        return new CurveSectionDTO(
                mapToPointDTO(jsonCurveSection.getStart()),
                mapToPointDTO(jsonCurveSection.getEnd()),
                mapToPointDTO(jsonCurveSection.getControl())
        );
    }

    private PointDTO mapToPointDTO(JsonPointDTO jsonPoint) {
        return new PointDTO(jsonPoint.getX(), jsonPoint.getY());
    }
}
