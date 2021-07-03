package com.kpolak.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;

@Builder
@EqualsAndHashCode(exclude = {"patientBirthDate","patientSex","patientAge","patientComments"})
public class Patient {
    String patientName;
    String patientID;
    String patientBirthDate;
    String patientSex;
    String patientAge;
    String patientComments;
}
