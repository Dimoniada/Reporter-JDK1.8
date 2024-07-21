package com.reporter.formatter.html;

import com.google.common.base.Objects;
import com.model.domain.Document;
import com.model.domain.Footer;
import com.model.domain.Heading;
import com.model.domain.Paragraph;
import com.model.domain.Picture;
import com.model.domain.Table;
import com.model.domain.TableCell;
import com.model.domain.TableHeaderCell;
import com.model.domain.TableHeaderRow;
import com.model.domain.TableRow;
import com.model.domain.Title;
import com.model.domain.core.DocumentItem;
import com.model.domain.style.LayoutStyle;
import com.model.domain.style.Style;
import com.model.domain.style.StyleCondition;
import com.model.domain.style.TextStyle;
import com.model.domain.style.constant.Color;
import com.model.domain.style.constant.FillPattern;
import com.model.domain.style.constant.HorAlignment;
import com.model.domain.style.constant.PictureFormat;
import com.model.domain.style.constant.VertAlignment;
import com.model.domain.style.geometry.Geometry;
import com.model.domain.style.geometry.GeometryDetails;
import com.model.formatter.DocumentHolder;
import com.model.formatter.html.HtmlFormatter;
import com.model.formatter.html.HtmlFormatterVisitor;
import com.model.formatter.html.style.HtmlLayoutTextStyle;
import com.model.formatter.html.style.HtmlStyleService;
import com.model.formatter.html.tag.HtmlDiv;
import com.model.formatter.html.tag.HtmlFooter;
import com.model.formatter.html.tag.HtmlH1;
import com.model.formatter.html.tag.HtmlHeading;
import com.model.formatter.html.tag.HtmlParagraph;
import com.model.formatter.html.tag.HtmlPicture;
import com.model.formatter.html.tag.HtmlTableCell;
import com.model.formatter.html.tag.HtmlTableHeaderCell;
import com.model.formatter.html.tag.HtmlTag;
import com.reporter.formatter.BaseDocument;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.WritableResource;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HtmlFormatterTest extends BaseDocument {

    public static final String testString = "test_text";
    public static final String testPicture;
    public static final byte[] testPictureData;

    static {
        final String imageName = "pic/pic.jpg";

        final URL url = HtmlFormatterTest.class.getClassLoader().getResource(imageName);
        Assertions.assertNotNull(url);
        final WritableResource resource;
        try {
            resource = new PathResource(url.toURI());
            testPictureData = IOUtils.toByteArray(resource.getInputStream());
            testPicture = String.format(
                "data:image/%s;base64,%s",
                PictureFormat.JPG.toString().toLowerCase(Locale.ENGLISH),
                Base64.getEncoder().encodeToString(testPictureData)
            );
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

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
        styleService.setWriteStyleInTag(false);

        final Document doc1 = Document.create().setLabel("doc1")
            .addParts(
                Heading.create(1).setText("testHeading"),
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
            Assertions.assertEquals(1, StringUtils.countOccurrencesOf(
                text,
                "{color:#00FF00;" +
                    "font-family:Brush Script MT,monospace;" +
                    "font-size:35pt;" +
                    "font-style:italic;" +
                    "font-weight:bold}"
            ));
            Assertions.assertEquals(1, StringUtils.countOccurrencesOf(
                text,
                "{color:#FF0000;" +
                    "font-family:Gill Sans,monospace;" +
                    "font-size:15pt}"
            ));
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
                                TableHeaderCell.create("Column 1"),
                                TableHeaderCell.create("Column 2")
                            )
                    )
                    .addParts(
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create("Cell 1.1"),
                                TableCell.create("Cell 1.2")
                            ),
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create("Cell 2.1"),
                                TableCell.create("Cell 2.2")
                            ),
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create("Cell 3.1"),
                                TableCell.create("Cell 3.2")
                            ),
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create("Cell 4.1"),
                                TableCell.create("Cell 4.2")
                            )
                    )
            );
        final Style cellStyle = layoutTextStyle.clone().getLayoutStyle().setFillBackgroundColor(Color.GREEN)
            .setCondition(
                StyleCondition.create(TableCell.class, null)
            );
        final Style headerCellstyle = layoutTextStyle.clone().getLayoutStyle().setFillBackgroundColor(Color.TEAL)
            .setCondition(
                StyleCondition.create(TableHeaderCell.class, null)
            );
        htmlFormatter.setStyleService(
            HtmlStyleService
                .create()
                .setWriteStyleInTag(false)
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

    @Test
    public void testSaveTableWithPicture() throws Throwable {
        final String imageName = "pic/pic.jpg";
        final URL url = getClass().getClassLoader().getResource(imageName);
        Assertions.assertNotNull(url);
        final WritableResource resource = new PathResource(url.toURI());

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
                                TableHeaderCell.create("Column 1"),
                                TableHeaderCell.create("Column 2")
                                    .setStyle(
                                        LayoutStyle.create()
                                            .setGeometryDetails(
                                                GeometryDetails.create()
                                                    .setWidth(Geometry.create().add("html", "120px"))
                                                    .setHeight(Geometry.create().add("html", "60px"))
                                            )
                                            .setVertAlignment(VertAlignment.TOP)
                                            .setHorAlignment(HorAlignment.CENTER)
                                    )
                            )
                    )
                    .addParts(
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create(IOUtils.toByteArray(resource.getInputStream()))
                                    .setStyle(
                                        LayoutStyle.create()
                                            .setGeometryDetails(
                                                GeometryDetails.create()
                                                    .setScaleX(Geometry.create().add("html", 0.7f))
                                                    .setScaleY(Geometry.create().add("html", 0.7f))
                                                    .setWidth(Geometry.create().add("html", "80px"))
                                                    .setHeight(Geometry.create().add("html", "48px"))
                                                    .setAngle(Geometry.create().add("html", "45deg"))
                                            )
                                    ),
                                TableCell.create("Cell 1.2")
                            ),
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create("Cell 2.1"),
                                TableCell.create("Cell 2.2")
                            ),
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create("Cell 3.1"),
                                TableCell.create("Cell 3.2")
                            ),
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create("Cell 4.1"),
                                TableCell.create("Cell 4.2")
                            )
                    )
                    .setStyle(HtmlLayoutTextStyle.create(true))
            );

        final Style cellStyle = layoutTextStyle.clone().getLayoutStyle().setFillBackgroundColor(Color.GREEN)
            .setCondition(
                StyleCondition.create(TableCell.class, null)
            );
        final Style headerCellstyle = layoutTextStyle.clone().getLayoutStyle().setFillBackgroundColor(Color.TEAL)
            .setCondition(
                StyleCondition.create(TableHeaderCell.class, null)
            );
        htmlFormatter.setStyleService(
            HtmlStyleService
                .create()
                .setWriteStyleInTag(false)
                .addStyles(
                    headerCellstyle,
                    cellStyle
                )
        );
        try (DocumentHolder documentHolder = htmlFormatter.handle(doc1)) {
            final String text =
                FileUtils.readFileToString(documentHolder.getResource().getFile(), StandardCharsets.UTF_8);
            Assertions.assertTrue(text.contains("Cell 1.2"));
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
            final String text =
                FileUtils.readFileToString(documentHolder.getResource().getFile(), StandardCharsets.UTF_8);
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
            .addPart(Heading.create("test", 7));
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
        final Paragraph p = Paragraph.create(testString);
        final HtmlParagraph htmlParagraph = new HtmlParagraph();
        testHtmlTag(htmlParagraph, p, testString, Paragraph.class);
    }

    @Test
    public void testHtmlTitle() throws Throwable {
        final Title t = Title.create(testString);
        final HtmlH1 htmlTitle = new HtmlH1();
        testHtmlTag(htmlTitle, t, testString, Title.class);
    }

    @Test
    public void testHtmlPicture() throws Throwable {
        final Picture p = Picture.create(testPictureData, PictureFormat.JPG);
        final HtmlPicture htmlPicture = new HtmlPicture();
        testHtmlTag(htmlPicture, p, testPicture, Picture.class);
    }

    @Test
    public void testHtmlFooter() throws Throwable {
        final Footer f = Footer.create(testString);
        final HtmlFooter htmlFooter = new HtmlFooter();
        testHtmlTag(htmlFooter, f, testString, Footer.class);
    }

    @Test
    public void testHtmlHeading() throws Throwable {
        final Heading h = Heading.create(testString, 1);
        final HtmlHeading htmlHeading = new HtmlHeading(1);
        testHtmlTag(htmlHeading, h, testString, Heading.class);
    }

    @Test
    public void testHtmlTableCell() throws Throwable {
        final TableCell tc = TableCell.create(testString);
        final HtmlTableCell htmlTableCell = new HtmlTableCell();
        testHtmlTag(htmlTableCell, tc, testString, TableCell.class);
    }

    @Test
    public void testHtmlTableHeaderCell() throws Throwable {
        final TableHeaderCell thc = TableHeaderCell.create(testString);
        final HtmlTableHeaderCell htmlTableHeaderCell = new HtmlTableHeaderCell();
        testHtmlTag(htmlTableHeaderCell, thc, testString, TableHeaderCell.class);
    }

    @Test
    public void testHtmlTableRow() throws Throwable {
        final TableCell tc = TableCell.create(testString);
        final TableRow tr = TableRow.create(tc);
// Case when tag is without parameters
        final HtmlFormatterVisitor htmlFormatterVisitor = new HtmlFormatter();
        final HtmlStyleService styleService = HtmlStyleService.create();
        htmlFormatterVisitor.setStyleService(styleService);
        htmlFormatterVisitor.setOutputStreamWriter(writer);

        htmlFormatterVisitor.visitTableRow(tr);

        writer.flush();
        Assertions.assertEquals("<tr><td>" + testString + "</td></tr>", os.toString());
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
                "\">" + testString + "</td></tr>", os.toString());
        os.reset();

