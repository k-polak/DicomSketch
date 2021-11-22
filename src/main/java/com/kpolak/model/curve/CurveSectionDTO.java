package com.kpolak.model.curve;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CurveSectionDTO {
    PointDTO start;
    PointDTO end;
    PointDTO control;
}
