package com.kpolak.view;

import com.kpolak.model.Dicom;
import com.kpolak.reader.DicomReader;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class LeftSideThumbnailContainer extends VBox {
    private final DicomReader dicomReader;
    private VBox thumbnailContainer;
    private ScrollPane scrollPane;
    private Main main;

    public LeftSideThumbnailContainer(DicomReader dicomReader, Main main) {
        this.dicomReader = dicomReader;
        this.main = main;
    }

    public void buildThumbnailContainer() {
        thumbnailContainer = new VBox();
        scrollPane = new ScrollPane();
        dicomReader.getRootNode().flatTree().stream()
                .map(this::createThumbnail)
                .forEach(imageView -> thumbnailContainer.getChildren().add(imageView));
        thumbnailContainer.setSpacing(10.0);
        scrollPane.setContent(thumbnailContainer);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        getChildren().clear();
        getChildren().add(scrollPane);
    }

    private ImageView createThumbnail(Dicom dicom) {
        DicomThumbnail dicomThumbnail = new DicomThumbnail(dicom);
        Dicom clickedDicom = dicomReader.getRootNode().findDicom(dicom.getPatient(), dicom.getStudy(), dicom.getSeries());
        dicomThumbnail.setOnMouseClicked(e -> main.displayDicom(clickedDicom));
        return dicomThumbnail;
    }
}
