package com.model.domain.core;

import com.google.common.base.MoreObjects;

public class TextItem<T> extends DataItem<T> {

    private String text;

    @Override
    public String getText() {
        return text;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setText(String text) {
        this.text = text;
        return (T) this;
    }

    @Override
    public byte[] getData() {
        return null;
    }

    @Override
    public boolean isInheritedFrom(Class<?> type) {
        return type.isAssignableFrom(getClass());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("text", text)
            .add("super", super.toString())
            .toString();
    }
}
