package com.model.domain.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base data interface, contains base data access methods
 */
public abstract class DataItem<T> extends DocumentItem {

    private static final Logger log = LoggerFactory.getLogger(DataItem.class);
    private final String superClassName = getClass().getGenericSuperclass().getTypeName();

    public abstract String getText();

    public T setText(String text) throws Throwable {
        log.debug("No implementation for setText() in {}", superClassName);
        throw new Throwable("No implementation for setText() in " + superClassName);
    }

    public abstract byte[] getData();

    public T setData(byte[] data) throws Throwable {
        log.debug("No implementation for setData() in {}", superClassName);
        throw new Throwable("No implementation for setData() in " + superClassName);
    }

    public abstract boolean isInheritedFrom(Class<?> type);
}
