package com.kpolak.view.line;

import com.kpolak.api.CurveDTO;
import com.kpolak.api.CurveSectionDTO;
import com.kpolak.api.PointDTO;
import com.kpolak.view.DisplayUnit;
import com.kpolak.view.SimpleSequenceGenerator;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.StrokeLineCap;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Curve {
    private static final int POINT_MERGE_THRESHOLD = 10;
    private Optional<String> id;
    public boolean isClosed;
    List<Anchor> points;
    List<Anchor> controlPoints;
    List<Line> controlLines;
    List<QuadCurve> quadCurves;
    Group group;
    DisplayUnit controller;
    double maxWidth;
    double maxHeight;
    double xScale;
    double yScale;
    boolean alreadyScaled;

    public Curve(Group group, DisplayUnit controller, double maxWidth, double maxHeight, Optional<String> id, double xScale, double yScale, boolean alreadyScaled) {
        if (id.isPresent()) {
            this.id = id;
        } else {
            this.id = Optional.of(SimpleSequenceGenerator.next());
        }

        isClosed = false;
        this.controller = controller;
        points = new LinkedList<>();
        controlPoints = new LinkedList<>();
        controlLines = new LinkedList<>();
        quadCurves = new LinkedList<>();
        this.group = group;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.xScale = xScale;
        this.yScale = yScale;
        this.alreadyScaled = alreadyScaled;
    }

    public Optional<String> getId() {
        return id;
    }

    public List<Anchor> getPoints() {
        return points;
    }

    public List<Anchor> getControlPoints() {
        return controlPoints;
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

        Anchor clickedPoint = new Anchor(Color.TOMATO, x, y, 5, this, AnchorType.CURVE_POINT, maxWidth, maxHeight);
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
        QuadCurve curve = new QuadCurve();
        curve.setOnMouseClicked(event -> {
            System.out.println("Line clicked");
            lineClicked();
//            event.consume();
        });
        curve.setStartX(x1);
        curve.setStartY(y1);

        double[] controlPointXY = calculateControlXY(from, to);
        curve.setControlX(controlPointXY[0]);
        curve.setControlY(controlPointXY[1]);
        curve.setEndX(x2);
        curve.setEndY(y2);
        curve.setStroke(Color.color(0.5411765f, 0.16862746f, 0.8862745f, 0.6f));
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
        group.getChildren().addAll(curve, control1, controlLine2, controlLine1);
        control1.toFront();
        from.toFront();
        to.toFront();
        return curve;
    }

    public void clear() {
        points.forEach(point -> group.getChildren().remove(point));
        controlPoints.forEach(controlPoint -> group.getChildren().remove(controlPoint));
        controlLines.forEach(controlLine -> group.getChildren().remove(controlLine));
        quadCurves.forEach(quadCurve -> group.getChildren().remove(quadCurve));
    }

    private QuadCurve createInitialNewCurve(double fromX, double fromY, double controlX, double controlY, double toX, double toY) {
        QuadCurve curve = new QuadCurve();
        curve.setOnMouseClicked(event -> {
            System.out.println("Line clicked");
            lineClicked();
//            event.consume();
        });

        curve.setStroke(Color.color(0.5411765f, 0.16862746f, 0.8862745f, 0.6f));
        curve.setStrokeWidth(4);
        curve.setStrokeLineCap(StrokeLineCap.ROUND);
        curve.setFill(Color.TRANSPARENT);

        curve.setStartX(fromX);
        curve.setStartY(fromY);
        curve.setControlX(controlX);
        curve.setControlY(controlY);
        curve.setEndX(toX);
        curve.setEndY(toY);
        return curve;
    }

    private double[] calculateControlXY(Anchor from, Anchor to) {
        double distance = 20;
        double x1 = from.getCenterX(), y1 = from.getCenterY();
        double x2 = to.getCenterX(), y2 = to.getCenterY();
        double a = (y2 - y1) / (x2 - x1);
        double cx = (x1 + x2) / 2, cy = (y1 + y2) / 2;


        double outX = cx + Math.sqrt(distance * distance / (1 + 1 / (a * a)));

        double delta = 4 * cy * cy - 4 * (cy * cy + (outX - cx) * (outX - cx) - (distance * distance));
        double outY = (2 * cy - Math.sqrt(delta)) / 2;
        return new double[]{outX, outY};
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

    private QuadCurve getLastCurve() {
        return quadCurves.get(quadCurves.size() - 1);
    }

    private Anchor getLastPoint() {
        return points.get(points.size() - 1);
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

    public CurveDTO toCurveDTO() {
        List<CurveSectionDTO> curveSections = quadCurves.stream()
                .map(this::mapQuadCurveToCurveSectionDTO)
                .collect(Collectors.toList());
        return new CurveDTO(curveSections, id);
    }

    private CurveSectionDTO mapQuadCurveToCurveSectionDTO(QuadCurve quadCurve) {
        return CurveSectionDTO.builder()
                .start(new PointDTO(quadCurve.getStartX(), quadCurve.getStartY()))
                .control(new PointDTO(quadCurve.getControlX(), quadCurve.getControlY()))
                .end(new PointDTO(quadCurve.getEndX(), quadCurve.getEndY()))
                .build();
    }

    public void fromCurveDTO(CurveDTO curveDTO) {
        List<CurveSectionDTO> curveSectionDTOS = curveDTO.getCurveSectionDTOS();
        curveSectionDTOS.stream()
                .map(this::mapToQuadCurve)
                .forEach(quadCurves::add);

        for (int i = 0; i < quadCurves.size(); i++) {
            if (i == 0) {
                initFirstQuadCurve();
            } else if (i == quadCurves.size() - 1) {
                initLastQuadCurve();
            } else {
                initMiddleQuadCurve(i);
            }
        }
        isClosed = true;
    }

    private QuadCurve mapToQuadCurve(CurveSectionDTO curveSectionDTO) {
        PointDTO start = curveSectionDTO.getStart();
        PointDTO control = curveSectionDTO.getControl();
        PointDTO end = curveSectionDTO.getEnd();
        double xScale = alreadyScaled ? 1.0 : this.xScale;
        double yScale = alreadyScaled ? 1.0 : this.yScale;
        return createInitialNewCurve(start.getX() * xScale, start.getY() * yScale,
                control.getX() * xScale, control.getY() * yScale, end.getX() * xScale, end.getY() * yScale);
    }

    private void initFirstQuadCurve() {
        QuadCurve curve = quadCurves.get(0);
        Anchor start = new Anchor(Color.TOMATO, curve.getStartX(), curve.getStartY(), 5, this, AnchorType.CURVE_POINT, maxWidth, maxHeight);
        Anchor end = new Anchor(Color.TOMATO, curve.getEndX(), curve.getEndY(), 5, this, AnchorType.CURVE_POINT, maxWidth, maxHeight);
        Anchor control = new Anchor(Color.FORESTGREEN, curve.controlXProperty(), curve.controlYProperty(), 3, AnchorType.CONTROL_POINT, maxWidth, maxHeight);

        curve.startXProperty().bind(start.centerXProperty());
        curve.startYProperty().bind(start.centerYProperty());

        curve.endXProperty().bind(end.centerXProperty());
        curve.endYProperty().bind(end.centerYProperty());

        Line controlLineToStart = new ControlLine(curve.controlXProperty(), curve.controlYProperty(), curve.startXProperty(), curve.startYProperty(), this);
        Line controlLineToEnd = new ControlLine(curve.controlXProperty(), curve.controlYProperty(), curve.endXProperty(), curve.endYProperty(), this);

        points.add(start);
        points.add(end);

        controlPoints.add(control);
        controlLines.add(controlLineToStart);
        controlLines.add(controlLineToEnd);
        group.getChildren().addAll(curve, control, controlLineToStart, controlLineToEnd, start, end);
        control.toFront();
        start.toFront();
        end.toFront();
    }

    private void initMiddleQuadCurve(int position) {
        QuadCurve curve = quadCurves.get(position);
        Anchor start = getLastPoint();
        Anchor end = new Anchor(Color.TOMATO, curve.getEndX(), curve.getEndY(), 5, this, AnchorType.CURVE_POINT, maxWidth, maxHeight);
        Anchor control = new Anchor(Color.FORESTGREEN, curve.controlXProperty(), curve.controlYProperty(), 3, AnchorType.CONTROL_POINT, maxWidth, maxHeight);

        curve.startXProperty().bind(start.centerXProperty());
        curve.startYProperty().bind(start.centerYProperty());

        curve.endXProperty().bind(end.centerXProperty());
        curve.endYProperty().bind(end.centerYProperty());

        Line controlLineToStart = new ControlLine(curve.controlXProperty(), curve.controlYProperty(), curve.startXProperty(), curve.startYProperty(), this);
        Line controlLineToEnd = new ControlLine(curve.controlXProperty(), curve.controlYProperty(), curve.endXProperty(), curve.endYProperty(), this);

        points.add(end);
        controlPoints.add(control);
        controlLines.add(controlLineToStart);
        controlLines.add(controlLineToEnd);
        group.getChildren().addAll(curve, control, controlLineToStart, controlLineToEnd, end);
        control.toFront();
        start.toFront();
        end.toFront();
    }

    private void initLastQuadCurve() {
        QuadCurve curve = getLastCurve();
        Anchor start = getLastPoint();
        Anchor end = points.get(0);
        Anchor control = new Anchor(Color.FORESTGREEN, curve.controlXProperty(), curve.controlYProperty(), 3, AnchorType.CONTROL_POINT, maxWidth, maxHeight);

        curve.startXProperty().bind(start.centerXProperty());
        curve.startYProperty().bind(start.centerYProperty());

        curve.endXProperty().bind(end.centerXProperty());
        curve.endYProperty().bind(end.centerYProperty());

        Line controlLineToStart = new ControlLine(curve.controlXProperty(), curve.controlYProperty(), curve.startXProperty(), curve.startYProperty(), this);
        Line controlLineToEnd = new ControlLine(curve.controlXProperty(), curve.controlYProperty(), curve.endXProperty(), curve.endYProperty(), this);

        controlPoints.add(control);
        controlLines.add(controlLineToStart);
        controlLines.add(controlLineToEnd);
        group.getChildren().addAll(curve, control, controlLineToStart, controlLineToEnd);
        control.toFront();
        start.toFront();
        end.toFront();
    }
}
