package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.domain.core.DataItem;
import com.model.domain.core.PictureItem;
import com.model.domain.core.TextItem;
import com.model.formatter.FormatterVisitor;

/**
 * Table cell,
 * contains data in text form,
 * rowIndex and columnIndex
 */
public class TableCell extends DataItem {
    /**
     * Inner data type
     */
    protected Class<?> clazz;
    /**
     * Text data
     */
    protected String text;
    /**
     * Picture raw data
     */
    protected byte[] data;
    /**
     * Row index
     */
    protected long rowIndex;
    /**
     * Column index
     */
    protected long columnIndex;

    public static TableCell create(String text) {
        return new TableCell()
            .setClazz(TextItem.class)
            .setText(text);
    }

    public static TableCell create(byte[] data) {
        return new TableCell()
            .setClazz(PictureItem.class)
            .setData(data);
    }

    @Override
    public boolean isDataInheritedFrom(Class<?> type) {
        return type.isAssignableFrom(clazz);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TableCell setText(String text) {
        this.text = text;
        this.clazz = TextItem.class;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TableCell setData(byte[] data) {
        this.data = data;
        this.clazz = PictureItem.class;
        return this;
    }

    public TableCell setClazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

    @Override
    public TableCell accept(FormatterVisitor visitor) throws Throwable {
        visitor.visitTableCell(this);
        return this;
    }

    @Override
    public String toString() {
        final MoreObjects.ToStringHelper toStringHelper = MoreObjects.toStringHelper(this);
        return toStringHelper
            .add("rowIndex", rowIndex)
            .add("columnIndex", columnIndex)
            .add("text", text)
            .add("data", data)
            .add("super", super.toString())
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
