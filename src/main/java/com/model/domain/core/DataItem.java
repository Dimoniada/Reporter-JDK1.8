package com.model.domain.core;

import com.google.common.base.MoreObjects;

/**
 * Base data class, contains bytes
 */
public abstract class DataItem<T> extends DocumentItem {

    protected byte[] data;

    @SuppressWarnings("unchecked")
    public T setData(byte[] data) {
        this.data = data;
        return (T) this;
    }

    public byte[] getData() {
        return data;
    }

    @SuppressWarnings("unchecked")
    public T setText(String text) {
        this.data = text != null ? text.getBytes() : null;
        return (T) this;
    }

    public String getText() {
        return new String(data != null ? data : new byte[0]);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("text", getText())
            .toString();
    }
}
