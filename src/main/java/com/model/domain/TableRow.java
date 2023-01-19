package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.formatter.FormatterVisitor;

/**
 * Table row,
 * consists of {@link TableCell cells}
 */
public class TableRow extends CompositionPart<TableRow, TableCell> {

    protected int rowIndex;
    private int cellCount;

    public static TableRow create(TableCell... tableCells) {
        return new TableRow().addParts(tableCells);
    }

    public static TableRow create() {
        return new TableRow();
    }

    public TableRow accept(FormatterVisitor visitor) throws Throwable {
        visitor.visitTableRow(this);
        return this;
    }

    @Override
    public TableRow addPart(TableCell docItem) {
        this.cellCount++;
        docItem.setCustomIndex(rowIndex);
        return super.addPart(docItem);
    }

    @Override
    public TableRow addParts(TableCell... docItems) {
        this.cellCount += docItems.length;
        for (TableCell item : docItems) {
            item.setCustomIndex(rowIndex);
        }
        return super.addParts(docItems);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("parent", super.toString())
            .toString();
    }

    public int getCellCount() {
        return cellCount;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public TableRow setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
        if (parts != null) {
            for (TableCell item : parts) {
                item.setCustomIndex(rowIndex);
            }
        }
        return this;
    }
}
