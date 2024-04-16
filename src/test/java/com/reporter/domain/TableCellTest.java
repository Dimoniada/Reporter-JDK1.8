package com.reporter.domain;

import com.model.domain.TableCell;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TableCellTest {

    @Test
    public void testToStringWhenIndicesAreSetThenReturnStringWithIndices() {
        // Arrange
        final TableCell tableCell = TableCell.create("Test");
        tableCell.setRowIndex(1);
        tableCell.setColumnIndex(2);

        // Act
        final String result = tableCell.toString();

        // Assert
        assertThat(result).contains("rowIndex=1", "columnIndex=2");
    }
}