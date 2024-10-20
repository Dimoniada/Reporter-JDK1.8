package com.model.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.model.domain.core.DocumentItem;
import com.model.domain.style.BorderStyle;
import com.model.formatter.FormatterVisitor;

/**
 * For Pdf and Html:
 * horizontal line from left to right in the inner border of the sheet,
 * analogue of <hr> for html.
 */

public class LineSeparator extends DocumentItem {
    /**
     * Separator line type
     */
    protected BorderStyle borderStyle;

    public static LineSeparator create() {
        return new LineSeparator();
    }

    public static LineSeparator create(BorderStyle borderStyle) {
        return new LineSeparator().setBorderStyle(borderStyle);
    }

    @Override
    public LineSeparator accept(FormatterVisitor visitor) throws Throwable {
        visitor.visitLineSeparator(this);
        return this;
    }

    public BorderStyle getBorderStyle() {
        return borderStyle;
    }

    public LineSeparator setBorderStyle(BorderStyle borderStyle) {
        this.borderStyle = borderStyle;
        return this;
    }

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("borderStyle", borderStyle)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final LineSeparator that = (LineSeparator) o;

        return
            Objects.equal(this.borderStyle, that.borderStyle)
                && Objects.equal(this.style, that.style);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(borderStyle, style);
    }
}
