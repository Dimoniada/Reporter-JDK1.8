package com.reporter.formatter.html;

import com.google.common.base.Objects;
import com.model.domain.Document;
import com.model.domain.DocumentItem;
import com.model.domain.Footer;
import com.model.domain.Heading;
import com.model.domain.Paragraph;
import com.model.domain.Table;
import com.model.domain.TableCell;
import com.model.domain.TableHeaderCell;
import com.model.domain.TableHeaderRow;
import com.model.domain.TableRow;
import com.model.domain.Title;
import com.model.domain.style.LayoutStyle;
import com.model.domain.style.Style;
import com.model.domain.style.StyleCondition;
import com.model.domain.style.TextStyle;
import com.model.domain.style.constant.Color;
import com.model.domain.style.constant.FillPattern;
import com.model.domain.style.constant.HorAlignment;
import com.model.domain.style.constant.VertAlignment;
import com.model.domain.style.geometry.Geometry;
import com.model.domain.style.geometry.GeometryDetails;
import com.model.formatter.DocumentHolder;
import com.model.formatter.html.HtmlFormatter;
import com.model.formatter.html.HtmlFormatterVisitor;
import com.model.formatter.html.style.HtmlStyleService;
import com.model.formatter.html.tag.HtmlDiv;
import com.model.formatter.html.tag.HtmlFooter;
import com.model.formatter.html.tag.HtmlH1;
import com.model.formatter.html.tag.HtmlHeading;
import com.model.formatter.html.tag.HtmlParagraph;
import com.model.formatter.html.tag.HtmlTableCell;
import com.model.formatter.html.tag.HtmlTableHeaderCell;
import com.model.formatter.html.tag.HtmlTag;
import com.reporter.formatter.BaseDocument;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileUrlResource;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HtmlFormatterTest extends BaseDocument {

    public static final String expected = "<!doctype html><html><head><meta charset=\"UTF-8\"><title>Test document" +
        "</title></head><body><h1>Title 1</h1><p style=\"font-family:" +
        "courierNew,monospace;font-size:10pt;font-weight:bold\">paragraph 1</p><table><tr><th style=" +
        "\"border-bottom:double #000000;border-collapse:collapse;border-left:double #000000;border-right:" +
        "double #000000;border-top:double #000000;font-family:arial,monospace;font-size:14pt;" +
        "font-weight:bold;height:15px;transform:rotate(10deg);width:20px\">column1</th><th style=" +
        "\"border-bottom:double #000000;border-collapse:collapse;border-left:double #000000;border-right:" +
        "double #000000;border-top:double #000000;font-family:arial,monospace;font-size:14pt;" +
        "font-weight:bold;height:15px;transform:rotate(10deg);width:20px\">column2 (столбец2)</th></tr><tr>" +
        "<td style=\"font-family:arial,monospace;font-size:14pt;font-weight:bold\">1,000</td>" +
        "<td style=\"font-family:arial,monospace;font-size:14pt;font-weight:bold\">2,000</td></tr>" +
        "<tr><td style=\"font-family:arial,monospace;font-size:14pt;font-weight:bold\">3,000</td>" +
        "<td style=\"font-family:arial,monospace;font-size:14pt;font-weight:bold\">4,000</td></tr>" +
        "<tr><td style=\"font-family:arial,monospace;font-size:14pt;font-weight:bold\">5,000</td>" +
        "<td style=\"font-family:arial,monospace;font-size:14pt;font-weight:bold\">6,000</td></tr>" +
        "</table><h1 style=\"font-size:20pt\">Test document v.1</h1><hr " +
        "style=\"border-bottom:1px solid #008080;border-collapse:collapse\"><h1 style=\"" +
        "font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">Chapter 1</h1>" +
        "<h2 style=\"font-family:courierNew,monospace;font-size:10pt;" +
        "font-weight:bold\">Chapter 1.1</h2><h3 style=\"font-family:" +
        "courierNew,monospace;font-size:10pt;font-weight:bold\">Chapter 1.1.1</h3><p style=\"" +
        "font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">This is an " +
        "example of text in paragraph</p><table><tr><th style=\"border-bottom:double #000000;border-collapse:" +
        "collapse;border-left:double #000000;border-right:double #000000;border-top:double #000000;" +
        "font-family:arial,monospace;font-size:14pt;font-weight:bold;height:15px;transform:rotate(10deg);width:20px" +
        "\">Column 1</th><th style=\"border-bottom:double #000000;border-collapse:collapse;border-left:" +
        "double #000000;border-right:double #000000;border-top:double #000000;font-family:arial," +
        "monospace;font-size:14pt;font-weight:bold;height:15px;transform:rotate(10deg);width:20px\">Column 2</th>" +
        "</tr><tr><td style=\"font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">" +
        "Cell 1.1</td><td style=\"font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">" +
        "Cell 1.2</td></tr><tr><td style=\"font-family:courierNew,monospace;font-size:10pt;font-weight:" +
        "bold\">Cell 2.1</td><td style=\"font-family:courierNew,monospace;font-size:10pt;font-weight:" +
        "bold\">Cell 2.2</td></tr><tr><td style=\"font-family:courierNew,monospace;font-size:10pt;" +
        "font-weight:bold\">Cell 3.1</td><td style=\"font-family:courierNew,monospace;font-size:10pt;" +
        "font-weight:bold\">Cell 3.2</td></tr><tr><td style=\"font-family:courierNew,monospace;" +
        "font-size:10pt;font-weight:bold\">Cell 4.1</td><td style=\"font-family:courierNew,monospace;" +
        "font-size:10pt;font-weight:bold\">Cell 4.2</td></tr></table><h1 style=\"" +
        "font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">Chapter 2</h1><h2 style=" +
        "\"font-family:courierNew,monospace;font-size:10pt;font-weight:" +
        "bold\">Chapter 2.1</h2><h3 style=\"font-family:courierNew,monospace;" +
        "font-size:10pt;font-weight:bold\">Chapter 2.1.1</h3><p style=\"" +
        "font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">This is an example of text in paragraph" +
        " 2</p><table><tr><th style=\"border-bottom:double #000000;border-collapse:collapse;border-left:double " +
        "#000000;border-right:double #000000;border-top:double #000000;font-family:arial,monospace;" +
        "font-size:14pt;font-weight:bold;height:15px;transform:rotate(10deg);width:20px\">Column 1</th><th " +
        "style=\"border-bottom:double #000000;border-collapse:collapse;border-left:double #000000;border-right:" +
        "double #000000;border-top:double #000000;font-family:arial,monospace;font-size:14pt;" +
        "font-weight:bold;height:15px;transform:rotate(10deg);width:20px\">Column 2</th></tr><tr><td style=" +
        "\"font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">Cell 1.1</td><td style=" +
        "\"font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">Cell 1.2</td></tr><tr>" +
        "<td style=\"font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">Cell 2.1</td>" +
        "<td style=\"font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">Cell 2.2</td>" +
        "</tr></table><h1>Title 1</h1><p>paragraph 1</p><h2>shifted heading</h2><table><tr><th>столбец1</th><th>" +
        "column2</th></tr><tr><td style=\"font-family:courierNew,monospace;font-size:10pt;font-weight:" +
        "bold\">1,000</td><td style=\"font-family:courierNew,monospace;font-size:10pt;font-weight:" +
        "bold\">2,000</td></tr><tr><td style=\"font-family:courierNew,monospace;font-size:10pt;" +
        "font-weight:bold\">3,000</td><td style=\"font-family:courierNew,monospace;font-size:10pt;" +
        "font-weight:bold\">4 and some escape characters (символы) %;;;;;\\/</td></tr><tr><td style=\"" +
        "font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">5,000</td><td style=\"" +
        "font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">6,000</td></tr></table></body></html>";

    @Override
    @BeforeEach
    public void initDoc() throws Exception {
        super.initDoc();
    }

    /**
     * Test on uniq style, writing one in .html document
     *
     * @throws Throwable Exception/IOException
     */
    @Test
    public void testStyleWriting() throws Throwable {

        final Style titleStyle = TextStyle
            .create()
            .setFontNameResource("Brush Script MT")
            .setItalic(true)
            .setBold(true)
            .setFontSize((short) 35)
            .setColor(Color.GREEN)
            .setCondition(StyleCondition.create(Title.class, o -> o instanceof Title));

        final Style paragraphStyle = TextStyle
            .create()
            .setFontNameResource("Gill Sans")
            .setFontSize((short) 15)
            .setColor(Color.RED)
            .setCondition(StyleCondition.create(Paragraph.class, o -> o instanceof Paragraph));

        final HtmlStyleService styleService = HtmlStyleService.create()
            .addStyles(titleStyle, paragraphStyle);
        styleService.setWriteStyleInplace(false);

        final Document doc1 = Document.create().setLabel("doc1")
            .addParts(Heading.create(1).setText("testHeading"),
                Heading.create(2).setText("testHeading"),
                Title.create().setText("mainTitle"),
                Heading.create(3).setText("testHeading"),
                Paragraph.create().setText("testParagraph1"),
                Paragraph.create().setText("testParagraph2"),
                Paragraph.create().setText("testParagraph3"),
                Heading.create(2).setText("nextHeading"),
                Paragraph.create().setText("testParagraph4")
            );

        final HtmlFormatter htmlFormatter = HtmlFormatter.create().setStyleService(styleService);
        try (DocumentHolder documentHolder = htmlFormatter.handle(doc1)) {
            final String text = FileUtils.readFileToString(documentHolder.getResource().getFile(), StandardCharsets.UTF_8);
            Assertions.assertEquals(1, StringUtils.countOccurrencesOf(text,
                "{color:#00FF00;" +
                    "font-family:Brush Script MT,monospace;" +
                    "font-size:35pt;" +
                    "font-style:italic;" +
                    "font-weight:bold}"));
            Assertions.assertEquals(1, StringUtils.countOccurrencesOf(text,
                "{color:#FF0000;" +
                    "font-family:Gill Sans,monospace;" +
                    "font-size:15pt}"));
        }
    }

    /**
     * Test on {@link HtmlFormatter#handle handle} call
     * and proper saving result in "fileName"
     *
     * @throws Throwable Exception/IOException
     */
    @Test
    public void testSaveToFile() throws Throwable {
        final HtmlFormatter htmlFormatter = HtmlFormatter.create();
        try (DocumentHolder documentHolder = htmlFormatter.handle(doc)) {
            final String text = FileUtils.readFileToString(documentHolder.getResource().getFile(), StandardCharsets.UTF_8);
            Assertions.assertEquals(expected, text);
        }
    }

    /**
     * Test on {@link HtmlFormatter#handle handle} call
     * and proper saving result in "fileName"
     *
     * @throws Throwable Exception/IOException
     */
    @Test
    public void testSaveToFileTextWithTransform() throws Throwable {
        final HtmlFormatter htmlFormatter = HtmlFormatter.create();
        try (DocumentHolder documentHolder = htmlFormatter.handle(
            Document.create(
                Paragraph
                    .create("test string for transform")
                    .setStyle(
                        LayoutStyle.create()
                            .setGeometryDetails(
                                GeometryDetails.create()
                                    .setAngle(
                                        Geometry.create().add("html", "10deg")
                                    )
                                    .setTransformCenter(
                                        Geometry.<Map.Entry<HorAlignment, VertAlignment>>create()
                                            .add(
                                                "html",
                                                new AbstractMap.SimpleEntry<>(
                                                    HorAlignment.LEFT,
                                                    VertAlignment.BOTTOM
                                                )
                                            )
                                    )
                                    .setWidth(
                                        Geometry.create().add("html", "200px")
                                    )
                                    .setHeight(
                                        Geometry.create().add("html", "200px")
                                    )
                                    .setScaleX(
                                        Geometry.create().add("html", "0.3")
                                    )
                                    .setScaleY(
                                        Geometry.create().add("html", "0.3")
                                    )
                            )
                            .setVertAlignment(VertAlignment.CENTER)
                            .setHorAlignment(HorAlignment.CENTER)
                    )
            )
        )) {
            final String text = FileUtils.readFileToString(documentHolder.getResource().getFile(), StandardCharsets.UTF_8);

            Assertions.assertTrue(
                text.contains("display:table-cell;" +
                    "height:200px;" +
                    "text-align:center;" +
                    "transform:scaleX(0.3) scaleY(0.3) rotate(10deg);" +
                    "transform-origin:left bottom;" +
                    "vertical-align:middle;" +
                    "width:200px"
                )
            );
        }
    }

    /**
     * Test on {@link HtmlFormatter#handle handle} call
     * with styled <colgroup></colgroup> tag which propagates to columns
     * and proper saving result in "fileName"
     *
     * @throws Throwable Exception/IOException
     */
    @Test
    public void testSaveToFileWithColgroupStyles() throws Throwable {
        final HtmlFormatter htmlFormatter = HtmlFormatter.create();
        final Document doc1 = Document
            .create()
            .setLabel("Test document")
            .setAuthor("A1 Systems")
            .setDescription("meta information")
            .addParts(
                Table
                    .create()
                    .setTableHeaderRow(
                        TableHeaderRow
                            .create()
                            .addParts(
                                TableHeaderCell.create().setText("Column 1"),
                                TableHeaderCell.create().setText("Column 2")
                            )
                    )
                    .addParts(
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create().setText("Cell 1.1"),
                                TableCell.create().setText("Cell 1.2")
                            ),
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create().setText("Cell 2.1"),
                                TableCell.create().setText("Cell 2.2")
                            ),
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create().setText("Cell 3.1"),
                                TableCell.create().setText("Cell 3.2")
                            ),
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create().setText("Cell 4.1"),
                                TableCell.create().setText("Cell 4.2")
                            )
                    )
            );
        final Style cellStyle = layoutTextStyle.clone().getLayoutStyle().setFillBackgroundColor(Color.GREEN)
            .setCondition(
                StyleCondition.create(TableCell.class, null)
            );
        final Style headerCellstyle = layoutTextStyle.clone().getLayoutStyle().setFillBackgroundColor(Color.TEAL)
            .setGeometryDetails(null)
            .setCondition(
                StyleCondition.create(TableHeaderCell.class, null)
            );
        htmlFormatter.setStyleService(
            HtmlStyleService
                .create()
                .setWriteStyleInplace(false)
                .addStyles(
                    cellStyle,
                    headerCellstyle
                )
        );
        try (DocumentHolder documentHolder = htmlFormatter.handle(doc1)) {
            final String text = FileUtils.readFileToString(documentHolder.getResource().getFile(), StandardCharsets.UTF_8);
            Assertions.assertTrue(text.contains(Integer.toHexString(Objects.hashCode(cellStyle))));
            Assertions.assertTrue(text.contains(Integer.toHexString(Objects.hashCode(headerCellstyle))));
        }
    }

    /**
     * Test on {@link HtmlFormatter#handle handle} call
     * and proper saving result in {@link FileUrlResource "resource"}
     *
     * @throws Throwable Exception/IOException
     */
    @Test
    public void testSaveToResource() throws Throwable {
        final FileUrlResource resource = new FileUrlResource("testFile.txt");
        final HtmlFormatter htmlFormatter = HtmlFormatter.create().setResource(resource);
        try (DocumentHolder documentHolder = htmlFormatter.handle(doc)) {
            final String text = FileUtils.readFileToString(documentHolder.getResource().getFile(), StandardCharsets.UTF_8);
            Assertions.assertEquals(expected, text);
        }
    }

    /**
     * Test on {@link HtmlFormatter#handle handle} call
     * and proper saving result to "temporary file"
     * with "Cp1251" encoding
     *
     * @throws Throwable Exception
     */
    @Test
    public void testSaveToTmpFile() throws Throwable {
        final HtmlFormatter htmlFormatter = (HtmlFormatter) HtmlFormatter.create().setEncoding("UTF-8");
        try (DocumentHolder documentHolder = htmlFormatter.handle(doc)) {
            final String text = FileUtils.readFileToString(documentHolder.getResource().getFile(), StandardCharsets.UTF_8);
            Assertions.assertEquals(expected, text);
        }
    }

    /**
     * Test on {@link HtmlFormatter#handle handle} call
     * and proper saving result to "outputStream"
     *
     * @throws Throwable IOException/Exception
     */
    @Test
    public void testSaveToStream() throws Throwable {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final HtmlFormatter htmlFormatter = HtmlFormatter.create().setOutputStream(os);
        try (DocumentHolder ignored = htmlFormatter.handle(doc)) {
            final String text = os.toString(StandardCharsets.UTF_8.name());
            Assertions.assertEquals(expected, text);
        }
    }

    /**
     * Test on exception throwing when tag is malformed (h7)
     *
     * @throws Throwable IOException
     */
    @Test
    public void checkTags() throws Throwable {
        final Document doc = Document.create()
            .addPart(Heading.create(1).setDepth((short) 7).setText("test"));
        final HtmlFormatter htmlFormatter = HtmlFormatter.create();
        final Exception e = Assertions.assertThrows(Exception.class, () -> htmlFormatter.handle(doc).close());
        Files.deleteIfExists(htmlFormatter.getResource().getFile().toPath());
        Assertions.assertTrue("Malformed tag: h7".contains(e.getMessage()));
    }

    /**
     * Test on styles writing with old tags
     */
    @Test
    public void checkHtml4StyleTags() throws Throwable {
        final HtmlStyleService htmlStyleService = HtmlStyleService.create().setUseHtml4Tags(true);
        final LayoutStyle interlinearStyle = layoutStyle1.clone().setFillPattern(FillPattern.SOLID_FOREGROUND);
        interlinearStyle
            .setCondition(
                StyleCondition.create(TableRow.class, o -> {
                        if (o instanceof Table) {
                            return true;
                        }
                        if (o instanceof TableRow) {
                            final TableRow row = (TableRow) o;
                            final List<TableCell> cells = (ArrayList<TableCell>) row.getParts();
                            interlinearStyle.setFillForegroundColor(
                                row.getRowIndex() % 2 == 0 ? Color.GREY_25_PERCENT : Color.WHITE);
                            interlinearStyle.setFillBackgroundColor(
                                row.getRowIndex() % 2 == 0 ? Color.GREY_25_PERCENT : Color.WHITE);
                            final Style cellStyle;
                            try {
                                cellStyle = interlinearStyle.clone().setCondition(null);
                                cells.forEach(cell -> cell.setStyle(cellStyle));
                            } catch (CloneNotSupportedException ignored) {
                            }
                            return true;
                        }
                        return false;
                    }
                )
            );
        htmlStyleService.addStyles(interlinearStyle);

        final String expected = "<!doctype html><html><head><meta charset=\"Cp1251\"><title>Test document</title>" +
            "</head><body><h1>Title 1</h1><p><font face=\"monospace\" size=\"10\">paragraph 1</font></p><table " +
            "border=\"1\" cellspacing=\"0\"><tr><th><font face=\"sans-serif\" size=\"14\">column1</font></th><th>" +
            "<font face=\"sans-serif\" size=\"14\">column2 (столбец2)</font></th></tr><tr><td bgcolor=\"#C0C0C0\">1" +
            "</td><td bgcolor=\"#C0C0C0\">2</td></tr><tr><td bgcolor=\"#FFFFFF\">3</td><td bgcolor=\"#FFFFFF\">4" +
            "</td></tr><tr><td bgcolor=\"#C0C0C0\">5</td><td bgcolor=\"#C0C0C0\">6</td></tr></table><h1>" +
            "<font size=\"20\">Test document v.1</font></h1><hr><h1><font face=\"monospace\" size=\"10\">" +
            "Chapter 1</font></h1><h2><font face=\"monospace\" size=\"10\">Chapter 1.1</font></h2><h3><font " +
            "face=\"monospace\" size=\"10\">Chapter 1.1.1</font></h3><p><font face=\"monospace\" size=\"10\">" +
            "This is an example of text in paragraph</font></p><table border=\"1\" cellspacing=\"0\"><tr><th><font " +
            "face=\"sans-serif\" size=\"14\">Column 1</font></th><th><font face=\"sans-serif\" size=\"14\">Column 2" +
            "</font></th></tr><tr><td bgcolor=\"#C0C0C0\">Cell 1.1</td><td bgcolor=\"#C0C0C0\">Cell 1.2</td></tr>" +
            "<tr><td bgcolor=\"#FFFFFF\">Cell 2.1</td><td bgcolor=\"#FFFFFF\">Cell 2.2</td></tr><tr><td " +
            "bgcolor=\"#C0C0C0\">Cell 3.1</td><td bgcolor=\"#C0C0C0\">Cell 3.2</td></tr><tr><td bgcolor=\"#FFFFFF\">" +
            "Cell 4.1</td><td bgcolor=\"#FFFFFF\">Cell 4.2</td></tr></table><h1><font face=\"monospace\" size=\"10\">" +
            "Chapter 2</font></h1><h2><font face=\"monospace\" size=\"10\">Chapter 2.1</font></h2><h3><font " +
            "face=\"monospace\" size=\"10\">Chapter 2.1.1</font></h3><p><font face=\"monospace\" size=\"10\">" +
            "This is an example of text in paragraph 2</font></p><table border=\"1\" cellspacing=\"0\"><tr><th>" +
            "<font face=\"sans-serif\" size=\"14\">Column 1</font></th><th><font face=\"sans-serif\" size=\"14\">" +
            "Column 2</font></th></tr><tr><td bgcolor=\"#C0C0C0\">Cell 1.1</td><td bgcolor=\"#C0C0C0\">Cell 1.2</td>" +
            "</tr><tr><td bgcolor=\"#FFFFFF\">Cell 2.1</td><td bgcolor=\"#FFFFFF\">Cell 2.2</td></tr></table><h1>" +
            "Title 1</h1><p>paragraph 1</p><h2>shifted heading</h2><table border=\"1\" cellspacing=\"0\"><tr>" +
            "<th>столбец1</th><th>column2</th></tr><tr><td bgcolor=\"#C0C0C0\">1</td><td bgcolor=\"#C0C0C0\">2</td>" +
            "</tr><tr><td bgcolor=\"#FFFFFF\">3</td><td bgcolor=\"#FFFFFF\">4 and some escape characters " +
            "(символы) %;;;;;\\/</td></tr><tr><td bgcolor=\"#C0C0C0\">5</td><td bgcolor=\"#C0C0C0\">6</td></tr>" +
            "</table></body></html>";
        final HtmlFormatter htmlFormatter = HtmlFormatter
            .create()
            .setEncoding("Cp1251")
            .setStyleService(htmlStyleService);
        try (DocumentHolder documentHolder = htmlFormatter.handle(doc)) {
            final String text = FileUtils.readFileToString(
                documentHolder.getResource().getFile(),
                Charset.forName("Cp1251")
            );
            Assertions.assertEquals(expected, text);
        }
    }

    @Test
    public void testHtmlParagraph() throws Throwable {
        final Paragraph p = Paragraph.create("test_text");
        final HtmlParagraph htmlParagraph = new HtmlParagraph();
        testHtmlTag(p, htmlParagraph, Paragraph.class);
    }

    @Test
    public void testHtmlTitle() throws Throwable {
        final Title t = Title.create("test_text");
        final HtmlH1 htmlTitle = new HtmlH1();
        testHtmlTag(t, htmlTitle, Title.class);
    }

    @Test
    public void testHtmlFooter() throws Throwable {
        final Footer f = Footer.create("test_text");
        final HtmlFooter htmlFooter = new HtmlFooter();
        testHtmlTag(f, htmlFooter, Footer.class);
    }

    @Test
    public void testHtmlHeading() throws Throwable {
        final Heading h = Heading.create("test_text", 1);
        final HtmlHeading htmlHeading = new HtmlHeading(1);
        testHtmlTag(h, htmlHeading, Heading.class);
    }

    @Test
    public void testHtmlTableCell() throws Throwable {
        final TableCell tc = TableCell.create("test_text");
        final HtmlTableCell htmlTableCell = new HtmlTableCell();
        testHtmlTag(tc, htmlTableCell, TableCell.class);
    }

    @Test
    public void testHtmlTableHeaderCell() throws Throwable {
        final TableHeaderCell thc = TableHeaderCell.create("test_text");
        final HtmlTableHeaderCell htmlTableHeaderCell = new HtmlTableHeaderCell();
        testHtmlTag(thc, htmlTableHeaderCell, TableHeaderCell.class);
    }

    @Test
    public void testHtmlTableRow() throws Throwable {
        final TableCell tc = TableCell.create("test_text");
        final TableRow tr = TableRow.create(tc);
// Case when tag is without parameters
        final HtmlFormatterVisitor htmlFormatterVisitor = new HtmlFormatter();
        final HtmlStyleService styleService = HtmlStyleService.create();
        htmlFormatterVisitor.setStyleService(styleService);
        htmlFormatterVisitor.setOutputStreamWriter(writer);

        htmlFormatterVisitor.visitTableRow(tr);

        writer.flush();
        Assertions.assertEquals("<tr><td>test_text</td></tr>", os.toString());
        os.reset();

// Case of layout style
        tc.setStyle(layoutStyle1);
        htmlFormatterVisitor.visitTableRow(tr);
        writer.flush();

        Assertions.assertEquals(
            "<tr><td style=\"" +
                "border-bottom:double #000000;" +
                "border-collapse:collapse;" +
                "border-left:double #000000;" +
                "border-right:double #000000;" +
                "border-top:double #000000;" +
                "height:15px;" +
                "transform:rotate(10deg);" +
                "width:20px" +
                "\">test_text</td></tr>", os.toString());
        os.reset();

// Case of the named style class
        tc.setStyle(null);         // remove inner style

        layoutStyle1.setCondition(StyleCondition.create(TableCell.class, o -> o instanceof TableCell));
        styleService.addStyles(layoutStyle1);
        styleService.setWriteStyleInplace(false);

        doc.addPart(tr);

        final HtmlFormatter htmlFormatter = HtmlFormatter.create().setStyleService(styleService);
        htmlFormatter.setOutputStream(os);
        final Style divStyle = LayoutStyle.create()
            .setGeometryDetails(
                layoutStyle1.getGeometryDetails()
            )
            .setCondition(
                StyleCondition.create(HtmlDiv.class, o -> true)
            );
        try (DocumentHolder ignored = htmlFormatter.handle(doc)) {
            Assertions.assertTrue(StringUtils.endsWithIgnoreCase(os.toString(),
                "<tr><td class=\"_" + Integer.toHexString(Objects.hashCode(layoutStyle1.setGeometryDetails(null))) + "\">" +
                    "<div class=\"_" + Integer.toHexString(Objects.hashCode(divStyle)) + "\">test_text</div></td></tr>" +
                    "</body></html>"));
        }
    }

    @Test
    public void testHtmlTableHeaderRow() throws Throwable {
        final TableHeaderCell thc = TableHeaderCell.create("test_text");
        final TableHeaderRow thr = TableHeaderRow.create(thc);
// Case when tag is without parameters
        final HtmlFormatterVisitor htmlFormatterVisitor = new HtmlFormatter();
        final HtmlStyleService styleService = HtmlStyleService.create();
        htmlFormatterVisitor.setStyleService(styleService);
        htmlFormatterVisitor.setOutputStreamWriter(writer);

        htmlFormatterVisitor.visitTableHeaderRow(thr);

        writer.flush();

        Assertions.assertEquals("<tr><th>test_text</th></tr>", os.toString());
        os.reset();
// Case of the layout style
        thc.setStyle(layoutStyle1);
        htmlFormatterVisitor.visitTableHeaderRow(thr);
        writer.flush();

        Assertions.assertEquals("<tr><th style=\"" +
            "border-bottom:double #000000;" +
            "border-collapse:collapse;" +
            "border-left:double #000000;" +
            "border-right:double #000000;" +
            "border-top:double #000000;" +
            "height:15px;" +
            "transform:rotate(10deg);" +
            "width:20px" +
            "\">test_text</th></tr>", os.toString());
        os.reset();

// Case of the named class style
        thc.setStyle(null);          // remove inner style
        layoutStyle1.setCondition(StyleCondition.create(TableHeaderCell.class, o -> o instanceof TableHeaderCell));
        styleService.addStyles(layoutStyle1);
        styleService.setWriteStyleInplace(false);

        doc.addPart(thr);

        final HtmlFormatter htmlFormatter = HtmlFormatter.create().setStyleService(styleService);
        htmlFormatter.setOutputStream(os);
        try (DocumentHolder documentHolder = htmlFormatter.handle(doc)) {
            Assertions.assertTrue(StringUtils.endsWithIgnoreCase(os.toString(),
                "<tr><th class=\"_" + Integer.toHexString(Objects.hashCode(layoutStyle1)) + "\">test_text</th></tr>" +
                    "</body></html>"));
        }
    }

    @Test
    public void testHtmlTable() throws Throwable {
        final TableHeaderCell thc = TableHeaderCell.create("test_text1");
        final TableHeaderRow thr = TableHeaderRow.create(thc);
        final TableCell tc = TableCell.create("test_text2");
        final TableRow tr = TableRow.create(tc);

        final Table t = Table.create(thr).addParts(tr);

        // Case when tag is without parameters
        final HtmlFormatterVisitor htmlFormatterVisitor = new HtmlFormatter();
        htmlFormatterVisitor.setOutputStreamWriter(writer);

        final HtmlStyleService styleService = HtmlStyleService.create();
        htmlFormatterVisitor.setStyleService(styleService);

        htmlFormatterVisitor.visitTable(t);

        writer.flush();

        Assertions.assertEquals(
            "<table><tr><th>test_text1</th></tr><tr><td>test_text2</td></tr></table>",
            os.toString()
        );
        os.reset();
        // Case of the layout style
        thc.setStyle(layoutStyle1);

        htmlFormatterVisitor.visitTable(t);

        writer.flush();

        Assertions.assertEquals("<table><tr><th style=\"" +
            "border-bottom:double #000000;" +
            "border-collapse:collapse;" +
            "border-left:double #000000;" +
            "border-right:double #000000;" +
            "border-top:double #000000;" +
            "height:15px;" +
            "transform:rotate(10deg);" +
            "width:20px" +
            "\">test_text1</th></tr><tr><td>test_text2</td></tr></table>", os.toString());
        os.reset();

// Case of the named class style
        thc.setStyle(null);         // remove inner style
        layoutStyle1.setCondition(StyleCondition.create(TableHeaderCell.class, o -> o instanceof TableHeaderCell));
        styleService.addStyles(layoutStyle1);
        styleService.setWriteStyleInplace(false);

        doc.addPart(thr);

        final HtmlFormatter htmlFormatter = HtmlFormatter.create().setStyleService(styleService);
        htmlFormatter.setOutputStream(os);
        try (DocumentHolder documentHolder = htmlFormatter.handle(doc)) {
            Assertions.assertTrue(
                StringUtils.endsWithIgnoreCase(
                    os.toString(),
                    "<tr><th class=\"_" +
                        Integer.toHexString(Objects.hashCode(layoutStyle1)) +
                        "\">test_text1</th></tr></body></html>"
                )
            );
        }
    }

    public void testHtmlTag(
        DocumentItem item,
        HtmlTag baseTag,
        Class<? extends DocumentItem> itemClass
    ) throws Throwable {
// Case when tag is without parameters
        final HtmlFormatterVisitor htmlFormatterVisitor = new HtmlFormatter();
        final HtmlStyleService styleService = HtmlStyleService.create();
        htmlFormatterVisitor.setStyleService(styleService);
        htmlFormatterVisitor.setOutputStreamWriter(writer);
        handleItem(item, htmlFormatterVisitor);
        writer.flush();

        Assertions.assertEquals("<" + baseTag + ">test_text</" + baseTag + ">", os.toString());
        os.reset();
// Case of the layout style
        item.setStyle(layoutStyle1);
        handleItem(item, htmlFormatterVisitor);
        writer.flush();

        Assertions.assertEquals(
            "<" + baseTag + " style=\"" +
                "border-bottom:double #000000;" +
                "border-collapse:collapse;" +
                "border-left:double #000000;" +
                "border-right:double #000000;" +
                "border-top:double #000000;" +
                "height:15px;" +
                "transform:rotate(10deg);" +
                "width:20px" +
                "\">test_text</" + baseTag + ">", os.toString());
        os.reset();
// Case of the text style
        item.setStyle(textStyle1);
        handleItem(item, htmlFormatterVisitor);
        writer.flush();

        Assertions.assertEquals(
            "<" + baseTag + " style=\"" +
                "font-family:" + textStyle1.getFontNameResource() + ",monospace;" +
                "font-size:14pt;" +
                "font-weight:bold\">test_text</" + baseTag + ">", os.toString());
        os.reset();
// Case of the named style class
        item.setStyle(null);         // remove inner style
        layoutStyle1.setCondition(StyleCondition.create(itemClass, o -> o.getClass().equals(itemClass)));
        styleService.addStyles(layoutStyle1);
        styleService.setWriteStyleInplace(false);

        doc.addPart(item);


        final ByteArrayOutputStream osRes = new ByteArrayOutputStream();

        final HtmlFormatter htmlFormatter = HtmlFormatter.create().setStyleService(styleService);
        htmlFormatter.setOutputStream(osRes);
        final Style divStyle = LayoutStyle.create()
            .setGeometryDetails(
                layoutStyle1.getGeometryDetails()
            )
            .setCondition(
                StyleCondition.create(HtmlDiv.class, o -> true)
            );
        try (DocumentHolder ignored = htmlFormatter.handle(doc)) {
            if (baseTag.getClass().equals(HtmlTableCell.class)) {
                Assertions.assertTrue(StringUtils.endsWithIgnoreCase(osRes.toString(),
                    "<" + baseTag + " class=\"_" +
                        Integer.toHexString(Objects.hashCode(layoutStyle1.setGeometryDetails(null))) +
                        "\"><div class=\"_" + Integer.toHexString(Objects.hashCode(divStyle)) + "\">test_text</div></" + baseTag + ">" +
                        "</body></html>"));
            } else {
                Assertions.assertTrue(StringUtils.endsWithIgnoreCase(osRes.toString(),
                    "<" + baseTag + " class=\"_" +
                        Integer.toHexString(Objects.hashCode(layoutStyle1)) +
                        "\">test_text</" + baseTag + ">" +
                        "</body></html>"));
            }
        }
    }

    public void handleItem(DocumentItem item, HtmlFormatterVisitor htmlFormatterVisitor) throws Throwable {
        if (item instanceof Paragraph) {
            htmlFormatterVisitor.visitParagraph((Paragraph) item);
        } else if (item instanceof Title) {
            htmlFormatterVisitor.visitTitle((Title) item);
        } else if (item instanceof Heading) {
            htmlFormatterVisitor.visitHeading((Heading) item);
        } else if (item instanceof Table) {
            htmlFormatterVisitor.visitTable((Table) item);
        } else if (item instanceof TableHeaderCell) {
            htmlFormatterVisitor.visitTableHeaderCell((TableHeaderCell) item);
        } else if (item instanceof TableHeaderRow) {
            htmlFormatterVisitor.visitTableHeaderRow((TableHeaderRow) item);
        } else if (item instanceof TableCell) {
            htmlFormatterVisitor.visitTableCell((TableCell) item);
        } else if (item instanceof TableRow) {
            htmlFormatterVisitor.visitTableRow((TableRow) item);
        } else if (item instanceof Footer) {
            htmlFormatterVisitor.visitFooter((Footer) item);
        }
    }
}