package com.model.domain.core;

import com.google.common.base.MoreObjects;

/**
 * Base text class, contains text
 */
public abstract class TextItem<T> extends DocumentItem {

    protected String text;

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("text", text)
                .toString();
    }

    public String getText() {
        return text;
    }

    @SuppressWarnings("unchecked")
    public T setText(String text) {
        this.text = text;
        return (T) this;
    }
}
