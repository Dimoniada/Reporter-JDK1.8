package com.reporter.formatter.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutputStream;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import com.model.domain.Document;
import com.model.domain.Footer;
import com.model.domain.Heading;
import com.model.domain.Paragraph;
import com.model.domain.Picture;
import com.model.domain.Separator;
import com.model.domain.Table;
import com.model.domain.TableCell;
import com.model.domain.TableHeaderCell;
import com.model.domain.TableHeaderRow;
import com.model.domain.TableRow;
import com.model.domain.Title;
import com.model.domain.core.DocumentItem;
import com.model.domain.style.BorderStyle;
import com.model.domain.style.FontFamilyStyle;
import com.model.domain.style.LayoutStyle;
import com.model.domain.style.LayoutTextStyle;
import com.model.domain.style.StyleService;
import com.model.domain.style.TextStyle;
import com.model.domain.style.constant.BorderWeight;
import com.model.domain.style.constant.Color;
import com.model.domain.style.constant.FillPattern;
import com.model.domain.style.constant.HorAlignment;
import com.model.domain.style.constant.PictureFormat;
import com.model.domain.style.constant.VertAlignment;
import com.model.domain.style.geometry.Geometry;
import com.model.domain.style.geometry.GeometryDetails;
import com.model.formatter.DocumentHolder;
import com.model.formatter.pdf.PdfFormatter;
import com.model.formatter.pdf.style.PdfStyleService;
import com.model.utils.LocalizedNumberUtils;
import com.reporter.formatter.BaseDocument;
import com.reporter.formatter.pdf.utils.ImageEventListener;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.WritableResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

class PdfFormatterTest extends BaseDocument {
    public static final String expected = "Title 1\n" +
        "paragraph 1\n" +
        "column1 column2 (столбец2)\n" +
        "1,000 2,000\n" +
        "3,000 4,000\n" +
        "5,000 6,000\n" +
        "Test document v.1\n" +
        "Chapter 1\n" +
        "Chapter 1.1\n" +
        "Chapter 1.1.1\n" +
        "This is an example of text in paragraph\n" +
        "Column 1 Column 2\n" +
        "Cell 1.1 Cell 1.2\n" +
        "Cell 2.1 Cell 2.2\n" +
        "Cell 3.1 Cell 3.2\n" +
        "Cell 4.1 Cell 4.2\n" +
        "Chapter 2\n" +
        "Chapter 2.1\n" +
        "Chapter 2.1.1\n" +
        "This is an example of text in paragraph 2\n" +
        "Column 1 Column 2\n" +
        "Cell 1.1 Cell 1.2\n" +
        "Cell 2.1 Cell 2.2\n" +
        "Title 1\n" +
        "paragraph 1\n" +
        "shifted heading\n" +
        "1 column2\n" +
        "1,000 2,000\n" +
        "3,000 4 and some escape characters (символы) %;;;;;\\/\n" +
        "5,000 6,000";

    public StyleService styleService;
    public Document pdfDoc;

