package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.domain.styles.Style;
import com.model.formatter.FormatterVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base part of the document, all the rest are inherited from it.
 * If {@link DocumentItem#accept(FormatterVisitor)} is not overridden in the descendant,
 * then an exception "Overridden method "accept" not found for {@link DocumentItem}" will be thrown.
 * Contains style, see TextStyle, LayoutStyle or TextLayoutStyle
 *
 */
public abstract class DocumentItem {
    private static final Logger log = LoggerFactory.getLogger(DocumentItem.class);
    protected Style style;

    public DocumentItem accept(FormatterVisitor visitor) throws Throwable {
        log.debug("No overriding for accept in {}", getClass().getTypeName());
        throw new Throwable("No overriding for accept in " + getClass().getTypeName());
    }

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
