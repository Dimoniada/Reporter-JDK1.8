package com.model.domain.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base data abstract class, contains base data access methods
 */
public abstract class DataItem extends DocumentItem {

    private static final Logger log = LoggerFactory.getLogger(DataItem.class);
    private final String superClassName = getClass().getGenericSuperclass().getTypeName();

    /**
     * Returns string if item is a text
     */
    public abstract String getText();

    public <T extends DataItem> T setText(String text) throws Throwable {
        log.debug("No implementation for setText() in {}", superClassName);
        throw new Throwable("No implementation for setText() in " + superClassName);
    }

    /**
     * Returns byte array if item is a picture or other object
     */
    public abstract byte[] getData();

    public <T extends DataItem> T setData(byte[] data) throws Throwable {
        log.debug("No implementation for setData() in {}", superClassName);
        throw new Throwable("No implementation for setData() in " + superClassName);
    }

    public abstract boolean isDataInheritedFrom(Class<?> type);
}
