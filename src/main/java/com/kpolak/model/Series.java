package com.kpolak.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;

@Builder
@EqualsAndHashCode(exclude = {"seriesNumber","seriesDate","seriesTime","seriesDescription"})
public class Series {
    String seriesInstanceUID;
    String seriesNumber;
    String seriesDate;
    String seriesTime;
    String seriesDescription;
}
