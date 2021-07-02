package com.kpolak.view.line;

import com.kpolak.view.MainDisplay;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.StrokeLineCap;

import java.util.LinkedList;
import java.util.List;

public class Curve {
    private static final int POINT_MERGE_THRESHOLD = 10;
    public boolean isClosed;
    List<Anchor> points;
    List<Anchor> controlPoints;
    List<Line> controlLines;
    List<QuadCurve> quadCurves;
    Group group;
    MainDisplay controller;
    double maxWidth;
    double maxHeight;

    public Curve(Group group, MainDisplay controller, double maxWidth, double maxHeight) {
        isClosed = false;
        this.controller = controller;
        points = new LinkedList<>();
        controlPoints = new LinkedList<>();
        controlLines = new LinkedList<>();
        quadCurves = new LinkedList<>();
        this.group = group;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    public void handleMoveTo(double x, double y, Anchor anchor) {
        if (isPointMergeValid(anchor)) {
            handleAnchorMerge();
        }
    }

    public void handleClick(double x, double y) {
        if (isClosed) {
            return;
        }

        Anchor clickedPoint = new Anchor(Color.TOMATO, x, y, 5, this, AnchorType.CURVE_POINT,  maxWidth, maxHeight);
//        clickedPoint.setOnMouseClicked(e -> controller.handleCurveClicked(this));

        if (!points.isEmpty()) {
            QuadCurve quadCurve = createCurve(points.get(points.size() - 1), clickedPoint);
            quadCurves.add(quadCurve);
        }

        points.add(clickedPoint);
        group.getChildren().add(clickedPoint);

        if (isPointMergeValid(clickedPoint)) {
            handleAnchorMerge();
        }
    }

    QuadCurve createCurve(Anchor from, Anchor to) {
        double x1 = from.getCenterX(), y1 = from.getCenterY();
        double x2 = to.getCenterX(), y2 = to.getCenterY();
        double distance = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        QuadCurve curve = new QuadCurve();
        curve.setOnMouseClicked(event -> {
            System.out.println("Line clicked");
            lineClicked();
//            event.consume();
        });
        curve.setStartX(x1);
        curve.setStartY(y1);
        curve.setControlX(x1 + 20 * (x2 - x1) / distance);
        curve.setControlY(y1 + 20 * (y2 - y1) / distance);
//        curve.setControlX2(x2 - 20 * (x2 - x1) / distance);
//        curve.setControlY2(y2 - 20 * (y2 - y1) / distance);
        curve.setEndX(x2);
        curve.setEndY(y2);
        curve.setStroke(Color.BLUEVIOLET);
        curve.setStrokeWidth(4);
        curve.setStrokeLineCap(StrokeLineCap.ROUND);
        curve.setFill(Color.TRANSPARENT);
        curve.startXProperty().bind(from.centerXProperty());
        curve.startYProperty().bind(from.centerYProperty());
        curve.endXProperty().bind(to.centerXProperty());
        curve.endYProperty().bind(to.centerYProperty());

        Line controlLine1 = new ControlLine(curve.controlXProperty(), curve.controlYProperty(), curve.startXProperty(), curve.startYProperty(), this);
        Line controlLine2 = new ControlLine(curve.controlXProperty(), curve.controlYProperty(), curve.endXProperty(), curve.endYProperty(), this);
        Anchor control1 = new Anchor(Color.FORESTGREEN, curve.controlXProperty(), curve.controlYProperty(), 3, AnchorType.CONTROL_POINT, maxWidth, maxHeight);

        controlPoints.add(control1);
        controlLines.add(controlLine1);
        controlLines.add(controlLine2);
//        Anchor control2 = new Anchor(Color.FORESTGREEN, curve.controlX2Property(), curve.controlY2Property(), 3);
        group.getChildren().addAll(curve, control1, controlLine2, controlLine1);
        control1.toFront();
        from.toFront();
        to.toFront();
        return curve;
    }

    public void lineClicked() {
        controller.handleCurveClicked(this);
    }

    public void highlight() {
        controlPoints.forEach(anchor -> anchor.setVisible(true));
        controlLines.forEach(line -> line.setVisible(true));
    }

    public void removeHighlight() {
        controlPoints.forEach(anchor -> anchor.setVisible(false));
        controlLines.forEach(line -> line.setVisible(false));
    }

    private boolean isPointMergeValid(Anchor anchor) {
        if (!points.get(points.size() - 1).equals(anchor)) {
            return false;
        }

        if (points.size() < 3) {
            return false;
        }

        if (Math.abs(anchor.getCenterX() - points.get(0).getCenterX()) < POINT_MERGE_THRESHOLD &&
                Math.abs(anchor.getCenterY() - points.get(0).getCenterY()) < POINT_MERGE_THRESHOLD) {
            return true;
        }
        return false;
    }

    private void handleAnchorMerge() {
        closeCurve();
        removeMergedAnchor();
        points.forEach(Anchor::toFront);
    }

    private void closeCurve() {
        QuadCurve quadCurve = quadCurves.get(quadCurves.size() - 1);
        quadCurve.endXProperty().bind(points.get(0).centerXProperty());
        quadCurve.endYProperty().bind(points.get(0).centerYProperty());
        isClosed = true;
    }

    private void removeMergedAnchor() {
        Anchor anchorToRemove = points.remove(points.size() - 1);
        anchorToRemove.prepareRemoval();
        group.getChildren().remove(anchorToRemove);
        points.remove(anchorToRemove);
    }
}
