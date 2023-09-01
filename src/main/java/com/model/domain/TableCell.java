package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.domain.style.TextStyle;
import com.model.formatter.FormatterVisitor;

/**
 * Table cell,
 * contains data in text form,
 * base {@link TextStyle style} text
 * and customIndex (default parent-row index)
 */
public class TableCell extends TextItem<TableCell> {

    protected int customIndex;

    public static TableCell create(String text) {
        return new TableCell().setText(text);
    }

    public static TableCell create() {
        return new TableCell();
    }

    @Override
    public TableCell accept(FormatterVisitor visitor) throws Throwable {
        visitor.visitTableCell(this);
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("customIndex", customIndex)
            .add("parent", super.toString())
            .toString();
    }

    public int getCustomIndex() {
        return customIndex;
    }

    public TableCell setCustomIndex(int customIndex) {
        this.customIndex = customIndex;
        return this;
    }
}
