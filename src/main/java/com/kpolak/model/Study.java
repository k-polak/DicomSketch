package com.kpolak.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@EqualsAndHashCode(exclude = {"studyID","studyDate","studyTime"})
@Getter
public class Study {
    String studyInstanceUID;
    String studyID;
    String studyDate;
    String studyTime;
}
