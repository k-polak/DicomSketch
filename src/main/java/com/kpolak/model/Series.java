package com.kpolak.model;

import lombok.Builder;

@Builder
public class Series {
    String seriesInstanceUID;
    String seriesNumber;
    String seriesDate;
    String seriesTime;
    String seriesDescription;
}
