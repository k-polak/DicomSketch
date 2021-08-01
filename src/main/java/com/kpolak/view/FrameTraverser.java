package com.kpolak.view;

import com.kpolak.model.Dicom;
import com.kpolak.view.line.Curve;

import java.util.ListIterator;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FrameTraverser {
    MainDisplay mainDisplay;
    Dicom dicom;
    ListIterator<DisplayUnit> displayUnitsIterator;
    DisplayUnit currentDisplayUnit;

    public FrameTraverser(MainDisplay mainDisplay, Dicom dicom) {
        this.mainDisplay = mainDisplay;
        this.dicom = dicom;
        displayUnitsIterator = dicom.getFrames().entrySet().stream()
                .map(entry -> new DisplayUnit(entry.getValue(), entry.getKey(), mainDisplay))
                .collect(Collectors.toList())
                .listIterator();
    }

    public DisplayUnit current() {
        if (currentDisplayUnit != null) {
            return currentDisplayUnit;
        } else {
            throw new RuntimeException("There is no current display unit");
        }
    }

    public DisplayUnit next() {
        if (displayUnitsIterator.hasNext()) {
            currentDisplayUnit = getNextWithCurvesIfEmpty();
        }
        return current();
    }

    public DisplayUnit previous() {
        if (displayUnitsIterator.hasPrevious()) {
            displayUnitsIterator.previous();
            if (displayUnitsIterator.hasPrevious()) {
                currentDisplayUnit = displayUnitsIterator.previous();
            } else {
                displayUnitsIterator.next();
            }
        }
        return current();
    }

    private DisplayUnit getNextWithCurvesIfEmpty() {
        if (currentDisplayUnit == null) {
            return displayUnitsIterator.next();
        }
        DisplayUnit next = displayUnitsIterator.next();
        if (next.isEmpty()) {
            Curve currentFocusedCurve = currentDisplayUnit.getFocusedCurve();
            Optional<UUID> curveUUID = currentFocusedCurve == null ? Optional.empty() : currentFocusedCurve.getId();
            next.withCurves(currentDisplayUnit.getCurvesDTO(), curveUUID);
        }
        return next;
    }
}
