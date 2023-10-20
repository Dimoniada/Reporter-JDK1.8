package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.formatter.FormatterVisitor;

/**
 * Table header,
 * consists of {@link TableHeaderCell cells}
 */
public class TableHeaderRow extends CompositionPart<TableHeaderRow, TableHeaderCell> {
    /**
     * Count of cells
     */
    private long cellCount;

    public static TableHeaderRow create(TableHeaderCell... docItems) {
        return new TableHeaderRow().addParts(docItems);
    }

    public static TableHeaderRow create() {
        return new TableHeaderRow();
    }

    @Override
    public TableHeaderRow accept(FormatterVisitor visitor) throws Throwable {
        visitor.visitTableHeaderRow(this);
        return this;
    }

    @Override
    public TableHeaderRow addPart(TableHeaderCell docItem) {
        docItem.setColumnIndex(this.cellCount);
        this.cellCount ++;
        return super.addPart(docItem);
    }

    @Override
    public TableHeaderRow addParts(TableHeaderCell... docItems) {
        long columnIndex = this.cellCount;
        for (final TableHeaderCell docItem : docItems) {
            docItem.setColumnIndex(columnIndex);
            columnIndex ++;
        }
        this.cellCount += docItems.length;
        return super.addParts(docItems);
    }

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("cellCount", cellCount)
                .toString();
    }

    public long getCellCount() {
        return cellCount;
    }
}
