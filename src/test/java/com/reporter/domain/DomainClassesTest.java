package com.reporter.domain;

import com.model.domain.Document;
import com.model.domain.DocumentCase;
import com.model.domain.Table;
import com.model.domain.TableCell;
import com.model.domain.TableHeaderCell;
import com.model.domain.TableHeaderRow;
import com.model.domain.TableRow;
import com.model.domain.core.CompositionPart;
import com.model.formatter.DocumentHolder;
import com.model.formatter.excel.XlsFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.FileUrlResource;

import javax.validation.constraints.NotNull;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class DomainClassesTest {
    static class CustomIterableTableCell implements Iterable<TableCell> {
        /**
         * Custom forward-iterable sequence of TableCell-s
         */
        @NotNull
        @Override
        public Iterator<TableCell> iterator() {
            return new Iterator<TableCell>() {
                private int count = 10;

                @Override
                public boolean hasNext() {
                    return count-- > 0;
                }

                @Override
                public TableCell next() {
                    return TableCell.create("textCell");
                }
            };
        }

        @Override
        public void forEach(Consumer<? super TableCell> action) {
        }

        @Override
        public Spliterator<TableCell> spliterator() {
            return null;
        }
    }

    /**
     * Checks failure call addPart() after adding
     * not a Collection {@link CustomIterableTableCell}
     * as a part of {@link CompositionPart}
     */
    @Test
    public void testCustomCompositionPartsFailure() {
        final TableCell item2 = TableCell.create("cell2");
        final CustomIterableTableCell cells = new CustomIterableTableCell();
        final Exception e = Assertions.assertThrows(IllegalStateException.class, () ->
            TableRow.create().addPart(item2).setParts(cells).addPart(item2)
        );
        Assertions.assertEquals("Parts is not a collection", e.getMessage());
    }

    /**
     * Example how to encapsulate and avoid failure call addPart() after adding
     * a non-Collection {@link CustomIterableTableCell} via setParts()
     */
    @Test
    public void testCustomCompositionPartsOk() throws Throwable {
        final CustomIterableTableCell cells = new CustomIterableTableCell();

        final Table table = Table.create()
            .setTableHeaderRow(
                TableHeaderRow.create()
                    .addParts(
                        TableHeaderCell.create("Column1"),
                        TableHeaderCell.create("Column2")
                    ))
            .addPart(TableRow.create().addPart(TableCell.create("")).addPart(TableCell.create("")))
            .addPart(TableRow.create().setParts(cells))
            .addPart(TableRow.create().addPart(TableCell.create("")).addPart(TableCell.create("")));

        final Document doc = Document.create()
            .setLabel("doc1.xls")
            .addPart(
                DocumentCase.create()
                    .setName("Sheet with table")
                    .addPart(table)
            );

        final XlsFormatter xlsFormatter = spy(XlsFormatter.class);
        final DocumentHolder documentHolder = xlsFormatter.handle(doc);
        documentHolder.close();
        Mockito.verify(xlsFormatter, times(2 + 10 + 2)).visitTableCell(any());
    }

    /**
     * Test on {@link DocumentHolder file-holder} creation,
     * closing and removing file.
     *
     * @throws Throwable MalformedURLException/Exception/IOException
     */
    @Test
    public void testDocumentHolder() throws Throwable {
        final FileUrlResource file = new FileUrlResource("testFile");
        try (DocumentHolder documentHolder = new DocumentHolder(file)) {
            Assertions.assertTrue(AutoCloseable.class.isAssignableFrom(documentHolder.getClass()));
            Assertions.assertTrue(Files.notExists(documentHolder.getResource().getFile().toPath()));
        }
    }
}
