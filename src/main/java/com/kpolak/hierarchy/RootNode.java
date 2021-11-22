package com.kpolak.hierarchy;

import com.kpolak.model.dicom.Dicom;
import com.kpolak.model.dicom.Patient;
import com.kpolak.model.dicom.Series;
import com.kpolak.model.dicom.Study;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RootNode {
    private final List<PatientNode> patients;

    public RootNode() {
        patients = new ArrayList<>();
    }

    public List<PatientNode> getPatients() {
        return patients;
    }

    public void add(Dicom dicom) {
        Optional<PatientNode> patient = patients.stream()
                .filter(patientNode -> patientNode.getPatient().equals(dicom.getPatient()))
                .findFirst();

        if (patient.isPresent()) {
            patient.get().add(dicom);
        } else {
            patients.add(new PatientNode(dicom));
        }
    }

    public boolean containsDicom(Dicom dicom) {
        return flatTree().contains(dicom);
    }

    public List<Dicom> flatTree() {
        return patients.stream()
                .flatMap(patientNode -> patientNode.getStudies().stream())
                .flatMap(studyNode -> studyNode.getSeriesList().stream())
                .map(seriesNode -> seriesNode.images)
                .collect(Collectors.toList());
    }

    public Dicom findDicom(Patient patient, Study study, Series series) {
        return patients.stream()
                .filter(patientNode -> patientNode.getPatient().equals(patient))
                .flatMap(patientNode -> patientNode.getStudies().stream())
                .filter(studyNode -> studyNode.getStudy().equals(study))
                .flatMap(studyNode -> studyNode.getSeriesList().stream())
                .filter(seriesNode -> seriesNode.getSeries().equals(series))
                .map(SeriesNode::getImages)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Couldn't get dicom by series"));
    }
}
