package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.domain.core.DataItem;
import com.model.domain.core.PictureItem;
import com.model.domain.core.TextItem;
import com.model.formatter.FormatterVisitor;

/**
 * Table header cell,
 * contains data in text form
 */
public class TableHeaderCell extends DataItem {
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
     * For instance, is a parent column's index
     */
    protected long columnIndex;
    /**
     * Real column name in SQL view
     */
    protected String aliasName = "";

    public static TableHeaderCell create(String text) {
        return new TableHeaderCell()
            .setClazz(TextItem.class)
            .setText(text);
    }

    public static TableHeaderCell create(byte[] data) {
        return new TableHeaderCell()
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
    public TableHeaderCell setText(String text) {
        this.text = text;
        this.clazz = TextItem.class;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TableHeaderCell setData(byte[] data) {
        this.data = data;
        this.clazz = PictureItem.class;
        return this;
    }

    public TableHeaderCell setClazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

    @Override
    public TableHeaderCell accept(FormatterVisitor visitor) throws Throwable {
        visitor.visitTableHeaderCell(this);
        return this;
    }

    @Override
    public String toString() {
        final MoreObjects.ToStringHelper toStringHelper = MoreObjects.toStringHelper(this);
        return toStringHelper
            .add("columnIndex", columnIndex)
            .add("aliasName", aliasName)
            .add("text", text)
            .add("data", data)
            .add("super", super.toString())
            .toString();
    }

    public String getAliasName() {
        return aliasName;
    }

    public TableHeaderCell setAliasName(String aliasName) {
        this.aliasName = aliasName;
        return this;
    }

    public long getColumnIndex() {
        return columnIndex;
    }

    public TableHeaderCell setColumnIndex(long columnIndex) {
        this.columnIndex = columnIndex;
        return this;
    }
}
