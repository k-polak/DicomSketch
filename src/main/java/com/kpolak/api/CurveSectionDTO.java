package com.kpolak.api;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurveSectionDTO {
    PointDTO start;
    PointDTO end;
    PointDTO control;
}
