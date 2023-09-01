package com.reporter.formatter.excel.styles;

import com.model.domain.style.BorderStyle;
import com.model.domain.style.LayoutStyle;
import com.model.domain.style.constant.BorderWeight;
import com.model.domain.style.constant.Color;
import com.model.domain.style.constant.FillPattern;
import com.model.domain.style.constant.HorAlignment;
import com.model.domain.style.constant.PictureFormat;
import com.model.domain.style.constant.VertAlignment;
import com.model.formatter.excel.styles.ExcelStyleService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.model.formatter.excel.styles.ExcelStyleService.toExcelBorder;
import static com.model.formatter.excel.styles.ExcelStyleService.toExcelColor;
import static com.model.formatter.excel.styles.ExcelStyleService.toExcelFillPattern;
import static com.model.formatter.excel.styles.ExcelStyleService.toExcelHorAlignment;
import static com.model.formatter.excel.styles.ExcelStyleService.toExcelPictureFormat;
import static com.model.formatter.excel.styles.ExcelStyleService.toExcelVertAlignment;

public class ExcelStyleApplierTest {

    private CellStyle cellStyle;

    private HSSFWorkbook wb;

    private final LayoutStyle layoutStyle = LayoutStyle.create()
        .setBorderTop(BorderStyle.create(Color.BLACK, BorderWeight.DOUBLE))
        .setBorderLeft(BorderStyle.create(Color.WHITE, BorderWeight.MEDIUM))
        .setBorderRight(BorderStyle.create(Color.RED, BorderWeight.THICK))
        .setBorderBottom(BorderStyle.create(Color.PINK, BorderWeight.THIN))
        .setVertAlignment(VertAlignment.BOTTOM)
        .setHorAlignment(HorAlignment.GENERAL)
        .setFillPattern(FillPattern.THIN_VERT_BANDS)
        .setFillForegroundColor(Color.WHITE)
        .setFillBackgroundColor(Color.WHITE)
        .setShrinkToFit(true);

    @BeforeEach
    public void init() {
        wb = new HSSFWorkbook();
        cellStyle = wb.createCellStyle();
    }

    @AfterEach
    void dispose() throws IOException {
        wb.close();
    }

    @Test
    public void testConvertFit() {
        ExcelStyleService.convertFit(cellStyle, layoutStyle);
        Assertions.assertTrue(cellStyle.getShrinkToFit());
    }

    @Test
    public void testConvertVerticalAlignment() {
        ExcelStyleService.convertVerticalAlignment(cellStyle, layoutStyle);
        Assertions.assertEquals(cellStyle.getVerticalAlignment(), toExcelVertAlignment(layoutStyle.getVertAlignment()));
    }

    @Test
    public void testConvertHorizontalAlignment() {
        ExcelStyleService.convertHorizontalAlignment(cellStyle, layoutStyle);
        Assertions.assertEquals(cellStyle.getAlignment(), toExcelHorAlignment(layoutStyle.getHorAlignment()));
    }

    @Test
    public void testConvertBorders() {
        ExcelStyleService.convertBorders(cellStyle, layoutStyle);

        Assertions.assertEquals(cellStyle.getBorderTop(), toExcelBorder(layoutStyle.getBorderTop().getWeight()));
        Assertions.assertEquals(cellStyle.getBorderLeft(), toExcelBorder(layoutStyle.getBorderLeft().getWeight()));
        Assertions.assertEquals(cellStyle.getBorderRight(), toExcelBorder(layoutStyle.getBorderRight().getWeight()));
        Assertions.assertEquals(cellStyle.getBorderBottom(), toExcelBorder(layoutStyle.getBorderBottom().getWeight()));
    }

    @Test
    public void testConvertBorderColors() {
        ExcelStyleService.convertBorderColors(cellStyle, layoutStyle);

        Assertions.assertEquals(cellStyle.getTopBorderColor(),
            toExcelColor(layoutStyle.getBorderTop().getColor()));
        Assertions.assertEquals(cellStyle.getLeftBorderColor(),
            toExcelColor(layoutStyle.getBorderLeft().getColor()));
        Assertions.assertEquals(cellStyle.getRightBorderColor(),
            toExcelColor(layoutStyle.getBorderRight().getColor()));
        Assertions.assertEquals(cellStyle.getBottomBorderColor(),
            toExcelColor(layoutStyle.getBorderBottom().getColor()));
    }

    //Look https://stackoverflow.com/questions/38874115/
    //java-apache-poi-how-to-set-background-color-and-borders-at-same-time/46996790#46996790
    @Test
    public void testConvertGround() {
        ExcelStyleService.convertGround(cellStyle, layoutStyle);

        Assertions.assertEquals(cellStyle.getFillBackgroundColor(), toExcelColor(layoutStyle.getFillBackgroundColor()));
        Assertions.assertEquals(cellStyle.getFillForegroundColor(), toExcelColor(layoutStyle.getFillForegroundColor()));
        Assertions.assertEquals(cellStyle.getFillPattern(), toExcelFillPattern(layoutStyle.getFillPattern()));
    }

    @Test
    public void testConvertPictureFormats() {
        Assertions.assertEquals(Workbook.PICTURE_TYPE_EMF, toExcelPictureFormat(PictureFormat.EMF));
        Assertions.assertEquals(Workbook.PICTURE_TYPE_WMF, toExcelPictureFormat(PictureFormat.WMF));
        Assertions.assertEquals(Workbook.PICTURE_TYPE_PICT, toExcelPictureFormat(PictureFormat.PICT));
        Assertions.assertEquals(Workbook.PICTURE_TYPE_JPEG, toExcelPictureFormat(PictureFormat.JPEG));
        Assertions.assertEquals(Workbook.PICTURE_TYPE_JPEG, toExcelPictureFormat(PictureFormat.JPG));
        Assertions.assertEquals(Workbook.PICTURE_TYPE_PNG, toExcelPictureFormat(PictureFormat.PNG));
        Assertions.assertEquals(Workbook.PICTURE_TYPE_DIB, toExcelPictureFormat(PictureFormat.DIB));
    }
}
