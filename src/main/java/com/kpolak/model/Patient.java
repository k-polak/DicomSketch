package com.kpolak.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@EqualsAndHashCode(exclude = {"patientBirthDate","patientSex","patientAge","patientComments"})
@Getter
public class Patient {
    String patientName;
    String patientID;
    String patientBirthDate;
    String patientSex;
    String patientAge;
    String patientComments;
}
