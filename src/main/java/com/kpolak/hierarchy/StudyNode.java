package com.kpolak.hierarchy;

import com.kpolak.model.dicom.Dicom;
import com.kpolak.model.dicom.Study;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudyNode {
    Study study;
    private List<SeriesNode> seriesList;

    public List<SeriesNode> getSeriesList() {
        return seriesList;
    }

    public StudyNode(Dicom dicom) {
        seriesList = new ArrayList<>();
        study = dicom.getStudy();
        add(dicom);
    }

    public Study getStudy() {
        return study;
    }

    public void add(Dicom dicom) {
        Optional<SeriesNode> seriesNode = seriesList.stream()
                .filter(series -> series.getSeries().equals(dicom.getSeries()))
                .findFirst();
        if (seriesNode.isPresent()) {
            seriesNode.get().add(dicom);
        } else {
            seriesList.add(new SeriesNode(dicom));
        }
    }
}
