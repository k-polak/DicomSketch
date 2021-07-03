package com.kpolak.hierarchy;

import com.kpolak.model.Dicom;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RootNode {
    private List<PatientNode> patients;

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

    public List<Dicom> flatTree() {
        return patients.stream()
                .flatMap(patientNode -> patientNode.getStudies().stream())
                .flatMap(studyNode -> studyNode.getSeriesList().stream())
                .map(seriesNode -> seriesNode.images)
                .collect(Collectors.toList());
    }
}
