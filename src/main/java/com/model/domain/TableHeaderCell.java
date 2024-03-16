package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.domain.core.TextItem;
import com.model.formatter.FormatterVisitor;

/**
 * Table header cell,
 * contains data in text form
 */
public class TableHeaderCell extends TextItem<TableHeaderCell> {
    /**
     * For instance, is a parent column's index
     */
    protected long columnIndex;

    /**
     * Real column name in SQL view
     */
    protected String aliasName = "";

    public static TableHeaderCell create(String text) {
        return new TableHeaderCell().setText(text);
    }

    public static TableHeaderCell create() {
        return new TableHeaderCell();
    }

    @Override
    public TableHeaderCell accept(FormatterVisitor visitor) throws Throwable {
        visitor.visitTableHeaderCell(this);
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("columnIndex", columnIndex)
            .add("aliasName", aliasName)
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
