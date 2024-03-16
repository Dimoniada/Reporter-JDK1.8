package com.model.domain.core;

import com.google.common.base.MoreObjects;

/**
 * Base data class, contains bytes
 */
public abstract class DataItem<T> extends DocumentItem {

    protected byte[] data;

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("data", data)
                .toString();
    }

    public byte[] getData() {
        return data;
    }

    @SuppressWarnings("unchecked")
    public T setData(byte[] data) {
        this.data = data;
        return (T) this;
    }
}
