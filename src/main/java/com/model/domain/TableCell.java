package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.domain.core.DataItem;
import com.model.formatter.FormatterVisitor;

/**
 * Table cell,
 * contains data in text form,
 * rowIndex and columnIndex
 */
public class TableCell extends DataItem<TableCell> {
    /**
     * Row index
     */
    protected long rowIndex;
    /**
     * Column index
     */
    protected long columnIndex;

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
        final MoreObjects.ToStringHelper toStringHelper = MoreObjects.toStringHelper(this);
        if (!this.getClass().isAssignableFrom(Picture.class)) {
            toStringHelper.add("text", this.getText());
        }
        return toStringHelper
            .add("rowIndex", rowIndex)
            .add("columnIndex", columnIndex)
            .toString();
    }

    public long getRowIndex() {
        return rowIndex;
    }

    public TableCell setRowIndex(long rowIndex) {
        this.rowIndex = rowIndex;
        return this;
    }

    public long getColumnIndex() {
        return columnIndex;
    }

    public TableCell setColumnIndex(long columnIndex) {
        this.columnIndex = columnIndex;
        return this;
    }
}
