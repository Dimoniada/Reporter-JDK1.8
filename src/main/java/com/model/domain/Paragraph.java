package com.model.domain;

import com.model.domain.core.DataItem;
import com.model.domain.core.TextItem;
import com.model.domain.style.TextStyle;
import com.model.formatter.FormatterVisitor;

/**
 * Paragraph
 */
public class Paragraph extends TextItem {
    public static Paragraph create(String text, TextStyle textStyle) {
        return ((DataItem) new Paragraph().setText(text)).setStyle(textStyle);
    }

    public static Paragraph create(String text) {
        return create(text, null);
    }

    public static Paragraph create() {
        return create(null);
    }

    @Override
    public Paragraph accept(FormatterVisitor visitor) throws Throwable {
        visitor.visitParagraph(this);
        return this;
    }
}