    @BeforeEach
    public void initDoc() throws Exception {
        super.initDoc();
        styleService = PdfStyleService.create("Cp1251");

        final LayoutTextStyle titleStyle1 =
            LayoutTextStyle.create(
                TextStyle.create()
                    .setColor(Color.TEAL)
                    .setBold(true)
                    .setFontSize((short) 14)
                    .setFontFamilyStyle(FontFamilyStyle.SANS_SERIF)
                    .setFontNameResource("arial"),
                LayoutStyle.create()
                    .setGeometryDetails(
                        GeometryDetails.create()
                            .setWidth(Geometry.create().add("pdf", 40f))
                    )
            );
        final TextStyle paragraphStyle1 =
            TextStyle.create()
                .setColor(Color.BLUE)
                .setItalic(true)
                .setFontSize((short) 10)
                .setFontFamilyStyle(FontFamilyStyle.MONOSPACED);

        final TextStyle titleStyle2 =
            TextStyle.create()
                .setColor(Color.RED)
                .setBold(true)
                .setFontSize((short) 20)
                .setFontFamilyStyle(FontFamilyStyle.SANS_SERIF)
                .setFontNameResource("helvetica");
        final TextStyle paragraphStyle2 =
            TextStyle.create()
                .setColor(Color.GREY_25_PERCENT)
                .setItalic(true)
                .setFontSize((short) 12)
                .setFontFamilyStyle(FontFamilyStyle.SANS_SERIF)
                .setFontNameResource("helvetica");

        final TextStyle tableLabelStyle3 =
            TextStyle.create()
                .setColor(Color.GREEN)
                .setBold(true)
                .setFontSize((short) 12)
                .setFontFamilyStyle(FontFamilyStyle.SANS_SERIF)
                .setFontNameResource("tahoma");
        final TextStyle tableCellsStyle3 =
            TextStyle.create()
                .setColor(Color.ORANGE)
                .setFontSize((short) 8)
                .setFontFamilyStyle(FontFamilyStyle.SERIF)
                .setFontNameResource("times");
        final TextStyle tableHeaderCellStyle4 =
            TextStyle.create()
                .setColor(Color.RED_DARK)
                .setFontSize((short) 13)
                .setFontFamilyStyle(FontFamilyStyle.SANS_SERIF)
                .setFontNameResource("verdana");
        final TextStyle tableHeaderCellStyle5 =
            TextStyle.create()
                .setColor(Color.RED)
                .setFontSize((short) 14)
                .setFontFamilyStyle(FontFamilyStyle.SANS_SERIF)
                .setFontNameResource("verdana");

        final LayoutTextStyle cellStyle6 =
            LayoutTextStyle.create(
                TextStyle.create()
                    .setColor(Color.ORANGE)
                    .setFontSize((short) 8),
                LayoutStyle.create()
                    .setFillBackgroundColor(Color.GREY_25_PERCENT)
                    .setFillPattern(FillPattern.SOLID_FOREGROUND)
            );

        pdfDoc = Document.create()
            .setLabel("Тестовый pdf документ")
            .setAuthor("A1 Systems")
            .setDescription("описание темы/содержания файла")
            .addParts(
                Title.create()
                    .setText("Смысл сайта (шрифт arial,14)").setStyle(titleStyle1),
                Separator.create(BorderStyle.create(Color.TEAL, BorderWeight.THIN)),
                Heading.create(0)
                    .setText("Шрифт courierNew,10:").setStyle(paragraphStyle1),
                Paragraph.create()
                    .setText("Сайт рыбатекст поможет дизайнеру, верстальщику, вебмастеру сгенерировать" +
                        "несколько абзацев более менее осмысленного текста рыбы на русском языке, а начинающему " +
                        "оратору отточить навык публичных выступлений в домашних условиях. При создании генератора " +
                        "мы использовали небезизвестный универсальный код речей. Текст генерируется абзацами " +
                        "случайным образом от двух до десяти предложений в абзаце, что позволяет сделать текст более" +
                        " привлекательным и живым для визуально-слухового восприятия. По своей сути рыбатекст " +
                        "является альтернативой традиционному lorem ipsum, который вызывает у некторых людей " +
                        "недоумение при попытках прочитать рыбу текст. В отличии от lorem ipsum, текст рыба на " +
                        "русском языке наполнит любой макет непонятным смыслом и придаст неповторимый колорит " +
                        "советских времен. ").setStyle(paragraphStyle1),
                Separator.create(
                    BorderStyle.create(Color.GREEN_DARK, BorderWeight.THIN)
                ),
                Heading.create(1)
                    .setText("Подзаголовок (шрифт helvetica,16):").setStyle(titleStyle2),
                Paragraph.create()
                    .setText("helvetica,16,italic: Lorem ipsum dolor sit amet, consectetur adipiscing " +
                        "elit. Morbi " +
                        "sollicitudin lacus augue, ut tristique justo feugiat vel. Quisque id vestibulum enim," +
                        "eget convallis dolor. Vivamus dapibus hendrerit sodales. Fusce nisi orci, ultrices eget " +
                        "finibus ut, aliquam in dui. Etiam tincidunt enim in mi interdum tristique nec ut sem. Nunc " +
                        "gravida feugiat massa a sodales. Integer viverra molestie tellus. Praesent fermentum libero " +
                        "ut ultricies posuere. Sed ultrices pellentesque orci nec imperdiet. Praesent placerat " +
                        "sollicitudin semper. Praesent id mauris porta, eleifend mi eget, ultrices sapien. " +
                        "Suspendisse ultrices mi a volutpat ultricies. Nunc fringilla, velit vitae porttitor euismod," +
                        " ante erat pulvinar magna, ut tempor orci lacus quis lacus. Fusce suscipit lorem sed orci" +
                        " porttitor tempor. Morbi porta ante nulla, nec faucibus nunc volutpat quis. Nunc et nisi " +
                        "blandit, euismod felis id, malesuada mauris. ").setStyle(paragraphStyle2),
                Table.create()
                    .setLabel("Таблица (шрифт этой строки - tahoma,12):")
                    .setTableHeaderRow(
                        TableHeaderRow
                            .create().addParts(
                                TableHeaderCell.create("verdana,13.").setStyle(tableHeaderCellStyle4),
                                TableHeaderCell.create("verdana,14.").setStyle(tableHeaderCellStyle5)
                            )
                    )
                    .addParts(
                        TableRow.create()
                            .addParts(
                                TableCell.create("1"),
                                TableCell.create("2123123")
                            ),
                        TableRow.create()
                            .addParts(
                                TableCell.create("3.14"),
                                TableCell.create("4")
                            ),
                        TableRow.create()
                            .addParts(
                                TableCell.create("all cells with times,8 font").setStyle(cellStyle6),
                                TableCell.create("6")
                            )
                    )
                    .spreadStyleToParts(tableCellsStyle3)
                    .setStyle(tableLabelStyle3)
            );
    }