// Case of the named style class
        tc.setStyle(null);         // remove inner style

        layoutStyle1.setCondition(StyleCondition.create(TableCell.class, o -> o instanceof TableCell));
        styleService.addStyles(layoutStyle1);
        styleService.setWriteStyleInTag(false);

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
            Assertions.assertTrue(StringUtils.endsWithIgnoreCase(
                os.toString(),
                "<tr><td class=\"_" + Integer.toHexString(Objects.hashCode(layoutStyle1.setGeometryDetails(null))) + "\">" +
                    "<div class=\"_" + Integer.toHexString(Objects.hashCode(divStyle)) + "\">" + testString + "</div></td></tr>" +
                    "</body></html>"
            ));
        }
    }

    @Test
    public void testHtmlTableHeaderRow() throws Throwable {
        final TableHeaderCell thc = TableHeaderCell.create(testString);
        final TableHeaderRow thr = TableHeaderRow.create(thc);
// Case when tag is without parameters
        final HtmlFormatterVisitor htmlFormatterVisitor = new HtmlFormatter();
        final HtmlStyleService styleService = HtmlStyleService.create();
        htmlFormatterVisitor.setStyleService(styleService);
        htmlFormatterVisitor.setOutputStreamWriter(writer);

        htmlFormatterVisitor.visitTableHeaderRow(thr);

        writer.flush();

        Assertions.assertEquals("<tr><th>" + testString + "</th></tr>", os.toString());
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
            "\">" + testString + "</th></tr>", os.toString());
        os.reset();

