package com.kpolak.view;

import com.kpolak.model.Dicom;
import com.kpolak.view.line.Curve;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MainDisplay {
    private List<Curve> curves = new LinkedList<>();
    private Curve focusedCurve = null;
    private Dicom dicom;
    private ImageView imageView;
    private int currentFrame;
    StackPane imageHolder;
    Pane pane;


    public MainDisplay(Dicom dicom) {
        this.dicom = dicom;
    }

    Node getRoot() {
        pane = new Pane();
        pane.setBackground(Background.EMPTY);
        imageView = new ImageView();
        imageHolder = new StackPane(imageView);
        imageHolder.setAlignment(Pos.CENTER);
        imageHolder.setBackground((new Background(
                new BackgroundFill(Color.rgb(50, 50, 50), CornerRadii.EMPTY, Insets.EMPTY))));
        pane.getChildren().add(imageHolder);
        startPane();
        return pane;
    }

    void nextFrame() {
        if (currentFrame < dicom.getFrames().size()) {
            currentFrame++;
        }
        showFrame();
    }

    void previousFrame() {
        if (currentFrame > 1) {
            currentFrame--;
        }
        showFrame();
    }

    private void showFrame() {
        BufferedImage buffer = dicom.getFrames().get(currentFrame);
        imageView.setImage(SwingFXUtils.toFXImage(buffer, null));
        imageHolder.setMaxSize(buffer.getWidth(), buffer.getHeight());
        imageHolder.setMinSize(buffer.getWidth(), buffer.getHeight());
        pane.setMaxSize(buffer.getWidth(), buffer.getHeight());
        pane.setMinSize(buffer.getWidth(), buffer.getHeight());
    }


    public void startPane() {
        Group group = new Group();
        group.setManaged(false);

        pane.getChildren().add(group);
        pane.setOnMouseClicked(event -> {
            double x = event.getX(), y = event.getY();
            System.out.println("Clicked x: " + x + "  y: " + y);

            if (event.getButton() == MouseButton.SECONDARY) {
                if (curves.isEmpty()) {
                    createNewCurve(x, y, group);
                } else {
                    if (focusedCurve != null) {
                        if (focusedCurve.isClosed) {
                            curves.forEach(Curve::removeHighlight);
                            createNewCurve(x, y, group);
                        } else {
                            focusedCurve.handleClick(x, y);
                        }
                    } else {
                        createNewCurve(x, y, group);
                    }
                }
            } else if (event.getButton() == MouseButton.PRIMARY) {
                //
            }
        });
    }

    private void createNewCurve(double x, double y, Group group) {
        Curve newCurve = new Curve(group, this, dicom.getWidth(), dicom.getHeight());
        newCurve.handleClick(x, y);
        focusedCurve = newCurve;
        curves.add(newCurve);
    }

    public void handleCurveClicked(Curve curve) {
        if (focusedCurve != null && !focusedCurve.equals(curve)) {
            focusedCurve.removeHighlight();
            curve.highlight();
            focusedCurve = curve;
        }
    }
}