    @Test
    public void testLocalizeNumber() {
        Assertions.assertDoesNotThrow(() -> LocalizedNumberUtils.localizeNumber("123", null, null));
    }

    @Test
    public void testPdfDocumentCreation() throws Throwable {
        final PdfFormatter pdfFormatter = (PdfFormatter) PdfFormatter.create()
            .setEncoding("Cp1251");
        pdfFormatter.setFileName("pdfDocument");

        pdfFormatter.getStyleService().setFontService(fontService);

        Assertions.assertDoesNotThrow(() -> pdfFormatter.handle(pdfDoc).close());
    }

    /**
     * Tests {@link PdfFormatter#handle handle} call
     * and proper saving table in "test_file.pdf",
     * checks saved table as a text
     *
     * @throws Throwable Exception/IOException
     */
    @Test
    public void testSaveTableToNewFile() throws Throwable {
        final PdfFormatter pdfFormatter = PdfFormatter.create()
            .setEncoding("Cp1251")
            .setStyleService(styleService);
        pdfFormatter.setFileName("test_file");
        Assertions.assertEquals("test_file", pdfFormatter.getFileName());

        try (DocumentHolder documentHolder = pdfFormatter.handle(doc)) {
            if (Files.exists(documentHolder.getResource().getFile().toPath())) {
                final PdfReader pdfReader = new PdfReader(documentHolder.getResource().getFile());
                final PdfDocument doc1 = new PdfDocument(pdfReader);
                final ITextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
                final String currentText = PdfTextExtractor.getTextFromPage(doc1.getPage(2), strategy);
                doc1.close();
                pdfReader.close();
                Assertions.assertEquals(expected, currentText);
            }
        }
    }