// Case of the named class style
        thc.setStyle(null);          // remove inner style
        layoutStyle1.setCondition(StyleCondition.create(TableHeaderCell.class, o -> o instanceof TableHeaderCell));
        styleService.addStyles(layoutStyle1);
        styleService.setWriteStyleInTag(false);

        doc.addPart(thr);

        final HtmlFormatter htmlFormatter = HtmlFormatter.create().setStyleService(styleService);
        htmlFormatter.setOutputStream(os);
        try (DocumentHolder ignored = htmlFormatter.handle(doc)) {
            final Style htmlDivStyle = styleService.getCustomTableCellDivStyle(new HtmlDiv());
            Assertions.assertTrue(StringUtils.endsWithIgnoreCase(
                os.toString(),
                "<tr><th class=\"_" + Integer.toHexString(Objects.hashCode(layoutStyle1))
                    + "\"><div class=\"_"
                    + Integer.toHexString(Objects.hashCode(htmlDivStyle))
                    + "\">"
                    + testString + "</div></th></tr>" +
                    "</body></html>"
            ));
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
        styleService.setWriteStyleInTag(false);

        doc.addPart(thr);

        final HtmlFormatter htmlFormatter = HtmlFormatter.create().setStyleService(styleService);
        htmlFormatter.setOutputStream(os);
        try (DocumentHolder ignored = htmlFormatter.handle(doc)) {
            final Style htmlDivStyle = styleService.getCustomTableCellDivStyle(new HtmlDiv());
            Assertions.assertTrue(
                StringUtils.endsWithIgnoreCase(
                    os.toString(),
                    "<tr><th class=\"_"
                        + Integer.toHexString(Objects.hashCode(layoutStyle1))
                        + "\"><div class=\"_"
                        + Integer.toHexString(Objects.hashCode(htmlDivStyle))
                        + "\">test_text1</div></th></tr></body></html>"
                )
            );
        }
    }

    public void testHtmlTag(
        HtmlTag baseTag,
        DocumentItem item,
        String testData,
        Class<? extends DocumentItem> itemClass
    ) throws Throwable {
// Case when tag is without parameters
        final HtmlFormatterVisitor htmlFormatterVisitor = new HtmlFormatter();
        final HtmlStyleService styleService = HtmlStyleService.create();
        htmlFormatterVisitor.setStyleService(styleService);
        htmlFormatterVisitor.setOutputStreamWriter(writer);
        handleItem(item, htmlFormatterVisitor);
        writer.flush();

        Assertions.assertTrue(os.toString().contains(baseTag.toString()));
        Assertions.assertTrue(os.toString().contains(testData));
        os.reset();
// Case of the layout style
        item.setStyle(layoutStyle1);
        handleItem(item, htmlFormatterVisitor);
        writer.flush();

        if (!(item instanceof Picture)) {
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
                    "\">" + testString + "</" + baseTag + ">", os.toString());
        } else {
            Assertions.assertEquals(
                "<" + baseTag + " src=\"" + testPicture + "\" style=\"" +
                    "border-bottom:double #000000;" +
                    "border-collapse:collapse;" +
                    "border-left:double #000000;" +
                    "border-right:double #000000;" +
                    "border-top:double #000000;" +
                    "height:15px;" +
                    "transform:rotate(10deg);" +
                    "width:20px" +
                    "\"></" + baseTag + ">", os.toString());
        }
        os.reset();
