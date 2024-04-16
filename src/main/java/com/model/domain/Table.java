package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.domain.core.CompositionPart;
import com.model.domain.style.TextStyle;
import com.model.formatter.FormatterVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Table class,
 * contains the label {@link Table#label},
 * optionally contain a header line with column names - {@link TableRow tableHeaderRow},
 * as well as an array of rows {@link TableRow tableRows}.
 * <p>
 * If the table style is {@link TextStyle},
 * then it only applies to {@link Table#label}
 */
public class Table extends CompositionPart<Table, TableRow> {
    private static final Logger log = LoggerFactory.getLogger(Table.class);
    /**
     * Table header, contains column names
     */
    protected TableHeaderRow tableHeaderRow;

    /**
     * Table label, displayed in front of its header
     */
    protected String label;

    /**
     * Current number of columns in the table
     */
    protected long columnCount;

    /**
     * Current number of rows in the table
     */
    protected long rowCount;

    public static Table create(TableHeaderRow tableHeaderRow) {
        final Table table = new Table();
        table.setTableHeaderRow(tableHeaderRow);
        return table;
    }

    public static Table create() {
        return new Table();
    }

    @Override
    public Table accept(FormatterVisitor visitor) throws Throwable {
        visitor.visitTable(this);
        log.debug("accept: called on table {}", this);
        return this;
    }

    @Override
    public Table addPart(TableRow docItem) {
        docItem.setRowIndex(rowCount);
        this.rowCount += 1;
        this.columnCount = Long.max(this.columnCount, docItem.getCellCount());
        log.debug("addPart: result columns - {} and rows - {}", rowCount, columnCount);
        return super.addPart(docItem);
    }

    @Override
    public Table addParts(TableRow... docItems) {
        for (int i = 0; i < docItems.length; i++) {
            docItems[i].setRowIndex(i + rowCount);
        }
        this.rowCount += docItems.length;
        Stream.of(docItems)
            .map(TableRow::getCellCount)
            .mapToLong(v -> v)
            .max()
            .ifPresent(max -> this.columnCount = Long.max(this.columnCount, max));
        log.debug("addParts: result columns - {} and rows - {}", rowCount, columnCount);
        return super.addParts(docItems);
    }

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("tableHeaderRow", tableHeaderRow)
                .add("label", label)
                .toString();
    }

    public Optional<TableHeaderRow> getTableHeaderRow() {
        return Optional.ofNullable(tableHeaderRow);
    }

    public Table setTableHeaderRow(TableHeaderRow tableHeaderRow) {
        this.tableHeaderRow = tableHeaderRow;
        tableHeaderRow.setParentObject(this);
        log.debug("setTableHeaderRow: result tableHeaderRow - {}", tableHeaderRow);
        return this;
    }

    public String getLabel() {
        return label;
    }

    public Table setLabel(String label) {
        this.label = label;
        log.debug("setLabel: result label - {}", label);
        return this;
    }
}
