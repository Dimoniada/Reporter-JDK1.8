package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.domain.core.TextItem;
import com.model.formatter.FormatterVisitor;

/**
 * Text footer
 */
public class Footer extends TextItem<Footer> {

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

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .toString();
    }
}
