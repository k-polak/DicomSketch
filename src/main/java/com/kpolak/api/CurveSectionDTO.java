package com.kpolak.api;

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
