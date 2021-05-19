package com.kpolak.model;

import lombok.Builder;

@Builder
public class Study {
    String studyID;
    String studyInstanceUID;
    String studyDate;
    String studyTime;
}
