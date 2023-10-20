package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.formatter.FormatterVisitor;

/**
 * Table row,
 * consists of {@link TableCell cells}
 */
public class TableRow extends CompositionPart<TableRow, TableCell> {
    /**
     * Row index in parent table
     */
    protected long rowIndex;
    /**
     * Count of cells
     */
    private long cellCount;

    public static TableRow create(TableCell... tableCells) {
        return new TableRow().addParts(tableCells);
    }

    public static TableRow create() {
        return new TableRow();
    }

    @Override
    public TableRow accept(FormatterVisitor visitor) throws Throwable {
        visitor.visitTableRow(this);
        return this;
    }

    @Override
    public TableRow addPart(TableCell docItem) {
        docItem.setColumnIndex(this.cellCount);
        this.cellCount++;
        docItem.setRowIndex(rowIndex);
        return super.addPart(docItem);
    }

    @Override
    public TableRow addParts(TableCell... docItems) {
        long columnIndex = this.cellCount;
        for (final TableCell item : docItems) {
            item.setRowIndex(rowIndex);
            item.setColumnIndex(columnIndex);
            columnIndex++;
        }
        this.cellCount += docItems.length;
        return super.addParts(docItems);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("parentObject", parentObject)
            .add("rowIndex", rowIndex)
            .add("cellCount", cellCount)
            .toString();
    }

    public long getCellCount() {
        return cellCount;
    }

    public long getRowIndex() {
        return rowIndex;
    }

    public TableRow setRowIndex(long rowIndex) {
        this.rowIndex = rowIndex;
        if (parts != null) {
            for (final TableCell item : parts) {
                item.setRowIndex(rowIndex);
            }
        }
        return this;
    }
}
