package com.kpolak.view;

import com.kpolak.external.util.OutlineExporter;
import com.kpolak.model.dicom.Dicom;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.awt.image.BufferedImage;

public class MainDisplay extends Pane {
    public static final double MAX_WIDTH = 880;
    public static final double MAX_HEIGHT = 880;
    private final FrameTraverser frameTraverser;
    private final OutlineExporter outlineExporter;
    private final Dicom dicom;
    private ImageView imageView;
    private Pane imagePane;
    private Text textNumberLabel;
    private PanAndZoomPane panAndZoomPane;
    private PopupWindow popupWindow;

    public MainDisplay(Dicom dicom, PopupWindow popupWindow) {
        this.dicom = dicom;
        frameTraverser = new FrameTraverser(this, dicom);
        outlineExporter = new OutlineExporter(getXScale(), getYScale(), frameTraverser);
        this.popupWindow = popupWindow;
        init();
    }

    public FrameTraverser getFrameTraverser() {
        return frameTraverser;
    }

    public double getXScale() {
        return MAX_WIDTH / dicom.getWidth();
    }

    public double getYScale() {
        return MAX_HEIGHT / dicom.getHeight();
    }


    private void init() {
        setMaxSize(MAX_WIDTH, MAX_HEIGHT);
        setMinSize(dicom.getWidth(), dicom.getHeight());
        setBackground(Background.EMPTY);
        setFocusTraversable(false);

        createImagePane();
        panAndZoomPane = createPaneAndZoomPane();
        panAndZoomPane.maxWidthProperty().bind(widthProperty());
        panAndZoomPane.maxHeightProperty().bind(heightProperty());

        panAndZoomPane.getChildren().add(imagePane);
        ScrollPane scrollPane = createScrollPane(panAndZoomPane);

        textNumberLabel = new Text();
        textNumberLabel.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
        textNumberLabel.setFill(Color.WHITE);
        VBox vbox = new VBox(scrollPane, textNumberLabel);
        vbox.setFocusTraversable(false);
        vbox.setAlignment(Pos.CENTER);
        getChildren().add(vbox);

        showDisplayUnit(frameTraverser.current());
    }

    private ScrollPane createScrollPane(PanAndZoomPane panAndZoomPane) {
        ScrollPane scrollPane = new ScrollPane();
        SceneGestures sceneGestures = new SceneGestures(panAndZoomPane);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFocusTraversable(false);

        scrollPane.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        scrollPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        scrollPane.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
        scrollPane.setContent(panAndZoomPane);
        scrollPane.getStylesheets().add(getClass().getResource("/styles/scrollPaneFocus.css").toExternalForm());
        panAndZoomPane.toBack();
        return scrollPane;
    }

    public void fitWindow() {
        panAndZoomPane.fitWidth();
    }

    private PanAndZoomPane createPaneAndZoomPane() {
        PanAndZoomPane panAndZoomPane = new PanAndZoomPane();
        DoubleProperty zoomProperty = new SimpleDoubleProperty(1.0d);
        DoubleProperty deltaY = new SimpleDoubleProperty(0.0d);
        zoomProperty.bind(panAndZoomPane.myScale);
        deltaY.bind(panAndZoomPane.deltaY);
        panAndZoomPane.setFocusTraversable(false);
        return panAndZoomPane;
    }

    private Pane createImagePane() {
        imagePane = new Pane();
        imagePane.setBackground(Background.EMPTY);
        imagePane.setFocusTraversable(false);

        StackPane imageHolder = createImageHolder();

        imagePane.getChildren().add(imageHolder);

        Group group1 = new Group();
        group1.setManaged(false);

        imagePane.getChildren().add(group1);

        return imagePane;
    }

    private void getMouseEventHandlerForImagePane(MouseEvent event) {
        frameTraverser.current().handleMouseEvent(event);
    }

    private StackPane createImageHolder() {
        imageView = new ImageView();

        imageView.fitWidthProperty().bind(widthProperty());
        imageView.fitHeightProperty().bind(heightProperty());

        imageView.setPreserveRatio(true);

        imageView.setOnMouseClicked(this::getMouseEventHandlerForImagePane);
        StackPane imageHolder = new StackPane(imageView);
        imageHolder.setAlignment(Pos.CENTER);
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
        updateFrameNumberLabel();
    }

    void showDisplayUnit(DisplayUnit displayUnit) {
        showFrame(displayUnit.frame);
        showOverlay(displayUnit.overlay);
        updateFrameNumberLabel();
    }

    void showFrame(BufferedImage frame) {
        imageView.setImage(SwingFXUtils.toFXImage(frame, null));
    }

    void showOverlay(Group group) {
        imagePane.getChildren().removeIf(child -> child instanceof Group);
        imagePane.getChildren().add(group);
    }

    private void updateFrameNumberLabel() {
        textNumberLabel.setText(getFrameNumberLabel());
    }

    private String getFrameNumberLabel() {
        return "Frame number: " + frameTraverser.getCurrentPositionInLoadedFrames() + "/" + frameTraverser.getNumberOfLoadedFrames();
    }

    public void loadAnotherFrame(Dicom dicom) {
        frameTraverser.loadAnotherFrame(dicom);
        updateFrameNumberLabel();
    }

    public void exportCurves() {
        try {
            outlineExporter.exportOutline(dicom);
            popupWindow.showPopup("Export finished successfully");
        } catch (ExportException e) {
            popupWindow.showPopup(e.getMessage());
        }
    }

    public void handleDelete() {
        frameTraverser.current().handleDelete();
    }
}