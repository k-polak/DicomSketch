package com.kpolak.model.dicom;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@EqualsAndHashCode(exclude = {"seriesNumber","seriesDate","seriesTime","seriesDescription"})
@Getter
public class Series {
    String seriesInstanceUID;
    String seriesNumber;
    String seriesDate;
    String seriesTime;
    String seriesDescription;
}
