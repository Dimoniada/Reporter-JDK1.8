package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.domain.style.TextStyle;

/**
 * Base text class,
 * contains text and {@link TextStyle style}
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
