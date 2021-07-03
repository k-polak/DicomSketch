package com.kpolak.hierarchy;

import com.kpolak.model.Dicom;
import com.kpolak.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientNode {
    Patient patient;
    private List<StudyNode> studies;

    public List<StudyNode> getStudies() {
        return studies;
    }

    public PatientNode(Dicom dicom) {
        patient = dicom.getPatient();
        studies = new ArrayList<>();
        add(dicom);
    }

    public Patient getPatient() {
        return patient;
    }

    public void add(Dicom dicom) {
        Optional<StudyNode> studyNode = studies.stream()
                .filter(study -> study.getStudy().equals(dicom.getStudy()))
                .findFirst();
        if (studyNode.isPresent()) {
            studyNode.get().add(dicom);
        } else {
            studies.add(new StudyNode(dicom));
        }
    }
}
