package com.kpolak.view.line;

import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

class ControlLine extends Line {
    private Curve curve;
    ControlLine(DoubleProperty startX, DoubleProperty startY, DoubleProperty endX, DoubleProperty endY, Curve curve) {
        this.curve = curve;
        startXProperty().bind(startX);
        startYProperty().bind(startY);
        endXProperty().bind(endX);
        endYProperty().bind(endY);
        setStrokeWidth(2);
        setStroke(Color.FORESTGREEN.deriveColor(0, 1, 1, 0.5));

//        setOnMouseClicked(event -> {
//            System.out.println("Line clicked");
//            this.curve.lineClicked();
//            event.consume();
//        });
    }
}