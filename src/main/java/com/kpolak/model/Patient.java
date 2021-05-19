package com.kpolak.model;

import lombok.Builder;

@Builder
public class Patient {
    String patientName;
    String patientID;
    String patientBirthDate;
    String patientSex;
    String patientAge;
    String patientComments;
}
