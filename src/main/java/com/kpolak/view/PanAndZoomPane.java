package com.kpolak.view;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class PanAndZoomPane extends Pane {
    public static final double DEFAULT_DELTA = 1.3d;
    DoubleProperty myScale = new SimpleDoubleProperty(1.0);
    public DoubleProperty deltaY = new SimpleDoubleProperty(0.0);
    private Timeline timeline;

    public PanAndZoomPane() {

        this.timeline = new Timeline(60);

        // add scale transform
        scaleXProperty().bind(myScale);
        scaleYProperty().bind(myScale);
    }


    public double getScale() {
        return myScale.get();
    }

    public void setScale(double scale) {
        myScale.set(scale);
    }

    public void setPivot(double x, double y, double scale) {
        // note: pivot value must be untransformed, i. e. without scaling
        // timeline that scales and moves the node
        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(200), new KeyValue(translateXProperty(), getTranslateX() - x)),
                new KeyFrame(Duration.millis(200), new KeyValue(translateYProperty(), getTranslateY() - y)),
                new KeyFrame(Duration.millis(200), new KeyValue(myScale, scale))
        );
        timeline.play();

    }

    public void fitWidth() {
        double scale = getParent().getLayoutBounds().getMaxX() / getLayoutBounds().getMaxX();
        double oldScale = getScale();

        double f = scale - oldScale;

        double dx = getTranslateX() - getBoundsInParent().getMinX() - getBoundsInParent().getWidth() / 2;
        double dy = getTranslateY() - getBoundsInParent().getMinY() - getBoundsInParent().getHeight() / 2;

        double newX = f * dx + getBoundsInParent().getMinX();
        double newY = f * dy + getBoundsInParent().getMinY();

        setPivot(newX, newY, scale);

    }

    public void resetZoom() {
        double scale = 1.0d;

        double x = getTranslateX();
        double y = getTranslateY();

        setPivot(x, y, scale);
    }

    public double getDeltaY() {
        return deltaY.get();
    }

    public void setDeltaY(double dY) {
        deltaY.set(dY);
    }
}
