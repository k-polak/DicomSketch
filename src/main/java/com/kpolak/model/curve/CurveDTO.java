package com.kpolak.model.curve;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class CurveDTO {
    List<CurveSectionDTO> curveSectionDTOS;
    Optional<String> id;
}
