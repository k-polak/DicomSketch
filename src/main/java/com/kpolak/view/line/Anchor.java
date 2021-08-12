package com.kpolak.view.line;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

class Anchor extends Circle {
    Curve curve;
    AnchorType anchorType;
    double maxWidth;
    double maxHeight;

    Anchor(Color color, DoubleProperty x, DoubleProperty y, double radius, AnchorType anchorType, double maxWidth, double maxHeight) {
        super(x.get(), y.get(), radius);
        this.anchorType = anchorType;
        this.maxHeight = maxHeight;
        this.maxWidth = maxWidth;
        setFill(color.deriveColor(1, 1, 1, 0.5));
        setStroke(color);
        setStrokeWidth(2);
        setStrokeType(StrokeType.OUTSIDE);
        x.bind(centerXProperty());
        y.bind(centerYProperty());
        enableDrag();
    }


    Anchor(Color color, double x, double y, double radius, Curve curve, AnchorType anchorType, double maxWidth, double maxHeight) {
        super(x, y, radius);
        this.curve = curve;
        this.anchorType = anchorType;
        this.maxHeight = maxHeight;
        this.maxWidth = maxWidth;
        setFill(color.deriveColor(1, 1, 1, 0.5));
        setStroke(color);
        setStrokeWidth(2);
        setStrokeType(StrokeType.OUTSIDE);
        enableDrag();
    }

    public void addBinding(DoubleProperty x, DoubleProperty y) {
        x.bind(centerXProperty());
        y.bind(centerYProperty());
    }

    void prepareRemoval() {
        getScene().setCursor(Cursor.DEFAULT);
    }

    // make a node movable by dragging it around with the mouse.
    private void enableDrag() {
        final Delta dragDelta = new Delta();
        setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = getCenterX() - mouseEvent.getX();
            dragDelta.y = getCenterY() - mouseEvent.getY();
            getScene().setCursor(Cursor.MOVE);
        });
        setOnMouseReleased(mouseEvent -> getScene().setCursor(Cursor.HAND));
        setOnMouseDragged(mouseEvent -> {
            double newX = mouseEvent.getX() + dragDelta.x;
            double newY = mouseEvent.getY() + dragDelta.y;
            System.out.println("Drag: "+ (newX > 0 && newX < maxWidth && newY > 0 && newY < maxHeight));
            //if (newX > 0 && newX < getScene().getWidth() && newY > 0 && newY < getScene().getHeight()) {
            if (newX > 0 && newX < maxWidth && newY > 0 && newY < maxHeight) {
                setCenterX(newX);
                setCenterY(newY);
                if (anchorType.equals(AnchorType.CURVE_POINT)) {
                    curve.handleMoveTo(newX, newY, this);
                }
            } else {
                System.out.println();
            }
        });
        setOnMouseEntered(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                getScene().setCursor(Cursor.HAND);
            }
        });
        setOnMouseExited(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                getScene().setCursor(Cursor.DEFAULT);
            }
        });

        setOnMouseClicked(event -> event.consume());
    }

    // records relative x and y co-ordinates.
    private class Delta {
        double x, y;
    }
}