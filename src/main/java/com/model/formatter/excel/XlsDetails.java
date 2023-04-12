package com.model.formatter.excel;

import com.model.formatter.BaseDetails;
import org.springframework.http.MediaType;

/**
 * Xls document details
 */
public interface XlsDetails extends BaseDetails {
    String EXTENSION = "xls";

    MediaType MEDIA_TYPE = MediaType.parseMediaType("application/vnd.ms-excel");

    default String getExtension() {
        return EXTENSION;
    }

    default MediaType getContentMediaType() {
        return MEDIA_TYPE;
    }
}
