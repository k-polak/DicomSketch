package com.kpolak.view;

import com.kpolak.model.Dicom;
import com.kpolak.view.line.Curve;

import java.awt.image.BufferedImage;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
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
            Optional<UUID> curveUUID = currentFocusedCurve == null ? Optional.empty() : currentFocusedCurve.getId();
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
}
