package com.kpolak.view.line;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.QuadCurve;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class App4 extends Application {
    private List<Curve> curves = new LinkedList<>();
    private Curve focusedCurve = null;

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Pane stackPane = new Pane(group);
        Scene scene = new Scene(stackPane, 500, 500, Color.BISQUE);
        scene.setOnMouseClicked(event -> {
            double x = event.getX(), y = event.getY();


            if (event.getButton() == MouseButton.SECONDARY) {
                if(curves.isEmpty()) {
                    //createNewCurve(x, y, group);
                } else {
                    if(focusedCurve != null) {
                        if(focusedCurve.isClosed) {
                            curves.forEach(Curve::removeHighlight);
                           // createNewCurve(x, y, group);
                        } else {
                            focusedCurve.handleClick(x,y);
                        }
                    } else {
                     //   createNewCurve(x, y, group);
                    }
                }
            } else if(event.getButton() == MouseButton.PRIMARY) {
//                if(focusedCurve != null) {
//                    focusedCurve.removeHighlight();
//                }
//
//                focusedCurve = null;
            }
        });
        stage.setScene(scene);
        stage.show();
    }

//    private void createNewCurve(double x, double y, Group group) {
//        Curve newCurve = new Curve(group, this);
//        newCurve.handleClick(x, y);
//        focusedCurve = newCurve;
//        curves.add(newCurve);
    //}

    void handleCurveClicked(Curve curve) {
        if (focusedCurve != null && !focusedCurve.equals(curve)) {
            focusedCurve.removeHighlight();
            curve.highlight();
            focusedCurve = curve;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
