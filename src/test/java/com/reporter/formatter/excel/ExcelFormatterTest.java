package com.reporter.formatter.excel;

import com.model.domain.Document;
import com.model.domain.DocumentCase;
import com.model.domain.Picture;
import com.model.domain.styles.LayoutStyle;
import com.model.domain.styles.constants.PictureFormat;
import com.model.domain.styles.geometry.Geometry;
import com.model.domain.styles.geometry.GeometryDetails;
import com.model.formatter.DocumentHolder;
import com.model.formatter.excel.XlsFormatter;
import com.model.formatter.excel.XlsxFormatter;
import com.model.formatter.excel.styles.ExcelStyleService;
import com.reporter.formatter.BaseDocument;
import org.apache.commons.io.IOUtils;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.WritableResource;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

class ExcelFormatterTest extends BaseDocument {

    public static final String check = "Title 1,paragraph 1,column1,column2 (столбец2),1,000,2,000,3,000,4,000," +
        "5,000,6,000,Test document v.1,,,Chapter 1,,,,,This is an example of text in paragraph,Column 1,Column 2," +
        "Cell 1.1,Cell 1.2,Cell 2.1,Cell 2.2,Cell 3.1,Cell 3.2,Cell 4.1,Cell 4.2,,Chapter 2,,,,,This is an example" +
        " of text in paragraph 2,Column 1,Column 2,Cell 1.1,Cell 1.2,Cell 2.1,Cell 2.2,Title 1,paragraph 1,,," +
        "столбец1,column2,1,000,2,000,3,000,4 and some escape characters (символы) %;;;;;\\/,5,000,6,000";

    @BeforeEach
    public void initStyledDoc() throws Exception {
        super.initDoc();
    }

    /**
     * Test {@link XlsFormatter#handle handle} call
     * and proper saving texts in "doc1.xls"
     *
     * @throws Throwable Exception/IOException
     */
    @Test
    public void testSaveTextToXlsxFile() throws Throwable {

        final XlsxFormatter xlsxFormatter = XlsxFormatter.create();
        try (DocumentHolder documentHolder = xlsxFormatter.handle(doc)) {
            final List<String> checked = new ArrayList<>();
            final Workbook wb = WorkbookFactory.create(documentHolder.getResource().getFile());
            final Sheet sheet = wb.getSheetAt(0);
            sheet.rowIterator().forEachRemaining(r -> {
                Cell cell = r.getCell(0);
                if (cell != null) {
                    checked.add(cell.getStringCellValue());
                }
                cell = r.getCell(1);
                if (cell != null) {
                    checked.add(cell.getStringCellValue());
                }
            });
            wb.close();
            Assertions.assertEquals(check, String.join(",", checked));
        }
    }

    /**
     * Test {@link XlsxFormatter#handle handle} call
     * and proper saving table in "doc1.xls",
     * check cell value in saved table on some position
     *
     * @throws Throwable Exception/IOException
     */
    @Test
    public void testSaveTableToXlsxFile() throws Throwable {

        final ExcelStyleService styleService = ExcelStyleService.create(FontCharset.DEFAULT, null);

        final XlsxFormatter xlsxFormatter = XlsxFormatter.create().setStyleService(styleService);
        try (DocumentHolder documentHolder = xlsxFormatter.handle(doc)) {
            final Workbook wb = WorkbookFactory.create(documentHolder.getResource().getFile());
            final Sheet sheet = wb.getSheetAt(0);
            final String check = sheet.getRow(15).getCell(1).getStringCellValue();
            wb.close();
            Assertions.assertEquals("Cell 3.2", check);
        }
    }

    /**
     * Tests some properties of {@link XlsFormatter}
     *
     * @throws Throwable when Document.handle() fails
     */
    @Test
    public void testFormatterProperties() throws Throwable {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final XlsFormatter xlsFormatter = XlsFormatter.create();

        final DecimalFormat df = new DecimalFormat("‰00");

        try (DocumentHolder documentHolder = xlsFormatter.handle(doc)) {
            final Workbook wb = WorkbookFactory.create(documentHolder.getResource().getFile());
            xlsFormatter
                .setFontCharset(FontCharset.JOHAB)
                .setWorkbook(wb)
                .setOutputStream(os)
                .getStyleService()
                .setDecimalFormat(df);
            Assertions.assertEquals(os, xlsFormatter.getOutputStream());
            Assertions.assertEquals(wb, xlsFormatter.getWorkbook());
            Assertions.assertEquals(FontCharset.JOHAB, xlsFormatter.getFontCharset());
            Assertions.assertEquals(df, xlsFormatter.getStyleService().getDecimalFormat());
            wb.close();
        }
    }

    @Test
    public void testSavePictureToXlsxFile() throws Throwable {
        final URL url = getClass().getClassLoader().getResource("pic.jpg");
        Assertions.assertNotNull(url);
        final WritableResource resource = new PathResource(url.toURI());
        final DocumentCase documentCase = DocumentCase.create().setName("Test sheet1")
            .addParts(
                Picture.create(resource, PictureFormat.JPEG)
                    .setStyle(
                        LayoutStyle.create()
                            .setGeometryDetails(
                                GeometryDetails.create()
//                                    .setScaleX(Geometry.create().add("xlsx", Double.MAX_VALUE))
//                                    .setScaleY(Geometry.create().add("xlsx", Double.MAX_VALUE))
                                    .setWidth(Geometry.create().add("xlsx", 80 * 100))
                                    .setHeight(Geometry.create().add("xlsx", (short) (48 * 40)))
                                    .setAngle(Geometry.create().add("xlsx", 30))
                            )
                    )
            );

        doc = Document
            .create()
            .setLabel("Test document")
            .setAuthor("A1 Systems")
            .setDescription("meta information")
            .addPart(documentCase);

        final XlsxFormatter xlsxFormatter = XlsxFormatter.create();
        try (DocumentHolder documentHolder = xlsxFormatter.handle(doc)) {
            final Workbook wb = WorkbookFactory.create(documentHolder.getResource().getFile());
            final Sheet sheet = wb.getSheetAt(0);
            final XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
            Assertions.assertEquals(1, drawing.getShapes().size());
            Assertions.assertEquals(XSSFPicture.class, drawing.getShapes().get(0).getClass());

            final XSSFPicture picture = (XSSFPicture) drawing.getShapes().get(0);
            Assertions.assertEquals(0, picture.getClientAnchor().getCol1());
            Assertions.assertEquals(1, picture.getClientAnchor().getCol2());
            Assertions.assertEquals(0, picture.getClientAnchor().getRow1());
            Assertions.assertEquals(1, picture.getClientAnchor().getRow2());
            Assertions.assertArrayEquals(
                IOUtils.toByteArray(resource.getInputStream()),
                picture.getPictureData().getData()
            );

            wb.close();
        }
    }
}
