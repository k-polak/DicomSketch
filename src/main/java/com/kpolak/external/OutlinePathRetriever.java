package com.kpolak.external;

import com.kpolak.model.Dicom;

import java.io.File;
import java.nio.file.Paths;

public class OutlinePathRetriever {
    static String retrieveJsonOutlinePath(Dicom dicom) {
        return Paths.get(dicom.getPath()).toAbsolutePath().getParent() + File.separator + retrieveJsonOutlineFilename(dicom);
    }

    static String retrieveJsonOutlineFilename(Dicom dicom) {
        return dicom.getPatient().getPatientID() + "_" + dicom.getStudy().getStudyID() + "_"
                + dicom.getSeries().getSeriesInstanceUID() + ".json";
    }
}
