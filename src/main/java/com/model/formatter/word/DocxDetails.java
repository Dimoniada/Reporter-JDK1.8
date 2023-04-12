package com.model.formatter.word;

import com.model.formatter.BaseDetails;
import org.springframework.http.MediaType;

/**
 * Docx document details
 */
public interface DocxDetails extends BaseDetails {
    String EXTENSION = "docx";

    MediaType MEDIA_TYPE = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

    default String getExtension() {
        return EXTENSION;
    }

    default MediaType getContentMediaType() {
        return MEDIA_TYPE;
    }
}
