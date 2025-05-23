package com.model.formatter.csv;

import com.model.formatter.BaseDetails;
import org.springframework.http.MediaType;

/**
 * Csv document details
 */
public interface CsvDetails extends BaseDetails {
    String EXTENSION = "csv";

    MediaType MEDIA_TYPE = MediaType.parseMediaType("text/csv");

    @Override
    default String getExtension() {
        return EXTENSION;
    }

    @Override
    default MediaType getContentMediaType() {
        return MEDIA_TYPE;
    }
}
