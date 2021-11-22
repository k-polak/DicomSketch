package com.kpolak.model.dicom;

import com.kpolak.external.api.JsonDicomOutlineDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Optional;

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
    String path;
    @Setter
    @Builder.Default
    Optional<JsonDicomOutlineDTO> dicomOutlineDTO = Optional.empty();
}
