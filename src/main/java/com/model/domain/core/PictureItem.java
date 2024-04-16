package com.model.domain.core;

import com.google.common.base.MoreObjects;

public class PictureItem<T> extends DataItem<T> {

    protected byte[] data;

    @Override
    public String getText() {
        return null;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setData(byte[] data) {
        this.data = data;
        return (T) this;
    }

    @Override
    public boolean isInheritedFrom(Class<?> type) {
        return type.isAssignableFrom(getClass());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("data", data)
            .add("super", super.toString())
            .toString();
    }
}
