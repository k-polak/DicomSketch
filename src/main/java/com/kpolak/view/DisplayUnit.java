package com.kpolak.view;

import com.kpolak.api.CurveDTO;
import com.kpolak.view.line.Curve;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
        Curve newCurve = new Curve(group, this, frame.getWidth(), frame.getHeight());
        newCurve.handleClick(x, y);
        focusedCurve = newCurve;
        startedCurve = newCurve;
        curves.add(newCurve);
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
        }
    }

    public boolean isEmpty() {
        return curves.isEmpty();
    }

    public List<CurveDTO> getCurvesDTO() {
        return curves.stream()
                .map(Curve::toCurveDTO)
                .collect(Collectors.toList());
    }

    public void withCurves(List<CurveDTO> curveDTOS, Optional<UUID> highlightedCurve) {
        curves = curveDTOS.stream()
                .map(this::buildCurveFromDTO)
                .collect(Collectors.toList());
        highlightedCurve.ifPresent(this::highlightCurveById);
    }

    private void highlightCurveById(UUID uuid) {
        curves.stream()
                .filter(curve -> curve.getId().isPresent() && curve.getId().get().equals(uuid))
                .findFirst()
                .ifPresent(Curve::highlight);
    }

    private Curve buildCurveFromDTO(CurveDTO curveDTO) {
        Curve curve = new Curve(overlay, this, frame.getWidth(), frame.getHeight());
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
