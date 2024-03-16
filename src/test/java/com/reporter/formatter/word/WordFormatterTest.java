package com.reporter.formatter.word;

import com.model.domain.Document;
import com.model.domain.Footer;
import com.model.domain.Picture;
import com.model.domain.Table;
import com.model.domain.TableCell;
import com.model.domain.TableHeaderCell;
import com.model.domain.TableHeaderRow;
import com.model.domain.TableRow;
import com.model.domain.Title;
import com.model.domain.core.DocumentItem;
import com.model.domain.style.BorderStyle;
import com.model.domain.style.LayoutStyle;
import com.model.domain.style.LayoutTextStyle;
import com.model.domain.style.Style;
import com.model.domain.style.TextStyle;
import com.model.domain.style.constant.BorderWeight;
import com.model.domain.style.constant.Color;
import com.model.domain.style.constant.PictureFormat;
import com.model.domain.style.geometry.Geometry;
import com.model.domain.style.geometry.GeometryDetails;
import com.model.formatter.DocumentHolder;
import com.model.formatter.word.DocFormatter;
import com.model.formatter.word.DocxFormatter;
import com.model.formatter.word.WordFormatter;
import com.reporter.formatter.BaseDocument;
import org.apache.commons.io.IOUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeaderFooter;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.WritableResource;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WordFormatterTest extends BaseDocument {

    public static final String expectedTextParagraphs = "Title 1;paragraph 1;\n;Test document v.1;\n" +
        ";Chapter 1;Chapter 1.1;Chapter 1.1.1;This is an example of text in paragraph;Chapter 2;Chapter 2.1;" +
        "Chapter 2.1.1;This is an example of text in paragraph 2;Title 1;paragraph 1;shifted heading";

    public static final String expectedTextTables = "column1\tcolumn2 (столбец2)\n1,000\t2,000\n" +
        "3,000\t4,000\n5,000\t6,000\n;Column 1\tColumn 2\nCell 1.1\tCell 1.2\nCell 2.1\tCell 2.2\n" +
        "Cell 3.1\tCell 3.2\nCell 4.1\tCell 4.2\n;Column 1\tColumn 2\nCell 1.1\tCell 1.2\nCell 2.1\tCell 2.2\n" +
        ";столбец1\tcolumn2\n1,000\t2,000\n3,000\t4 and some escape characters (символы) %;;;;;\\/\n5,000\t6,000\n";

    @BeforeEach
    public void initStyledDoc() throws Exception {
        super.initDoc();
    }

    /**
     * Test {@link DocxFormatter#handle handle} call
     * and proper saving texts in "Test document.docx"
     *
     * @throws Throwable Exception/IOException
     */
    @Test
    public void testSaveTextToDocxFile() throws Throwable {
        final WordFormatter docxFormatter = DocxFormatter.create();
        try (DocumentHolder documentHolder = docxFormatter.handle(doc)) {

            final XWPFDocument docx = new XWPFDocument(documentHolder.getResource().getInputStream());
            final List<XWPFParagraph> paragraphs = docx.getParagraphs();

            final String actualParagraphs =
                paragraphs
                    .stream()
                    .map(XWPFParagraph::getText)
                    .collect(Collectors.joining(";"));

            Assertions.assertEquals(expectedTextParagraphs, actualParagraphs);

            final List<XWPFTable> tables = docx.getTables();

            final String actualTables =
                tables
                    .stream()
                    .map(XWPFTable::getText)
                    .collect(Collectors.joining(";"));

            Assertions.assertEquals(expectedTextTables, actualTables);
            docx.close();
        }
    }

    @Test
    public void testSaveRotatedTextAsPictureToDocxFile() throws Throwable {
        final WordFormatter docxFormatter = DocxFormatter.create();
        final Style bordersStyle = LayoutStyle.create()
            .setBorderTop(BorderStyle.create(Color.BLACK, BorderWeight.THICK))
            .setBorderLeft(BorderStyle.create(Color.BLACK, BorderWeight.THICK))
            .setBorderRight(BorderStyle.create(Color.BLACK, BorderWeight.THICK))
            .setBorderBottom(BorderStyle.create(Color.BLACK, BorderWeight.THICK));
        doc = Document.create()
            .addParts(
                Table.create(
                        TableHeaderRow.create(
                                TableHeaderCell.create("Test Header1"),
                                TableHeaderCell.create("Test Header2")
                            )
                            .spreadStyleToParts(bordersStyle)
                    )
                    .addParts(
                        TableRow.create(
                            TableCell.create("test1")
                                .setStyle(
                                    LayoutTextStyle.create(
                                        TextStyle.create()
                                            .setUnderline((byte) 1)
                                            .setItalic(true),
                                        LayoutStyle.create()
                                            .setGeometryDetails(
                                                GeometryDetails.create()
                                                    .setAngle(
                                                        Geometry.create()
                                                            .add("docx", 20)
                                                    )
                                            )

                                    )
                                ),
                            TableCell.create("test2")
                        )
                    )
                    .spreadStyleToParts(bordersStyle)
            );


        try (DocumentHolder ignored = docxFormatter.handle(doc)) {
            /**/
        }
    }

    @Test
    public void testFooter() throws Throwable {
        final DocxFormatter docxFormatter = DocxFormatter.create();
        docxFormatter.setFileName("test_file");

        ((List<DocumentItem>) doc.getParts())
            .add(0, Footer.create("simple footer"));

        try (DocumentHolder documentHolder = docxFormatter.handle(doc)) {

            Assertions.assertDoesNotThrow(() -> {
                final File file = documentHolder.getResource().getFile();
                if (Files.exists(file.toPath())) {
                    Assertions.assertEquals("test_file.docx", file.getName());

                    final XWPFDocument docx = new XWPFDocument(documentHolder.getResource().getInputStream());
                    final String actual =
                        docx.getFooterList()
                            .stream()
                            .map(XWPFHeaderFooter::getText)
                            .collect(Collectors.joining(";"));

                    Assertions.assertTrue(actual.endsWith("simple footer\n"));
                    docx.close();
                }
            });
        }
    }

    /**
     * Test {@link DocFormatter#handle handle} call
     * and proper saving texts in "Test document.doc"
     *
     * @throws Throwable Exception/IOException
     */
    @Test
    public void testSaveTextToDocFile() throws Throwable {
        final WordFormatter docFormatter = DocFormatter.create();
        try (DocumentHolder ignored = docFormatter.handle(doc)) { /**/ }
    }

    @Test
    public void testSavePictureToDocxFile() throws Throwable {
        final int DOCX_ANGLE_CONST = -60000;
        final WordFormatter docxFormatter = DocxFormatter.create();
        final URL url = getClass().getClassLoader().getResource("pic.jpg");
        Assertions.assertNotNull(url);
        final WritableResource resource = new PathResource(url.toURI());
        doc = Document.create()
            .setLabel("docx with picture")
            .addParts(
                Title.create().setText("Picture 1"),
                Picture.create()
                    .setFormat(PictureFormat.JPG)
                    .setData(IOUtils.toByteArray(resource.getInputStream()))
                    .setStyle(
                        LayoutStyle.create()
                            .setGeometryDetails(
                                GeometryDetails.create()
                                    .setWidth(Geometry.create().add(DocxFormatter.EXTENSION, 80))
                                    .setHeight(Geometry.create().add(DocxFormatter.EXTENSION, 48))
                                    .setAngle(Geometry.create().add(DocxFormatter.EXTENSION, 30))
                            )
                    )
            );

        try (
            DocumentHolder documentHolder = docxFormatter.handle(doc);
            XWPFDocument docx = new XWPFDocument(documentHolder.getResource().getInputStream())
        ) {
            final List<XWPFPictureData> resultPicturelist = docx.getAllPictures();
            Assertions.assertEquals(1, resultPicturelist.size());
            final XWPFPictureData resultPicture = resultPicturelist.get(0);
            Assertions.assertArrayEquals(IOUtils.toByteArray(resource.getInputStream()), resultPicture.getData());

            final XWPFRun pictureRun = docx.getParagraphs().get(1).getRuns().get(0);
            final List<XWPFPicture> xwpfPictureList = pictureRun.getEmbeddedPictures();
            final XWPFPicture xwpfPicture = xwpfPictureList.get(0);
            final CTPicture picture = xwpfPicture.getCTPicture();
            final CTTransform2D transform2D = picture.getSpPr().getXfrm();
            final int resultAngle = transform2D.getRot();
            final CTPositiveSize2D ext = transform2D.getExt();
            Assertions.assertEquals(30 * DOCX_ANGLE_CONST, resultAngle);
            Assertions.assertEquals(80, Units.toPoints(ext.getCx()));
            Assertions.assertEquals(48, Units.toPoints(ext.getCy()));
        }
    }

    @Test
    public void testSavePictureInCellToDocxFile() throws Throwable {
        final WordFormatter docxFormatter = DocxFormatter.create();
        final URL url = getClass().getClassLoader().getResource("pic.jpg");
        Assertions.assertNotNull(url);
        doc = Document.create()
            .addParts(
                Table.create(
                        TableHeaderRow.create()
                            .addParts(
                                TableHeaderCell.create().setText("Header cell 1"),
                                TableHeaderCell.create().setText("Header cell 2")
                            )
                    )
                    .addParts(
                        TableRow.create()
                            .addParts(
                                TableCell.create().setText("Cell 1"),
                                TableCell.create().setText("Cell 2")
                                    .setStyle(
                                        LayoutStyle.create()
                                            .setGeometryDetails(
                                                GeometryDetails.create()
                                                    .setAngle(
                                                        Geometry.create()
                                                            .add("docx", 2)
                                                    )
                                            )
                                    )
                            )
                    )
            );

        try (DocumentHolder ignored = docxFormatter.handle(doc)) {
            /**/
        }
    }

    private static List<CTDrawing> getAllDrawings(XWPFRun run) throws Exception {
        final CTR ctR = run.getCTR();
        final XmlCursor cursor = ctR.newCursor();
        cursor.selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' .//*/w:drawing");
        final List<CTDrawing> drawings = new ArrayList<>();
        while (cursor.hasNextSelection()) {
            cursor.toNextSelection();
            final XmlObject obj = cursor.getObject();
            final CTDrawing drawing = CTDrawing.Factory.parse(obj.newInputStream());
            drawings.add(drawing);
        }
        cursor.close();
        return drawings;
    }
}
