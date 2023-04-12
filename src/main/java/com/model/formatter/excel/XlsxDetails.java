package com.model.formatter.excel;

import com.model.formatter.BaseDetails;
import org.springframework.http.MediaType;

/**
 * Xlsx document details
 */
public interface XlsxDetails extends BaseDetails {
    String EXTENSION = "xlsx";

    MediaType MEDIA_TYPE = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    default String getExtension() {
        return EXTENSION;
    }

    default MediaType getContentMediaType() {
        return MEDIA_TYPE;
    }
}
