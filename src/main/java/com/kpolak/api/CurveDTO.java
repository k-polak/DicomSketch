package com.kpolak.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class CurveDTO {
    List<CurveSectionDTO> curveSectionDTOS;

    public CurveDTO() {
        curveSectionDTOS = new ArrayList<>();
    }
}
