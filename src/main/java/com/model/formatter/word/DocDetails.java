package com.model.formatter.word;

import com.model.formatter.BaseDetails;
import org.springframework.http.MediaType;

/**
 * Doc document details
 */
public interface DocDetails extends BaseDetails {
    String EXTENSION = "doc";

    MediaType MEDIA_TYPE = MediaType.parseMediaType("application/msword");

    default String getExtension() {
        return EXTENSION;
    }

    default MediaType getContentMediaType() {
        return MEDIA_TYPE;
    }
}
