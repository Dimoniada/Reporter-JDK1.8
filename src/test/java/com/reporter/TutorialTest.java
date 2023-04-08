package com.reporter;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import com.model.domain.*;
import com.model.domain.styles.BorderStyle;
import com.model.domain.styles.StyleService;
import com.model.domain.styles.constants.BorderWeight;
import com.model.domain.styles.constants.Color;
import com.model.formatter.Formatter;
import com.model.formatter.FormatterContext;
import com.model.formatter.FormatterFactory;
import com.model.formatter.csv.CsvFormatter;
import com.model.formatter.excel.XlsFormatter;
import com.model.formatter.excel.XlsxFormatter;
import com.model.formatter.html.HtmlFormatter;
import com.model.formatter.pdf.PdfFormatter;
import com.model.formatter.word.DocFormatter;
import com.model.formatter.word.DocxFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Stream;

@SpringBootTest(classes = {
    FormatterFactory.class, PdfFormatter.class,                             //FormatterFactory and Formatter-s
    XlsFormatter.class, XlsxFormatter.class,
    CsvFormatter.class, HtmlFormatter.class,
    DocFormatter.class, DocxFormatter.class
})
public class TutorialTest {

    @Autowired
    protected FormatterFactory formatterFactory;

    static Stream<Arguments> testArguments() {
        return Stream.of(
            Arguments.of(
                "PDF test",
                new ExportContext()                                             //For export to pdf
                    .setFormat("pdf")
                    .setLocale("en")
                    .setColumns(Arrays.asList("id", "comment", "name"))
                    .setEncoding("UTF-8")
                    .setCsvDelimiter(';')
            ),
            Arguments.of(
                "XLSX test",
                new ExportContext()                                             //For export to xlsx
                    .setFormat("xlsx")
                    .setLocale("en")
                    .setColumns(Arrays.asList("id", "comment", "name"))
                    .setEncoding("UTF-8")
                    .setCsvDelimiter(';')
            ),
            Arguments.of(
                "HTML test",
                new ExportContext()                                             //For export to html
                    .setFormat("html")
                    .setLocale("en")
                    .setColumns(Arrays.asList("id", "comment", "name"))
                    .setEncoding("UTF-8")
                    .setCsvDelimiter(';')
            ),
            Arguments.of(
                "DOCX test",
                new ExportContext()                                             //For export to docx
                    .setFormat("docx")
                    .setLocale("en")
                    .setColumns(Arrays.asList("id", "comment", "name"))
                    .setEncoding("UTF-8")
                    .setCsvDelimiter(';')
            )
        );
    }

