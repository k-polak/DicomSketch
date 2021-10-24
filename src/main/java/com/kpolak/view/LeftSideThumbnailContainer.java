package com.kpolak.view;

import com.kpolak.model.Dicom;
import com.kpolak.reader.DicomReader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class LeftSideThumbnailContainer extends VBox {
    private final DicomReader dicomReader;
    private final ViewManager viewManager;

    public LeftSideThumbnailContainer(DicomReader dicomReader, ViewManager viewManager) {
        this.dicomReader = dicomReader;
        this.viewManager = viewManager;
    }

    public void buildThumbnailContainer() {
        setFocusTraversable(false);
        setStyle(StyleConstants.LEFT_SIDE_CONTAINER_STYLE);

        VBox thumbnailContainer = new VBox();
        thumbnailContainer.setFocusTraversable(false);
        thumbnailContainer.setStyle(StyleConstants.BACKGROUND_COLOR);

        ScrollPane scrollPane = new ScrollPane();

        dicomReader.getRootNode().flatTree().stream()
                .map(this::createThumbnail)
                .forEach(imageView -> thumbnailContainer.getChildren().add(imageView));

        thumbnailContainer.setSpacing(10.0);
        scrollPane.setContent(thumbnailContainer);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFocusTraversable(false);
        scrollPane.getStylesheets().add(getClass().getResource("/styles/scrollPaneFocus.css").toExternalForm());
        getChildren().clear();
        getChildren().add(scrollPane);
    }

    private DicomThumbnail createThumbnail(Dicom dicom) {
        DicomThumbnail dicomThumbnail = new DicomThumbnail(dicom);
        dicomThumbnail.setOnMouseClicked(e -> viewManager.thumbnailSelected(dicomThumbnail));
        return dicomThumbnail;
    }
}
