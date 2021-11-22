package com.kpolak.hierarchy;

import com.kpolak.external.util.OutlineImporter;
import com.kpolak.model.Dicom;
import com.kpolak.model.Series;

public class SeriesNode {
    Series series;
    Dicom images;

    public SeriesNode(Dicom dicom) {
        series = dicom.getSeries();
        add(dicom);
    }

    public Series getSeries() {
        return series;
    }

    public Dicom getImages() {
        return images;
    }

    public void add(Dicom dicom) {
        if (images == null) {
            images = dicom;
            images.setDicomOutlineDTO(OutlineImporter.importOutline(dicom));
        } else {
            Integer instanceNumber = dicom.getInstanceNumber();

            if (dicom.getFrames().size() != 1) {
                throw new RuntimeException("Merging multiframe dicom files is not supported");
            }

            if (images.getFrames().get(instanceNumber) != null) {
                throw new RuntimeException("Duplicated instance number");
            }

            images.getFrames().put(instanceNumber, dicom.getFrames().get(instanceNumber));
        }
    }
}
