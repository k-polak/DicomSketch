package com.kpolak.view;

import com.kpolak.model.Dicom;
import com.kpolak.view.line.Curve;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MainDisplay extends Pane {
    private List<Curve> curves = new LinkedList<>();
    private Curve focusedCurve = null;
    private Dicom dicom;
    private ImageView imageView;
    private int currentFrame;
    StackPane imageHolder;
    Pane pane;

    private ScrollPane scrollPane = new ScrollPane();

    private final DoubleProperty zoomProperty = new SimpleDoubleProperty(1.0d);
    private final DoubleProperty deltaY = new SimpleDoubleProperty(0.0d);


    public MainDisplay(Dicom dicom) {
        this.dicom = dicom;
        setBackground(Background.EMPTY);
        pane = new Pane();
        pane.setBackground(Background.EMPTY);
        imageView = new ImageView();
        imageHolder = new StackPane(imageView);
        imageHolder.setAlignment(Pos.CENTER);
        imageHolder.setBackground((new Background(
                new BackgroundFill(Color.rgb(50, 50, 50), CornerRadii.EMPTY, Insets.EMPTY))));

        pane.getChildren().add(imageHolder);

        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        getChildren().add(scrollPane);

        // create canvas
        PanAndZoomPane panAndZoomPane = new PanAndZoomPane();
        zoomProperty.bind(panAndZoomPane.myScale);
        deltaY.bind(panAndZoomPane.deltaY);
        panAndZoomPane.getChildren().add(pane);

        SceneGestures sceneGestures = new SceneGestures(panAndZoomPane);

        scrollPane.setContent(panAndZoomPane);
        panAndZoomPane.toBack();
//        scrollPane.addEventFilter( MouseEvent.MOUSE_CLICKED, sceneGestures.getOnMouseClickedEventHandler());
        scrollPane.addEventFilter( MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        scrollPane.addEventFilter( MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        scrollPane.addEventFilter( ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

        startPane();
    }

    public Dicom getDicom() {
        return dicom;
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

        setMaxSize(buffer.getWidth(), buffer.getHeight());
        setMinSize(buffer.getWidth(), buffer.getHeight());
    }


    public void startPane() {
        Group group1 = new Group();
        group1.setManaged(false);

        pane.getChildren().add(group1);
        pane.setOnMouseClicked(event -> {
            double x = event.getX(), y = event.getY();
            System.out.println("Clicked x: " + x + "  y: " + y);

            if (event.getButton() == MouseButton.SECONDARY) {
                if (curves.isEmpty()) {
                    createNewCurve(x, y, group1);
                } else {
                    if (focusedCurve != null) {
                        if (focusedCurve.isClosed) {
                            curves.forEach(Curve::removeHighlight);
                            createNewCurve(x, y, group1);
                        } else {
                            focusedCurve.handleClick(x, y);
                        }
                    } else {
                        createNewCurve(x, y, group1);
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