    @Test
    public void testSaveParagraphWithRotationToNewFile() throws Throwable {
        final PdfFormatter pdfFormatter = PdfFormatter.create()
            .setEncoding("Cp1251")
            .setStyleService(styleService);
        pdfFormatter.setFileName("test_file");
        Assertions.assertEquals("test_file", pdfFormatter.getFileName());

        final Document document = Document.create(
            Paragraph.create("Test paragraph")
                .setStyle(
                    LayoutStyle.create()
                        .setGeometryDetails(
                            GeometryDetails.create()
                                .setAngle(Geometry.create().add("pdf", 45f))
                                .setTransformCenter(Geometry.<Map.Entry<HorAlignment, VertAlignment>>create()
                                    .add(
                                        "pdf",
                                        new AbstractMap.SimpleEntry<>(HorAlignment.CENTER, VertAlignment.CENTER)
                                    )
                                )
                                .setWidth(Geometry.create().add("pdf", 100f))
                        )
                ),
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
                                .setStyle(
                                    LayoutStyle.create()
                                        .setGeometryDetails(
                                            GeometryDetails.create()
                                                .setAngle(Geometry.create().add("pdf", 45f))
                                                .setTransformCenter(Geometry.<Map.Entry<HorAlignment, VertAlignment>>create()
                                                    .add(
                                                        "pdf",
                                                        new AbstractMap.SimpleEntry<>(HorAlignment.CENTER, VertAlignment.CENTER)
                                                    )
                                                )
                                                .setWidth(Geometry.create().add("pdf", 40f))
                                        )
                                )
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

        try (DocumentHolder documentHolder = pdfFormatter.handle(document)) {
            if (Files.exists(documentHolder.getResource().getFile().toPath())) {
                final PdfReader pdfReader = new PdfReader(documentHolder.getResource().getFile());
                final PdfDocument doc1 = new PdfDocument(pdfReader);
                final ITextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
                final String currentText = PdfTextExtractor.getTextFromPage(doc1.getPage(1), strategy);
                doc1.close();
                pdfReader.close();
                Assertions.assertEquals("Column 1 Column 2\n" +
                    "Cell 1.1 Cell 1.2\n" +
                    "Cell 2.1\n" +
                    "Cell 2.2\n" +
                    "Cell 3.1 Cell 3.2\n" +
                    "Cell 4.1 Cell 4.2\n" +
                    "Test paragraph", currentText);
            }
        }
    }

    @Test
    public void testSaveTableToResource() throws Throwable {
        final PdfFormatter pdfFormatter = PdfFormatter.create()
            .setEncoding("Cp1251").setStyleService(styleService);
        final FileUrlResource resource = new FileUrlResource("testFile");
        pdfFormatter.setResource(resource);

        Assertions.assertEquals(resource, pdfFormatter.getResource());

        try (DocumentHolder documentHolder = pdfFormatter.handle(doc)) {
            if (Files.exists(documentHolder.getResource().getFile().toPath())) {
                final PdfReader pdfReader = new PdfReader(documentHolder.getResource().getFile());
                final PdfDocument doc1 = new PdfDocument(pdfReader);
                final ITextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
                final String currentText = PdfTextExtractor.getTextFromPage(doc1.getPage(2), strategy);
                doc1.close();
                pdfReader.close();
                Assertions.assertEquals(expected, currentText);
            }
        }
    }

    @Test
    public void testSaveTableToStream() throws Throwable {
        final PdfFormatter pdfFormatter = PdfFormatter.create()
            .setEncoding("Cp1251").setStyleService(styleService);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        pdfFormatter.setOutputStream(os);

        Assertions.assertEquals(os, pdfFormatter.getOutputStream());

        try (DocumentHolder ignored = pdfFormatter.handle(doc)) {
            if (os.size() != 0) {
                final PdfReader pdfReader = new PdfReader(new ByteArrayInputStream(os.toByteArray()));
                final PdfDocument doc1 = new PdfDocument(pdfReader);
                final ITextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
                final String currentText = PdfTextExtractor.getTextFromPage(doc1.getPage(2), strategy);
                doc1.close();
                pdfReader.close();
                Assertions.assertEquals(expected, currentText);
            }
        }
    }

    @Test
    public void testFormatterProperties() throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final PdfWriter writer = new PdfWriter(os);
        final PdfFormatter pdfFormatter = PdfFormatter.create().setStyleService(styleService);

        final DecimalFormat df = new DecimalFormat("‰00");

        final PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new PdfOutputStream(os)));
        final com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);

        pdfFormatter
            .setWriter(writer)
            .setPdf(pdfDoc)
            .setEncoding("ISO-8859-1")
            .setDecimalFormat(df)
            .setDocument(document)
            .setOutputStream(os);
        Assertions.assertEquals(os, pdfFormatter.getOutputStream());
        Assertions.assertEquals(writer, pdfFormatter.getWriter());
        Assertions.assertEquals(pdfDoc, pdfFormatter.getPdf());
        Assertions.assertEquals("ISO-8859-1", pdfFormatter.getEncoding());
        Assertions.assertEquals(df, pdfFormatter.getDecimalFormat());
        Assertions.assertEquals(document, pdfFormatter.getDocument());

        document.close();
        pdfDoc.close();
        writer.close();
        os.close();
    }

    /**
     * Footer must be registered before information is added to the document
     *
     * @throws Throwable - handle()
     */
    @Test
    public void testFooter() throws Throwable {
        final PdfFormatter pdfFormatter = (PdfFormatter) PdfFormatter.create()
            .setEncoding("Cp1251");
        pdfFormatter.setFileName("test_file");

        ((List<DocumentItem>) doc.getParts())
            .add(0, Footer.create("simple footer"));

        try (DocumentHolder documentHolder = pdfFormatter.handle(doc)) {
            if (Files.exists(documentHolder.getResource().getFile().toPath())) {
                final PdfReader pdfReader = new PdfReader(documentHolder.getResource().getFile());
                final PdfDocument doc1 = new PdfDocument(pdfReader);
                final ITextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
                final String currentText = PdfTextExtractor.getTextFromPage(doc1.getPage(2), strategy);
                doc1.close();
                pdfReader.close();
                Assertions.assertTrue(currentText.endsWith("simple footer"));
            }
        }
    }

    @Test
    public void testFooterPicture() throws Throwable {
        final String imageName = "pic/pic.jpg";
        final URL url = getClass().getClassLoader().getResource(imageName);
        Assertions.assertNotNull(url);
        final WritableResource resource = new PathResource(url.toURI());

        final PdfFormatter pdfFormatter = PdfFormatter.create();
        pdfFormatter.setFileName("test_file");
        ((List<DocumentItem>) doc.getParts())
            .add(
                0,
                Footer.create(IOUtils.toByteArray(resource.getInputStream()))
                    .setStyle(
                        LayoutStyle.create()
                            .setGeometryDetails(
                                GeometryDetails.create()
                                    .setScaleX(Geometry.create().add("pdf", 0.1f))
                                    .setScaleY(Geometry.create().add("pdf", 0.1f))
                                    .setAngle(Geometry.create().add("pdf", -90f))
                            )
                            .setHorAlignment(HorAlignment.LEFT)
                    )
            );

        try (DocumentHolder documentHolder = pdfFormatter.handle(doc)) {
            if (Files.exists(documentHolder.getResource().getFile().toPath())) {
                final PdfReader pdfReader = new PdfReader(documentHolder.getResource().getFile());
                final PdfDocument doc1 = new PdfDocument(pdfReader);

                for (int ind = 1; ind <= doc1.getNumberOfPages(); ind++) {
                    try (ImageEventListener listener = new ImageEventListener(imageName)) {
                        final PdfCanvasProcessor canvasProcessor = new PdfCanvasProcessor(listener);
                        canvasProcessor.processPageContent(doc1.getPage(ind));
                        if (Files.exists(listener.getImagePath())) {
                            final byte[] expected = IOUtils.toByteArray(resource.getInputStream());
                            final byte[] actual = Files.readAllBytes(listener.getImagePath());
                            Assertions.assertArrayEquals(expected, actual);
                        }
                        canvasProcessor.reset();
                    }
                }

                doc1.close();
                pdfReader.close();
            }
        }
    }

    @Test
    public void testSavePicture() throws Throwable {
        final String imageName = "pic/pic.jpg";
        final URL url = getClass().getClassLoader().getResource(imageName);
        Assertions.assertNotNull(url);
        final WritableResource resource = new PathResource(url.toURI());
        final Picture pic = Picture.create(IOUtils.toByteArray(resource.getInputStream()), PictureFormat.JPG);
        pic.setStyle(
            LayoutStyle.create()
                .setGeometryDetails(
                    GeometryDetails.create()
                        .setScaleX(Geometry.create().add("pdf", 0.1f))
                        .setScaleY(Geometry.create().add("pdf", 0.1f))
                        .setAngle(Geometry.create().add("pdf", -45f))
                )
        );

        final Document document = Document.create(Paragraph.create("Test picture in PDF:"), pic);

        try (DocumentHolder documentHolder = PdfFormatter.create().handle(document)) {
            if (Files.exists(documentHolder.getResource().getFile().toPath())) {
                final PdfReader pdfReader = new PdfReader(documentHolder.getResource().getFile());
                final PdfDocument doc1 = new PdfDocument(pdfReader);

                for (int ind = 1; ind <= doc1.getNumberOfPages(); ind++) {
                    try (ImageEventListener listener = new ImageEventListener(imageName)) {
                        final PdfCanvasProcessor canvasProcessor = new PdfCanvasProcessor(listener);
                        canvasProcessor.processPageContent(doc1.getPage(ind));
                        if (Files.exists(listener.getImagePath())) {
                            final byte[] expected = IOUtils.toByteArray(resource.getInputStream());
                            final byte[] actual = Files.readAllBytes(listener.getImagePath());
                            Assertions.assertArrayEquals(expected, actual);
                        }
                        canvasProcessor.reset();
                    }
                }

                doc1.close();
                pdfReader.close();
            }
        }
    }

    @Test
    public void testSavePictureToTableCell() throws Throwable {
        final String imageName = "pic/pic.jpg";

        final URL url = getClass().getClassLoader().getResource(imageName);
        Assertions.assertNotNull(url);
        final WritableResource resource = new PathResource(url.toURI());

        final Document document = Document.create(
            Table.create()
                .setLabel("table with picture:")
                .setTableHeaderRow(
                    TableHeaderRow
                        .create().addParts(
                            TableHeaderCell.create("01"),
                            TableHeaderCell.create("02")
                        )
                )
                .addParts(
                    TableRow.create()
                        .addParts(
                            TableCell.create("11"),
                            TableCell.create(IOUtils.toByteArray(resource.getInputStream()))
                                .setStyle(
                                    LayoutStyle.create()
                                        .setGeometryDetails(
                                            GeometryDetails.create()
                                                .setScaleX(Geometry.create().add("pdf", 0.1f))
                                                .setScaleY(Geometry.create().add("pdf", 0.1f))
                                                .setAngle(Geometry.create().add("pdf", -45f))
                                        )
                                )
                        )
                )
        );

        try (DocumentHolder documentHolder = PdfFormatter.create().handle(document)) {
            if (Files.exists(documentHolder.getResource().getFile().toPath())) {
                final PdfReader pdfReader = new PdfReader(documentHolder.getResource().getFile());
                final PdfDocument doc1 = new PdfDocument(pdfReader);

                for (int ind = 1; ind <= doc1.getNumberOfPages(); ind++) {
                    try (ImageEventListener listener = new ImageEventListener(imageName)) {
                        final PdfCanvasProcessor canvasProcessor = new PdfCanvasProcessor(listener);
                        canvasProcessor.processPageContent(doc1.getPage(ind));
                        if (Files.exists(listener.getImagePath())) {
                            final byte[] expected = IOUtils.toByteArray(resource.getInputStream());
                            final byte[] actual = Files.readAllBytes(listener.getImagePath());
                            Assertions.assertArrayEquals(expected, actual);
                        }
                        canvasProcessor.reset();
                    }
                }

                doc1.close();
                pdfReader.close();
            }
        }
    }
}
