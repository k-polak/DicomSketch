package com.kpolak.view;

import com.kpolak.model.Dicom;
import com.kpolak.view.line.Curve;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
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
    private FrameTraverser frameTraverser;
    private Dicom dicom;
    private ImageView imageView;
    private Pane imagePane;
    private int currentFrame;

    public MainDisplay(Dicom dicom) {
        this.dicom = dicom;
        frameTraverser = new FrameTraverser(this, dicom);
        init();
        System.out.println();
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
        nextFrame();
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
//        return event -> {
//            double x = event.getX(), y = event.getY();
//            System.out.println("Clicked x: " + x + "  y: " + y);
//
//            if (event.getButton() == MouseButton.SECONDARY) {
//                if (curves.isEmpty()) {
//                    createNewCurve(x, y, group);
//                } else {
//                    if (focusedCurve != null) {
//                        if (focusedCurve.isClosed) {
//                            curves.forEach(Curve::removeHighlight);
//                            createNewCurve(x, y, group);
//                        } else {
//                            focusedCurve.handleClick(x, y);
//                        }
//                    } else {
//                        createNewCurve(x, y, group);
//                    }
//                }
//            } else if (event.getButton() == MouseButton.PRIMARY) {
//                //
//            }
//        };
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
//        if (currentFrame < dicom.getFrames().size()) {
//            currentFrame++;
//        }
//        showFrame();
    }

    void previousFrame() {
        DisplayUnit displayUnit = frameTraverser.previous();
        showDisplayUnit(displayUnit);
//        if (currentFrame > 1) {
//            currentFrame--;
//        }
//        showFrame();
    }

    private void showFrame() {
        BufferedImage buffer = dicom.getFrames().get(currentFrame);
        imageView.setImage(SwingFXUtils.toFXImage(buffer, null));
//        =============REMEMBER==========
//        imageHolder.setMaxSize(buffer.getWidth(), buffer.getHeight());
//        imageHolder.setMinSize(buffer.getWidth(), buffer.getHeight());
//        imagePane.setMaxSize(buffer.getWidth(), buffer.getHeight());
//        imagePane.setMinSize(buffer.getWidth(), buffer.getHeight());

//        setMaxSize(buffer.getWidth(), buffer.getHeight());
//        setMinSize(buffer.getWidth(), buffer.getHeight());
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

    private void createNewCurve(double x, double y, Group group) {
        Curve newCurve = new Curve(group, this, dicom.getWidth(), dicom.getHeight());
        newCurve.handleClick(x, y);
        focusedCurve = newCurve;
        curves.add(newCurve);
    }

    public void handleCurveClicked(Curve curve) {
        frameTraverser.current().handleCurveClicked(curve);
        if (focusedCurve != null && !focusedCurve.equals(curve)) {
            focusedCurve.removeHighlight();
            curve.highlight();
            focusedCurve = curve;
        }
    }
}