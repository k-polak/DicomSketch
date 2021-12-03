package com.kpolak.view;

import com.kpolak.model.curve.CurveDTO;
import com.kpolak.view.line.Curve;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DisplayUnit {
    private List<Curve> curves = new LinkedList<>();
    private Curve focusedCurve = null;
    private Curve startedCurve = null;
    BufferedImage frame;
    Group overlay;
    MainDisplay mainDisplay;
    Integer frameId;

    public DisplayUnit(BufferedImage frame, Integer frameId, MainDisplay mainDisplay) {
        this.frame = frame;
        this.frameId = frameId;
        this.mainDisplay = mainDisplay;
        this.overlay = new Group();
    }

    private void createNewCurve(double x, double y, Group group) {
        Curve newCurve = new Curve(group, this, mainDisplay.getWidth(), mainDisplay.getHeight(),
                Optional.empty(), mainDisplay.getXScale(), mainDisplay.getYScale(), false);
        newCurve.handleClick(x, y);
        focusedCurve = newCurve;
        startedCurve = newCurve;
        curves.add(newCurve);
    }

    public Integer getFrameId() {
        return frameId;
    }

    public List<Curve> getCurves() {
        return curves;
    }

    public Curve getFocusedCurve() {
        return focusedCurve;
    }

    public void handleCurveClicked(Curve curve) {
        if (focusedCurve != null && !focusedCurve.equals(curve)) {
            focusedCurve.removeHighlight();
            curve.highlight();
            focusedCurve = curve;
            startedCurve = curve;
        }
    }

    public boolean isEmpty() {
        return curves.isEmpty();
    }

    public void clearCurves() {
        curves.clear();
        focusedCurve = null;
        startedCurve = null;
    }

    public List<CurveDTO> getCurvesDTO() {
        return curves.stream()
                .map(Curve::toCurveDTO)
                .collect(Collectors.toList());
    }

    public void withCurves(List<CurveDTO> curveDTOS, Optional<String> highlightedCurve) {
        curves = curveDTOS.stream()
                .map(dto -> buildCurveFromDTO(dto, true))
                .collect(Collectors.toList());
        curves.forEach(Curve::removeHighlight);
        highlightedCurve.ifPresent(this::highlightCurveById);
    }

    public void withCurvesFromFile(List<CurveDTO> curveDTOS) {
        curves = curveDTOS.stream()
                .map(dto -> buildCurveFromDTO(dto, false))
                .collect(Collectors.toList());
        curves.forEach(Curve::removeHighlight);
        curves.get(0).highlight();
        focusedCurve = curves.get(0);
        startedCurve = curves.get(0);
    }

    private void highlightCurveById(String uuid) {
        curves.stream()
                .filter(curve -> curve.getId().isPresent() && curve.getId().get().equals(uuid))
                .findFirst()
                .ifPresent(curve -> {
                    curve.highlight();
                    focusedCurve = curve;
                    startedCurve = curve;
                });
    }

    public void handleDelete() {
        if (focusedCurve == null) {
            return;
        }

        focusedCurve.clear();
        curves.remove(focusedCurve);
        if (curves.isEmpty()) {
            focusedCurve = null;
            startedCurve = null;
        } else {
            focusedCurve = curves.get(0);
            focusedCurve.highlight();
            startedCurve = focusedCurve;
        }
    }

    private Curve buildCurveFromDTO(CurveDTO curveDTO, boolean alreadyScaled) {
        Curve curve = new Curve(overlay, this, MainDisplay.MAX_WIDTH, MainDisplay.MAX_HEIGHT,
                curveDTO.getId(), mainDisplay.getXScale(), mainDisplay.getYScale(), alreadyScaled);
        curve.fromCurveDTO(curveDTO);
        return curve;
    }

    public void handleMouseEvent(MouseEvent event) {
        double x = event.getX(), y = event.getY();
        System.out.println("Clicked x: " + x + "  y: " + y);

        if (event.getButton() == MouseButton.SECONDARY) {
            if (curves.isEmpty()) {
                createNewCurve(x, y, overlay);
            } else {
                if (focusedCurve != null) {
                    if (focusedCurve.isClosed) {
                        curves.forEach(Curve::removeHighlight);
                        if (!focusedCurve.equals(startedCurve)) {
                            startedCurve.highlight();
                            startedCurve.handleClick(x, y);
                            focusedCurve = startedCurve;
                        } else {
                            createNewCurve(x, y, overlay);
                        }
                    } else {
                        focusedCurve.handleClick(x, y);
                    }
                } else {
                    createNewCurve(x, y, overlay);
                }
            }
        } else if (event.getButton() == MouseButton.PRIMARY) {
            //
        }
    }
}