    /**
     * @param exportContext could appear from client call for example
     */
    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("testArguments")
    void createReportForWeb(String testName, ExportContext exportContext) {
        //Required parameters for report creation
        final String format = exportContext.getFormat();
        final Locale locale = exportContext.getLocale();
        final List<String> columns = exportContext.getColumns(); //(DB-)Table column names for export
        final String encoding = exportContext.getEncoding();
        final Character csvDelimiter = exportContext.getCsvDelimiter(); //If format == "csv"

        final List<?> data = Arrays.asList(                   //Getting data here from db for instance
            new Person(1, "Name1", "Comment1"),
            new Person(2, "Имя2 с !;№% номерами", "абракадабра с \\ЭЭ последовательностью"),
            new Person(3, "Name3", "Comment3"),
            new Person(4, "Name4", "Comment4")
        );
        /*Starting to prepare table in final report*/
        final ReportTable table = ReportTable.create();
        if (columns.isEmpty()) {                        //If we don't know columns names in db
            table.setTableHeaderRowFromData(true);      //we can get them automatically from db
        } else {                                        //If we know columns names in db
            final TableHeaderRow tableHeaderRow = TableHeaderRow.create();
            columns.forEach(c ->                        //then we might customize visual names in report
                tableHeaderRow
                    .addPart(TableHeaderCell
                        .create()
                        .setText(c)                     //Choice of arbitrary table column names in report
                        .setAliasName(c)                //to be match real column names from db
                    )
            );
            table.setTableHeaderRow(tableHeaderRow);
        }
        table.addDataList(data);                        //Filling table with data
        /*Preparing a formatter that forms report*/
        final TimeZone timeZone = LocaleContextHolder.getTimeZone();
        final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        final FormatterContext formatterContext =   //FormatterContext needs to create Formatter
            FormatterContext.create(
                encoding,                               //Final file's encoding with report
                locale,                                 //Glyphs locale in report
                timeZone,                               //TimeZone needs only for footer with time in some reports
                decimalFormat,                          //Number format in report
                csvDelimiter                            //Only for *.csv files (delimiter)
            );
        final Formatter formatter = formatterFactory.createFormatter(format, formatterContext);

        final String creationDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        final String documentName = "testReportTable"      //Forming full file name of report
            + "_"
            + creationDate
            + "."
            + formatter.getExtension();
        final String formattedDateTime = formatterContext.createFormattedZoneDateTime();

        final Document document =                   //Creating all document in report
            Document
                .create()
                .setLabel(documentName)                 //File or document name
                .addParts(
                    table,                              //Put our table here as a part of document
                    Paragraph.create(),
                    Separator.create(
                        BorderStyle
                            .create(Color.TEAL, BorderWeight.THIN)
                    ),
                    Paragraph
                        .create()
                        .setText(formattedDateTime)
                        .setStyle(formatterContext.createStyleFooter())
                );
        /*Time to stylize our report =)*/

        //We can do it in 3 ways (from simpler to complex):

        //1) By assigning a Style to a separate DocumentItem, like item.setStyle(Style)
        //2) By spreading a Style to all parts of some DocumentItem, like item.spreadStyleToParts(Style)
        //3) By adding Style-s to Formatter's StyleService, like shown below
        //   styles from StyleService will be applied to elements using rules of styles (look Style.class)
        try {
            final StyleService styleService = formatter.getStyleService();
            if (styleService != null) {
                styleService.addStyles(
                    formatterContext.createHeaderCellStyle(),
                    formatterContext.createRowStyleInterlinear(),
                    formatterContext.createRowStyleNormal()
                );

//                ((HtmlStyleService) styleService).setUseHtml4Tags(true);
            }
            //Write report to StreamingResponseBody
            final StreamingResponseBody responseBody =
                out -> {
                    formatter.setOutputStream(out);
                    try {
                        formatter.handle(document).close();
                    } catch (Throwable ignored) {
                    }
                };
            final ContentDisposition contentDisposition =
                ContentDisposition
                    .builder("attachment")
                    .filename(
                        documentName,
                        Charset.isSupported(encoding)
                            ? Charset.forName(encoding)
                            : StandardCharsets.UTF_8
                    )
                    .build();
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(contentDisposition);
            ResponseEntity<StreamingResponseBody> res = ResponseEntity
                .ok()
                .headers(headers)
                .contentType(formatter.getContentMediaType())
                .body(responseBody);

            //Let's read and check ResponseBody on "other" side
            final StreamingResponseBody testedBody = res.getBody();
            assert testedBody != null;
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            testedBody.writeTo(os);
            if (os.size() != 0) {
                switch (format) {
                    case "pdf":
                        final PdfReader pdfReader = new PdfReader(new ByteArrayInputStream(os.toByteArray()));
                        final PdfDocument doc1 = new PdfDocument(pdfReader);
                        final ITextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
                        final String currentText = PdfTextExtractor.getTextFromPage(doc1.getPage(1), strategy);
                        pdfReader.close();
                        Assertions.assertTrue(
                            currentText.contains("id comment name\n" +
                                "1 Comment1 Name1\n" +
                                "2 абракадабра с \\ЭЭ последовательностью Имя2 с !;№% номерами\n" +
                                "3 Comment3 Name3\n" +
                                "4 Comment4 Name4\n")
                        );
                        break;
                    case "xlsx":
                        final Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(os.toByteArray()));
                        final Sheet sheet = wb.getSheetAt(0);
                        final String check = sheet.getRow(3).getCell(2).getStringCellValue();
                        wb.close();
                        Assertions.assertEquals("Name3", check);
                        break;
                    case "html":
                        final String text = os.toString(StandardCharsets.UTF_8.name());
                        Assertions.assertTrue(text.contains("Comment4"));
                        break;
                    case "docx":
                        XWPFDocument docx = new XWPFDocument(new ByteArrayInputStream(os.toByteArray()));
                        Assertions.assertEquals(
                            "Comment3",
                            docx.getTableArray(0).getRow(3).getCell(1).getText()
                        );
                }
            }
        } catch (Throwable throwable) {
            throw new IllegalStateException(throwable.getMessage(), throwable);
        }
    }

    static class Person {
        Serializable id;
        String name;
        String comment;

        public Person(Serializable id, String name, String comment) {
            this.id = id;
            this.name = name;
            this.comment = comment;
        }

        public Serializable getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getComment() {
            return comment;
        }
    }
}
