package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.domain.styles.Style;
import com.model.formatter.FormatterVisitor;

/**
 * The base part of the document, all the rest are inherited from it.
 * If {@link DocumentItem#accept(FormatterVisitor)} is not overridden in the descendant,
 * then an exception "Overridden method "accept" not found for {@link DocumentItem}" will be thrown.
 * <p>
 * parentItem is a parent element or null if there is no parent
 * Style and nativeObject are used for styling:
 * Style TextStyle, LayoutStyle or TextLayoutStyle
 * </p>
 */
public abstract class DocumentItem {
    protected Style style;

    public abstract DocumentItem accept(FormatterVisitor visitor) throws Throwable;

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("style", style)
                .toString();
    }

    @SuppressWarnings("unchecked")
    public <T extends DocumentItem> T setStyle(Style style) {
        this.style = style;
        return (T) this;
    }

    public Style getStyle() {
        return style;
    }
}
