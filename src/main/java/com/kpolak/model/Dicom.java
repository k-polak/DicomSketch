package com.kpolak.model;

import lombok.Builder;
import lombok.Getter;

import java.awt.image.BufferedImage;
import java.util.Map;

@Builder
@Getter
public class Dicom {
    Patient patient;
    Series series;
    Study study;
    Integer instanceNumber;
    Map<Integer, BufferedImage> frames;
    int width;
    int height;
}
