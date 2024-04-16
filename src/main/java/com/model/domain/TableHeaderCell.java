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
public class TableHeaderCell extends DataItem<TableHeaderCell> {
    /**
     * Delegated item for storing either text or picture
     */
    protected DataItem<TableHeaderCell> dataItem;
    /**
     * For instance, is a parent column's index
     */
    protected long columnIndex;

    /**
     * Real column name in SQL view
     */
    protected String aliasName = "";

    public static TableHeaderCell create(String text) {
        final TableHeaderCell tableHeaderCell = new TableHeaderCell();
        tableHeaderCell.dataItem = new TextItem<DataItem<TableHeaderCell>>().setText(text);
        return tableHeaderCell;
    }

    public static TableHeaderCell create(byte[] data) {
        final TableHeaderCell tableHeaderCell = new TableHeaderCell();
        tableHeaderCell.dataItem = new PictureItem<DataItem<TableHeaderCell>>().setData(data);
        return tableHeaderCell;
    }

    @Override
    public boolean isInheritedFrom(Class<?> type) {
        return type.isAssignableFrom(dataItem.getClass());
    }

    @Override
    public String getText() {
        return dataItem.getText();
    }

    @Override
    public byte[] getData() {
        return dataItem.getData();
    }

    @Override
    public TableHeaderCell setText(String text) throws Throwable {
        dataItem.setText(text);
        return this;
    }

    @Override
    public TableHeaderCell setData(byte[] data) throws Throwable {
        dataItem.setData(data);
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