// Case of the text style
        item.setStyle(textStyle1);
        handleItem(item, htmlFormatterVisitor);
        writer.flush();

        if (item instanceof Picture) {
            Assertions.assertEquals(
                "<" + baseTag + " src=\"" + testPicture + "\" style=\"" +
                    "font-family:" + textStyle1.getFontNameResource() + ",monospace;" +
                    "font-size:14pt;" +
                    "font-weight:bold\"></" + baseTag + ">", os.toString());
        } else {
            Assertions.assertEquals(
                "<" + baseTag + " style=\"" +
                    "font-family:" + textStyle1.getFontNameResource() + ",monospace;" +
                    "font-size:14pt;" +
                    "font-weight:bold\">" + testString + "</" + baseTag + ">", os.toString());
        }
        os.reset();
// Case of the named style class
        item.setStyle(null);         // remove inner style
        layoutStyle1.setCondition(StyleCondition.create(itemClass, o -> o.getClass().equals(itemClass)));
        styleService.addStyles(layoutStyle1);
        styleService.setWriteStyleInTag(false);

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
                Assertions.assertTrue(StringUtils.endsWithIgnoreCase(
                    osRes.toString(),
                    "<" + baseTag + " class=\"_" +
                        Integer.toHexString(Objects.hashCode(layoutStyle1.setGeometryDetails(null))) +
                        "\"><div class=\"_" + Integer.toHexString(Objects.hashCode(divStyle)) + "\">"
                        + testString + "</div></" + baseTag + ">" +
                        "</body></html>"
                ));
            } else {
                if (item instanceof Picture) {
                    Assertions.assertTrue(StringUtils.endsWithIgnoreCase(
                        osRes.toString(),
                        "<" + baseTag + " class=\"_" +
                            Integer.toHexString(Objects.hashCode(layoutStyle1)) +
                            "\"" + " src=\"" + testPicture + "\"></" + baseTag + ">" +
                            "</body></html>"
                    ));
                    return;
                }
                if (item instanceof TableCell || item instanceof TableHeaderCell) {
                    final Style htmlDivStyle = styleService.getCustomTableCellDivStyle(new HtmlDiv());
                    Assertions.assertTrue(StringUtils.endsWithIgnoreCase(
                        osRes.toString(),
                        "<" + baseTag + " class=\"_"
                            + Integer.toHexString(Objects.hashCode(layoutStyle1))
                            + "\"><div class=\"_"
                            + Integer.toHexString(Objects.hashCode(htmlDivStyle))
                            + "\">"
                            + testString + "</div></" + baseTag + ">" +
                            "</body></html>"
                    ));
                    return;
                }
                Assertions.assertTrue(StringUtils.endsWithIgnoreCase(
                    osRes.toString(),
                    "<" + baseTag + " class=\"_"
                        + Integer.toHexString(Objects.hashCode(layoutStyle1))
                        + "\">"
                        + testString + "</" + baseTag + ">" +
                        "</body></html>"
                ));
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
        } else if (item instanceof Picture) {
            htmlFormatterVisitor.visitPicture((Picture) item);
        }
    }
}