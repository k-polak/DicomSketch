package com.kpolak.external.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpolak.external.api.JsonDicomOutlineDTO;
import com.kpolak.model.dicom.Dicom;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class OutlineImporter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Optional<JsonDicomOutlineDTO> importOutline(Dicom dicom) {
        String path = OutlinePathRetriever.retrieveJsonOutlinePath(dicom);
        File outline = new File(path);
        try {
            if (outline.exists()) {
                return Optional.of(objectMapper.readValue(outline, JsonDicomOutlineDTO.class));
            }
        } catch (IOException e) {
            System.out.println("Couldn't find outline for dicom at: " + path);
        }
        return Optional.empty();
    }

}
