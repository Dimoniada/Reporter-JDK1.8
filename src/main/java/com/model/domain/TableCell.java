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
public class TableCell extends DataItem<TableCell> {
    /**
     * Delegated item for storing either text or picture
     */
    protected DataItem<TableCell> dataItem;
    /**
     * Row index
     */
    protected long rowIndex;
    /**
     * Column index
     */
    protected long columnIndex;

    public static TableCell create(String text) {
        final TableCell tableCell = new TableCell();
        tableCell.dataItem = new TextItem<DataItem<TableCell>>().setText(text);
        return tableCell;
    }

    public static TableCell create(byte[] data) {
        final TableCell tableCell = new TableCell();
        tableCell.dataItem = new PictureItem<DataItem<TableCell>>().setData(data);
        return tableCell;
    }

    @Override
    public boolean isDataInheritedFrom(Class<?> type) {
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
    public TableCell setText(String text) throws Throwable {
        dataItem.setText(text);
        return this;
    }

    @Override
    public TableCell setData(byte[] data) throws Throwable {
        dataItem.setData(data);
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
