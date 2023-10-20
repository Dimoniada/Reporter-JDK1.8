package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.formatter.FormatterVisitor;

/**
 * Document title,
 * contains only text
 */
public class Title extends TextItem<Title> {

    public static Title create() {
        return new Title();
    }

    public static Title create(String text) {
        return new Title().setText(text);
    }

    @Override
    public Title accept(FormatterVisitor visitor) throws Throwable {
        visitor.visitTitle(this);
        return this;
    }

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .toString();
    }
}
