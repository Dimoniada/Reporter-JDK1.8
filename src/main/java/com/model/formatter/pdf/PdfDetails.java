package com.model.formatter.pdf;

import com.model.formatter.BaseDetails;
import org.springframework.http.MediaType;

/**
 * Pdf document details
 */
public interface PdfDetails extends BaseDetails {
    String EXTENSION = "pdf";

    MediaType MEDIA_TYPE = MediaType.parseMediaType("application/pdf");

    @Override
    default String getExtension() {
        return EXTENSION;
    }

    @Override
    default MediaType getContentMediaType() {
        return MEDIA_TYPE;
    }
}
