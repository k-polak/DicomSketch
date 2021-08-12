package com.kpolak.view;

import com.kpolak.model.Dicom;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.awt.image.BufferedImage;

public class MainDisplay extends Pane {
    private final FrameTraverser frameTraverser;
    private final Dicom dicom;
    private ImageView imageView;
    private Pane imagePane;

    public MainDisplay(Dicom dicom) {
        this.dicom = dicom;
        frameTraverser = new FrameTraverser(this, dicom);
        init();
    }

    private void init() {
        setMaxSize(dicom.getWidth(), dicom.getHeight());
        setMinSize(dicom.getWidth(), dicom.getHeight());
        setBackground(Background.EMPTY);

        createImagePane();
        PanAndZoomPane panAndZoomPane = createPaneAndZoomPane();
        panAndZoomPane.getChildren().add(imagePane);
        ScrollPane scrollPane = createScrollPane(panAndZoomPane);

        getChildren().add(scrollPane);
        showDisplayUnit(frameTraverser.current());
    }

    private ScrollPane createScrollPane(PanAndZoomPane panAndZoomPane) {
        ScrollPane scrollPane = new ScrollPane();
        SceneGestures sceneGestures = new SceneGestures(panAndZoomPane);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

//        scrollPane.addEventFilter( MouseEvent.MOUSE_CLICKED, sceneGestures.getOnMouseClickedEventHandler());
        scrollPane.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        scrollPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        scrollPane.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
        scrollPane.setContent(panAndZoomPane);
        panAndZoomPane.toBack();
        return scrollPane;
    }

    private PanAndZoomPane createPaneAndZoomPane() {
        PanAndZoomPane panAndZoomPane = new PanAndZoomPane();
        DoubleProperty zoomProperty = new SimpleDoubleProperty(1.0d);
        DoubleProperty deltaY = new SimpleDoubleProperty(0.0d);
        zoomProperty.bind(panAndZoomPane.myScale);
        deltaY.bind(panAndZoomPane.deltaY);
        return panAndZoomPane;
    }

    private Pane createImagePane() {
        imagePane = new Pane();
        imagePane.setMaxSize(dicom.getWidth(), dicom.getHeight());
        imagePane.setMinSize(dicom.getWidth(), dicom.getHeight());
        imagePane.setBackground(Background.EMPTY);
        StackPane imageHolder = createImageHolder();

        imagePane.getChildren().add(imageHolder);

        Group group1 = new Group();
        group1.setManaged(false);

        imagePane.getChildren().add(group1);
        imagePane.setOnMouseClicked(this::getMouseEventHandlerForImagePane);

        return imagePane;
    }

    private void getMouseEventHandlerForImagePane(MouseEvent event) {
        frameTraverser.current().handleMouseEvent(event);
    }

    private StackPane createImageHolder() {
        imageView = new ImageView();
        StackPane imageHolder = new StackPane(imageView);
        imageHolder.setAlignment(Pos.CENTER);
        imageHolder.setBackground((new Background(
                new BackgroundFill(Color.rgb(50, 50, 50), CornerRadii.EMPTY, Insets.EMPTY))));
        imageHolder.setMaxSize(dicom.getWidth(), dicom.getHeight());
        imageHolder.setMinSize(dicom.getWidth(), dicom.getHeight());
        return imageHolder;
    }

    public Dicom getDicom() {
        return dicom;
    }

    void nextFrame() {
        DisplayUnit displayUnit = frameTraverser.next();
        showDisplayUnit(displayUnit);
    }

    void previousFrame() {
        DisplayUnit displayUnit = frameTraverser.previous();
        showDisplayUnit(displayUnit);
    }

    void showDisplayUnit(DisplayUnit displayUnit) {
        showFrame(displayUnit.frame);
        showOverlay(displayUnit.overlay);
    }

    void showFrame(BufferedImage frame) {
        imageView.setImage(SwingFXUtils.toFXImage(frame, null));
    }

    void showOverlay(Group group) {
        imagePane.getChildren().removeIf(child -> child instanceof Group);
        imagePane.getChildren().add(group);
    }
}