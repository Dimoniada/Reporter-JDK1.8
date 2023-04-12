package com.model.formatter;

import org.springframework.http.MediaType;

/**
 * Document details interface
 */
public interface BaseDetails {
    /**
     * Returns the file extension for the entry
     *
     * @return extension
     */
    String getExtension();

    /**
     * Returns the mimetype of the received file/resource
     *
     * @return MediaType of file/resource
     */
    MediaType getContentMediaType();
}
