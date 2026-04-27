package com.tabariyya.export;

import java.util.Arrays;
import java.util.Optional;

public enum ExportType {
    CSV("text/csv"),
    EXCEL("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final String mediaType;
    ExportType(String mediaType) {
        this.mediaType = mediaType;
    }
    public String getMediaType() {
        return mediaType;
    }

    public static Optional<ExportType> match(String header) {
        return Arrays.stream(values())
                .filter(e -> header != null && header.contains(e.getMediaType()))
                .findFirst();
    }
}
