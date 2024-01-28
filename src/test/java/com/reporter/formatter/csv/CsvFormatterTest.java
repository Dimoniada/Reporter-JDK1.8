package com.reporter.formatter.csv;

import com.model.domain.db.QueryTable;
import com.model.formatter.DocumentHolder;
import com.model.formatter.csv.CsvFormatter;
import com.reporter.formatter.BaseDocument;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileUrlResource;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

@SpringBootTest(classes = QueryTable.class)
class CsvFormatterTest extends BaseDocument {
    public static final String expected =
            "\n" +
            "Test document v.1\n" +
            "\n" +
            "Chapter 1\n" +
            "Chapter 1.1\n" +
            "Chapter 1.1.1\n" +
            "This is an example of text in paragraph\n" +
            "Column 1;Column 2\n" +
            "Cell 1.1;Cell 1.2\n" +
            "Cell 2.1;Cell 2.2\n" +
            "Cell 3.1;Cell 3.2\n" +
            "Cell 4.1;Cell 4.2\n" +
            "Chapter 2\n" +
            "Chapter 2.1\n" +
            "Chapter 2.1.1\n" +
            "This is an example of text in paragraph 2\n" +
            "Column 1;Column 2\n" +
            "Cell 1.1;Cell 1.2\n" +
            "Cell 2.1;Cell 2.2\n" +
            "Title 1\n" +
            "paragraph 1\n" +
            "shifted heading\n" +
            "столбец1;column2\n" +
            "1,000;2,000\n" +
            "3,000;\"4 and some escape characters (символы) %;;;;;\\/\"\n" +
            "5,000;6,000\n";

    @BeforeEach
    public void initDoc() throws Exception {
        super.initDoc();
    }

    @Test
    public void testSaveTextToNewFile() throws Throwable {
        final CsvFormatter csvFormatter = (CsvFormatter) new CsvFormatter().setEncoding("Cp1251");
        csvFormatter.setFileName("testFile");
        Assertions.assertEquals("testFile", csvFormatter.getFileName());
        try (DocumentHolder documentHolder = csvFormatter.handle(doc)) {
            final String text =
                FileUtils.readFileToString(documentHolder.getResource().getFile(), Charset.forName("Cp1251"));
            Assertions.assertEquals(text, expected);
        }
    }

    @Test
    public void testSaveTextToResource() throws Throwable {

        final CsvPreference csvPreference = new CsvPreference.Builder('\"', ';', "\n").build();
        final CsvFormatter csvFormatter = (CsvFormatter) new CsvFormatter(csvPreference).setEncoding("Cp1251");
        final FileUrlResource resource = new FileUrlResource("testFile.csv");
        csvFormatter.setResource(resource);

        Assertions.assertEquals(resource, csvFormatter.getResource());

        try (DocumentHolder documentHolder = csvFormatter.handle(doc)) {
            final String text =
                FileUtils.readFileToString(documentHolder.getResource().getFile(), Charset.forName("Cp1251"));
            Assertions.assertEquals(expected, text);
        }
    }

    @Test
    public void testSaveTextToStream() throws Throwable {

        final CsvPreference csvPreference = new CsvPreference.Builder('\"', ';', "\n").build();
        final CsvFormatter csvFormatter = (CsvFormatter) new CsvFormatter(csvPreference).setEncoding("Cp1251");
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        csvFormatter.setOutputStream(os);

        try (DocumentHolder ignored = csvFormatter.handle(doc)) {
            final String text = os.toString("Cp1251");
            Assertions.assertEquals(expected, text);
        }
    }

    @Test
    public void testFormatterProperties() {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final OutputStreamWriter writer = new OutputStreamWriter(os);
        final CsvPreference csvPreference = new CsvPreference.Builder('\"', ';', "\n").build();
        final CsvListWriter csvWriter = new CsvListWriter(writer, csvPreference);
        final CsvFormatter csvFormatter = new CsvFormatter();

        final DecimalFormat df = new DecimalFormat("‰00");

        csvFormatter.setOs(os).setWriter(writer).setCsvWriter(csvWriter)
            .setEncoding("ISO-8859-1").setDecimalFormat(df);

        Assertions.assertEquals(os, csvFormatter.getOs());
        Assertions.assertEquals(writer, csvFormatter.getWriter());
        Assertions.assertEquals(csvWriter, csvFormatter.getCsvWriter());
        Assertions.assertEquals("ISO-8859-1", csvFormatter.getEncoding());
        Assertions.assertEquals(df, csvFormatter.getDecimalFormat());
    }

    @Test
    public void testDocumentNotSet() {
        final CsvFormatter csvFormatter = new CsvFormatter();
        final Exception e = Assertions.assertThrows(Exception.class, () -> csvFormatter.handle(null).close());
        Assertions.assertTrue("Document is not defined".contains(e.getMessage()));
    }
}
