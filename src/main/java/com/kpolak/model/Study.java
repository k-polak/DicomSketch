package com.kpolak.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;

@Builder
@EqualsAndHashCode(exclude = {"studyID","studyDate","studyTime"})
public class Study {
    String studyInstanceUID;
    String studyID;
    String studyDate;
    String studyTime;
}
