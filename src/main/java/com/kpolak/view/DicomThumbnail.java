package com.kpolak.view;

import com.kpolak.model.Dicom;
import com.kpolak.model.Patient;
import com.kpolak.model.Series;
import com.kpolak.model.Study;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import java.util.Iterator;

public class DicomThumbnail extends ImageView {
    private Patient patient;
    private Study study;
    private Series series;


    public DicomThumbnail(Dicom dicom) {
        patient = dicom.getPatient();
        study =  dicom.getStudy();
        series = dicom.getSeries();

        Iterator<BufferedImage> iterator = dicom.getFrames().values().iterator();
        BufferedImage bufferedImage;
        if (iterator.hasNext()) {
            bufferedImage = iterator.next();
        } else {
            throw new RuntimeException("Couldn't create thumbnail -  no frame present");
        }
        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
        setImage(image);

        setPreserveRatio(true);
        setFitHeight(130);
        setFitHeight(130);
    }

    public Patient getPatient() {
        return patient;
    }

    public Study getStudy() {
        return study;
    }

    public Series getSeries() {
        return series;
    }
}
