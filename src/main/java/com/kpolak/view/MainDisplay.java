package com.kpolak.view;

import com.kpolak.model.Dicom;
import com.kpolak.view.line.Curve;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
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
import javafx.util.Duration;


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
    Pane paneToReturn = new Pane();

    private ScrollPane scrollPane = new ScrollPane();

    private final DoubleProperty zoomProperty = new SimpleDoubleProperty(1.0d);
    private final DoubleProperty deltaY = new SimpleDoubleProperty(0.0d);



    public MainDisplay(Dicom dicom) {
        this.dicom = dicom;
    }

    Node getRoot() {
        paneToReturn = new Pane();
        paneToReturn.setBackground(Background.EMPTY);
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

        paneToReturn.getChildren().add(scrollPane);


        // create canvas
        PanAndZoomPane panAndZoomPane = new PanAndZoomPane();
        zoomProperty.bind(panAndZoomPane.myScale);
        deltaY.bind(panAndZoomPane.deltaY);
        panAndZoomPane.getChildren().add(pane);

        SceneGestures sceneGestures = new SceneGestures(panAndZoomPane);

//        imageHolder.minWidthProperty().bind(Bindings.createDoubleBinding(() ->
//                pane.getViewportBounds().getWidth(), pane.viewportBoundsProperty()));
//
//        scrollPane.setFitToWidth(true);
//        scrollPane.setFitToHeight(true);

        scrollPane.setContent(panAndZoomPane);
        panAndZoomPane.toBack();
//        scrollPane.addEventFilter( MouseEvent.MOUSE_CLICKED, sceneGestures.getOnMouseClickedEventHandler());
        scrollPane.addEventFilter( MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        scrollPane.addEventFilter( MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        scrollPane.addEventFilter( ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());



        startPane();

        return paneToReturn;
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
        paneToReturn.setMaxSize(buffer.getWidth(), buffer.getHeight());
        paneToReturn.setMinSize(buffer.getWidth(), buffer.getHeight());
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


    class PanAndZoomPane extends Pane {

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

        public void setScale( double scale) {
            myScale.set(scale);
        }

        public void setPivot( double x, double y, double scale) {
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

        public void fitWidth () {
            double scale = getParent().getLayoutBounds().getMaxX()/getLayoutBounds().getMaxX();
            double oldScale = getScale();

            double f = scale - oldScale;

            double dx = getTranslateX() - getBoundsInParent().getMinX() - getBoundsInParent().getWidth()/2;
            double dy = getTranslateY() - getBoundsInParent().getMinY() - getBoundsInParent().getHeight()/2;

            double newX = f*dx + getBoundsInParent().getMinX();
            double newY = f*dy + getBoundsInParent().getMinY();

            setPivot(newX, newY, scale);

        }

        public void resetZoom () {
            double scale = 1.0d;

            double x = getTranslateX();
            double y = getTranslateY();

            setPivot(x, y, scale);
        }

        public double getDeltaY() {
            return deltaY.get();
        }
        public void setDeltaY( double dY) {
            deltaY.set(dY);
        }
    }


    /**
     * Mouse drag context used for scene and nodes.
     */
    class DragContext {

        double mouseAnchorX;
        double mouseAnchorY;

        double translateAnchorX;
        double translateAnchorY;

    }

    /**
     * Listeners for making the scene's canvas draggable and zoomable
     */
    public class SceneGestures {

        private DragContext sceneDragContext = new DragContext();

        PanAndZoomPane panAndZoomPane;

        public SceneGestures( PanAndZoomPane canvas) {
            this.panAndZoomPane = canvas;
        }

        public EventHandler<MouseEvent> getOnMouseClickedEventHandler() {
            return onMouseClickedEventHandler;
        }

        public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
            return onMousePressedEventHandler;
        }

        public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
            return onMouseDraggedEventHandler;
        }

        public EventHandler<ScrollEvent> getOnScrollEventHandler() {
            return onScrollEventHandler;
        }

        private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {

                sceneDragContext.mouseAnchorX = event.getX();
                sceneDragContext.mouseAnchorY = event.getY();

                sceneDragContext.translateAnchorX = panAndZoomPane.getTranslateX();
                sceneDragContext.translateAnchorY = panAndZoomPane.getTranslateY();

            }

        };

        private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.MIDDLE)) {
                    panAndZoomPane.setTranslateX(sceneDragContext.translateAnchorX + event.getX() - sceneDragContext.mouseAnchorX);
                    panAndZoomPane.setTranslateY(sceneDragContext.translateAnchorY + event.getY() - sceneDragContext.mouseAnchorY);

                    event.consume();
                }
            }
        };

        /**
         * Mouse wheel handler: zoom to pivot point
         */
        private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {

            @Override
            public void handle(ScrollEvent event) {

                double delta = PanAndZoomPane.DEFAULT_DELTA;

                double scale = panAndZoomPane.getScale(); // currently we only use Y, same value is used for X
                double oldScale = scale;

                panAndZoomPane.setDeltaY(event.getDeltaY());
                if (panAndZoomPane.deltaY.get() < 0) {
                    scale /= delta;
                } else {
                    scale *= delta;
                }

                double f = (scale / oldScale)-1;
                double dx = (event.getX() - (panAndZoomPane.getBoundsInParent().getWidth()/2 + panAndZoomPane.getBoundsInParent().getMinX()));
                double dy = (event.getY() - (panAndZoomPane.getBoundsInParent().getHeight()/2 + panAndZoomPane.getBoundsInParent().getMinY()));

                panAndZoomPane.setPivot(f*dx, f*dy, scale);

                event.consume();

            }
        };

        /**
         * Mouse click handler
         */
        private EventHandler<MouseEvent> onMouseClickedEventHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    if (event.getClickCount() == 2) {
                        panAndZoomPane.resetZoom();
                    }
                }
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    if (event.getClickCount() == 2) {
                        panAndZoomPane.fitWidth();
                    }
                }
            }
        };
    }
}