package com.model.domain;

import com.model.domain.core.DataItem;
import com.model.formatter.FormatterVisitor;

/**
 * Text footer
 */
public class Footer extends DataItem<Footer> {

    public static Footer create() {
        return new Footer();
    }

    public static Footer create(String text) {
        return new Footer().setText(text);
    }

    @Override
    public Footer accept(FormatterVisitor visitor) throws Throwable {
        visitor.visitFooter(this);
        return this;
    }
}
