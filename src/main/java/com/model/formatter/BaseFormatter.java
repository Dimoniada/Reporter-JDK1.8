package com.model.formatter;

import com.model.domain.Document;
import com.model.domain.style.StyleService;

/**
 * Contains common formatter methods
 */
public interface BaseFormatter extends BaseDetails {
    /**
     * Starts generating a file/resource from a Document
     *
     * @param document input document
     * @return received file/resource holder
     * @throws Throwable if file/resource generation failed
     */
    DocumentHolder handle(Document document) throws Throwable;

    /**
     * Returns the style service for the document
     *
     * @return style service
     */
    StyleService getStyleService();
}
