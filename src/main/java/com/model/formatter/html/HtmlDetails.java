package com.model.formatter.html;

import com.model.formatter.BaseDetails;
import org.springframework.http.MediaType;

/**
 * Html document details
 */
public interface HtmlDetails extends BaseDetails {
    String EXTENSION = "html";

    MediaType MEDIA_TYPE = MediaType.parseMediaType("text/html");

    @Override
    default String getExtension() {
        return EXTENSION;
    }

    @Override
    default MediaType getContentMediaType() {
        return MEDIA_TYPE;
    }
}
