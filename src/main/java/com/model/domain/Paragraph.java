package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.domain.style.TextStyle;
import com.model.formatter.FormatterVisitor;

/**
 * Paragraph
 */
public class Paragraph extends TextItem<Paragraph> {
    public static Paragraph create(String text, TextStyle textStyle) {
        return new Paragraph().setText(text).setStyle(textStyle);
    }

    public static Paragraph create(String text) {
        return create(text, null);
    }

    public static Paragraph create() {
        return create(null, null);
    }

    @Override
    public Paragraph accept(FormatterVisitor visitor) throws Throwable {
        visitor.visitParagraph(this);
        return this;
    }

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .toString();
    }
}
