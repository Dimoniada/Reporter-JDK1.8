package com.model.formatter.excel.style;

import com.google.common.base.MoreObjects;
import com.model.domain.Picture;
import com.model.domain.core.DataItem;
import com.model.domain.core.TextItem;
import com.model.domain.style.BorderStyle;
import com.model.domain.style.LayoutStyle;
import com.model.domain.style.LayoutTextStyle;
import com.model.domain.style.Style;
import com.model.domain.style.StyleService;
import com.model.domain.style.TextStyle;
import com.model.domain.style.constant.BorderWeight;
import com.model.domain.style.constant.Color;
import com.model.domain.style.constant.FillPattern;
import com.model.domain.style.constant.HorAlignment;
import com.model.domain.style.constant.PictureFormat;
import com.model.domain.style.constant.VertAlignment;
import com.model.domain.style.geometry.GeometryDetails;
import com.model.formatter.excel.XlsDetails;
import com.model.utils.CastUtils;
import com.model.utils.LocalizedNumberUtils;
import com.model.utils.MapBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The class caches {@link Style} styles,
 * maps {@link Style} attributes to native,
 * implements methods of their application for
 * {@link org.apache.poi.ss.usermodel.Cell},
 * {@link org.apache.poi.ss.usermodel.Row}
 */
public class ExcelStyleService extends StyleService implements XlsDetails {
    // Apache POI can resolve 1/60000 of degree, minus because of the anticlockwise direction
    private static final int EXCEL_ANGLE_CONST = -60000;

    private static final Map<BorderWeight, org.apache.poi.ss.usermodel.BorderStyle> borderMap =
        new MapBuilder<BorderWeight, org.apache.poi.ss.usermodel.BorderStyle>()
            .put(null, org.apache.poi.ss.usermodel.BorderStyle.NONE)
            .put(BorderWeight.NONE, org.apache.poi.ss.usermodel.BorderStyle.NONE)
            .put(BorderWeight.THIN, org.apache.poi.ss.usermodel.BorderStyle.THIN)
            .put(BorderWeight.MEDIUM, org.apache.poi.ss.usermodel.BorderStyle.MEDIUM)
            .put(BorderWeight.THICK, org.apache.poi.ss.usermodel.BorderStyle.THICK)
            .put(BorderWeight.DOUBLE, org.apache.poi.ss.usermodel.BorderStyle.DOUBLE)
            .put(BorderWeight.DOTTED, org.apache.poi.ss.usermodel.BorderStyle.DOTTED)
            .build();

    private static final Map<HorAlignment, HorizontalAlignment> horizontalAlignmentMap =
        new MapBuilder<HorAlignment, HorizontalAlignment>()
            .put(null, null)
            .put(HorAlignment.GENERAL, HorizontalAlignment.GENERAL)
            .put(HorAlignment.LEFT, HorizontalAlignment.LEFT)
            .put(HorAlignment.CENTER, HorizontalAlignment.CENTER)
            .put(HorAlignment.RIGHT, HorizontalAlignment.RIGHT)
            .build();

    private static final Map<VertAlignment, VerticalAlignment> verticalAlignmentMap =
        new MapBuilder<VertAlignment, VerticalAlignment>()
            .put(null, null)
            .put(VertAlignment.TOP, VerticalAlignment.TOP)
            .put(VertAlignment.CENTER, VerticalAlignment.CENTER)
            .put(VertAlignment.BOTTOM, VerticalAlignment.BOTTOM)
            .build();

    private static final Map<FillPattern, FillPatternType> fillPatternMap =
        new MapBuilder<FillPattern, FillPatternType>()
            .put(null, FillPatternType.NO_FILL)
            .put(FillPattern.NO_FILL, FillPatternType.NO_FILL)
            .put(FillPattern.FINE_DOTS, FillPatternType.FINE_DOTS)
            .put(FillPattern.SOLID_FOREGROUND, FillPatternType.SOLID_FOREGROUND)
            .put(FillPattern.THIN_HORZ_BANDS, FillPatternType.THIN_HORZ_BANDS)
            .put(FillPattern.THIN_VERT_BANDS, FillPatternType.THIN_VERT_BANDS)
            .put(FillPattern.THIN_BACKWARD_DIAG, FillPatternType.THIN_BACKWARD_DIAG)
            .put(FillPattern.THIN_FORWARD_DIAG, FillPatternType.THIN_FORWARD_DIAG)
            .build();

    private static final Map<PictureFormat, Integer> pictureFormatMap =
        new MapBuilder<PictureFormat, Integer>()
            .put(null, -1)
            .put(PictureFormat.JPG, Workbook.PICTURE_TYPE_JPEG)
            .put(PictureFormat.PNG, Workbook.PICTURE_TYPE_PNG)
            .put(PictureFormat.WMF, Workbook.PICTURE_TYPE_WMF)
            .put(PictureFormat.EMF, Workbook.PICTURE_TYPE_EMF)
            .put(PictureFormat.DIB, Workbook.PICTURE_TYPE_DIB)
            .put(PictureFormat.BMP, Workbook.PICTURE_TYPE_DIB)
            .put(PictureFormat.PICT, Workbook.PICTURE_TYPE_PICT)
            .build();

    private static final HSSFPalette palette;

    protected final Map<Cell, LayoutStyle> needAdjustHeaderCells = new HashMap<>();

    private final FontCharset fontCharset;
    private final Map<LayoutStyle, CellStyle> layoutStyles = new HashMap<>();
    private final Map<TextStyle, CellStyle> textStyles = new HashMap<>();
    private final Map<LayoutTextStyle, CellStyle> layoutTextStyles = new HashMap<>();
    private Workbook workbook;

    static {
        try (HSSFWorkbook hwb = new HSSFWorkbook()) {
            palette = hwb.getCustomPalette();
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't create HSSFWorkbook for color palette.", e);
        }
    }

    public ExcelStyleService(FontCharset fontCharset, DecimalFormat decimalFormat) {
        this.fontCharset = fontCharset;
        this.decimalFormat = decimalFormat;
    }

    private void convertCommonStyleToCell(
        Cell cell,
        CellStyle cellStyle,
        LayoutStyle layoutStyle,
        XSSFPicture xssfPicture
    ) {
        convertGround(cellStyle, layoutStyle);
        convertBorders(cellStyle, layoutStyle);
        convertBorderColors(cellStyle, layoutStyle);
        convertHorizontalAlignment(cellStyle, layoutStyle);
        convertVerticalAlignment(cellStyle, layoutStyle);
        convertFit(cellStyle, layoutStyle);
        convertGeometryDetails(cell, cellStyle, layoutStyle, xssfPicture);
    }

    public static ExcelStyleService create(FontCharset fontCharset, DecimalFormat decimalFormat) {
        return new ExcelStyleService(fontCharset, decimalFormat);
    }

    public static ExcelStyleService create(FontCharset fontCharset) {
        return create(fontCharset, null);
    }

    public static ExcelStyleService create() {
        return create(FontCharset.DEFAULT, null);
    }

    /**
     * Utility Methods
     **/
    public static Short toExcelColor(Color color) {
        if (color == null) {
            return null;
        }
        final HSSFColor excelColor = palette.findSimilarColor(color.getRed(), color.getGreen(), color.getBlue());
        return excelColor.getIndex();
    }

    public static Integer toExcelPictureFormat(PictureFormat pictureFormat) {
        if (pictureFormatMap.containsKey(pictureFormat)) {
            return pictureFormatMap.get(pictureFormat);
        } else {
            throw new IllegalArgumentException("Undefined PictureFormat type");
        }
    }

    public static org.apache.poi.ss.usermodel.BorderStyle
    toExcelBorder(BorderWeight border) {
        if (borderMap.containsKey(border)) {
            return borderMap.get(border);
        } else {
            throw new IllegalArgumentException("Undefined BorderWeight type");
        }
    }

    public static VerticalAlignment toExcelVertAlignment(VertAlignment vertAlignment) {
        if (verticalAlignmentMap.containsKey(vertAlignment)) {
            return verticalAlignmentMap.get(vertAlignment);
        } else {
            throw new IllegalArgumentException("Undefined VerticalAlignment type");
        }
    }

    public static HorizontalAlignment toExcelHorAlignment(HorAlignment horAlignment) {
        if (horizontalAlignmentMap.containsKey(horAlignment)) {
            return horizontalAlignmentMap.get(horAlignment);
        } else {
            throw new IllegalArgumentException("Undefined HorizontalAlignment type");
        }
    }

    public static FillPatternType toExcelFillPattern(FillPattern fillPattern) {
        if (fillPatternMap.containsKey(fillPattern)) {
            return fillPatternMap.get(fillPattern);
        } else {
            throw new IllegalArgumentException("Undefined FillPattern type");
        }
    }

    public static void convertFit(CellStyle cellStyle, LayoutStyle layoutStyle) {
        final Boolean isShrinkToFit = layoutStyle.isShrinkToFit();
        if (isShrinkToFit != null) {
            cellStyle.setShrinkToFit(layoutStyle.isShrinkToFit());
        }
    }

    public static void convertGeometryDetails(
        Cell cell,
        CellStyle cellStyle,
        LayoutStyle layoutStyle,
        XSSFPicture xssfPicture
    ) {
        final GeometryDetails geometryDetails = layoutStyle.getGeometryDetails();
        if (geometryDetails == null) {
            return;
        }

        // Height
        if (geometryDetails.getHeight() != null) {
            geometryDetails
                .getHeight()
                .getValueFor(EXTENSION)
                .ifPresent(value ->
                    cell.getRow().setHeight(CastUtils.convert(value))
                );
        }

        // Width
        if (geometryDetails.getWidth() != null) {
            geometryDetails
                .getWidth()
                .getValueFor(EXTENSION)
                .ifPresent(value ->
                    cell.getSheet().setColumnWidth(cell.getColumnIndex(), CastUtils.convert(value))
                );
        }

        // Rotation angle (center is a middle of cell/picture)
        if (geometryDetails.getAngle() != null) {
            geometryDetails
                .getAngle()
                .getValueFor(EXTENSION)
                .ifPresent(angle -> {
                    if (xssfPicture != null) {
                        xssfPicture
                            .getCTPicture()
                            .getSpPr()
                            .getXfrm()
                            .setRot((short) CastUtils.convert(angle) * EXCEL_ANGLE_CONST);
                    } else {
                        cellStyle.setRotation(CastUtils.convert(angle));
                    }
                });
        }

        if (xssfPicture == null) {
            return;
        }
        // Horizontal & Vertical scaling
        if (geometryDetails.getScaleX() != null && geometryDetails.getScaleY() != null) {
            final AtomicReference<Double> scaleX = new AtomicReference<>(1d);
            final AtomicReference<Double> scaleY = new AtomicReference<>(1d);
            geometryDetails
                .getScaleX()
                .getValueFor(EXTENSION)
                .ifPresent(value -> scaleX.set(CastUtils.convert(value)));
            geometryDetails
                .getScaleY()
                .getValueFor(EXTENSION)
                .ifPresent(value -> scaleY.set(CastUtils.convert(value)));
            xssfPicture.resize(scaleX.get(), scaleY.get());
        }
    }

    public static void applyAutoWidth(Cell cell, LayoutStyle layoutStyle) {
        final Boolean isAutoWidth = layoutStyle.isAutoWidth();
        if (isAutoWidth != null && isAutoWidth) {
            cell.getSheet().autoSizeColumn(cell.getColumnIndex());
        }
    }

    public static void convertVerticalAlignment(CellStyle cellStyle, LayoutStyle layoutStyle) {
        final VerticalAlignment verticalAlignment =
            toExcelVertAlignment(layoutStyle.getVertAlignment());
        if (verticalAlignment != null) {
            cellStyle.setVerticalAlignment(verticalAlignment);
        }
    }

    public static void convertHorizontalAlignment(CellStyle cellStyle, LayoutStyle layoutStyle) {
        final HorizontalAlignment horizontalAlignment =
            toExcelHorAlignment(layoutStyle.getHorAlignment());
        if (horizontalAlignment != null) {
            cellStyle.setAlignment(toExcelHorAlignment(layoutStyle.getHorAlignment()));
        }
    }

    public static void convertBorders(CellStyle cellStyle, LayoutStyle layoutStyle) {
        final BorderStyle borderTop = layoutStyle.getBorderTop();
        final BorderStyle borderLeft = layoutStyle.getBorderLeft();
        final BorderStyle borderRight = layoutStyle.getBorderRight();
        final BorderStyle borderBottom = layoutStyle.getBorderBottom();
        if (borderTop != null) {
            cellStyle.setBorderTop(toExcelBorder(borderTop.getWeight()));
        }
        if (borderLeft != null) {
            cellStyle.setBorderLeft(toExcelBorder(borderLeft.getWeight()));
        }
        if (borderRight != null) {
            cellStyle.setBorderRight(toExcelBorder(borderRight.getWeight()));
        }
        if (borderBottom != null) {
            cellStyle.setBorderBottom(toExcelBorder(borderBottom.getWeight()));
        }
    }

    public static void convertBorderColors(CellStyle cellStyle, LayoutStyle layoutStyle) {
        final BorderStyle borderTop = layoutStyle.getBorderTop();
        final BorderStyle borderLeft = layoutStyle.getBorderLeft();
        final BorderStyle borderRight = layoutStyle.getBorderRight();
        final BorderStyle borderBottom = layoutStyle.getBorderBottom();
        if (borderTop != null) {
            cellStyle.setTopBorderColor(toExcelColor(borderTop.getColor()));
        }
        if (borderLeft != null) {
            cellStyle.setLeftBorderColor(toExcelColor(borderLeft.getColor()));
        }
        if (borderRight != null) {
            cellStyle.setRightBorderColor(toExcelColor(borderRight.getColor()));
        }
        if (borderBottom != null) {
            cellStyle.setBottomBorderColor(toExcelColor(borderBottom.getColor()));
        }
    }

    //See the difference between Foreground and Background colors, the order and conditions for their filling
    // https://stackoverflow.com/questions/38874115/
    // java-apache-poi-how-to-set-background-color-and-borders-at-same-time/46996790#46996790
    public static void convertGround(CellStyle cellStyle, LayoutStyle layoutStyle) {
        final Short foregroundColor = toExcelColor(layoutStyle.getFillForegroundColor());
        if (foregroundColor != null) {
            cellStyle.setFillForegroundColor(foregroundColor);
        }
        final FillPatternType fillPatternType = toExcelFillPattern(layoutStyle.getFillPattern());
        if (fillPatternType != null) {
            cellStyle.setFillPattern(fillPatternType);
        }
        final Short backgroundColor = toExcelColor(layoutStyle.getFillBackgroundColor());
        if (backgroundColor != null) {
            cellStyle.setFillBackgroundColor(backgroundColor);
        }
    }

    public void adjustHeaderCells() {
        needAdjustHeaderCells.forEach(ExcelStyleService::applyAutoWidth);
    }

    @Override
    public void writeStyles(Object o) {
        final Workbook workbook = (Workbook) o;
        this.workbook = workbook;
        final CellStyle style = workbook.createCellStyle();
        final Font font = workbook.createFont();
        style.setFont(font);
        font.setCharSet(fontCharset.getNativeId());
        textStyles.put(null, style);
        layoutStyles.put(null, style);
    }

    /**
     * Writes DocumentItem to excel cell
     *
     * @param dataItem DocumentItem
     * @param cellObj  native excel cell
     * @throws ParseException when can't apply FontCharset and DecimalFormat to cell
     * @throws IOException    when can't find/read picture or convert it to byte array
     * @throws Exception      on joining styles
     */
    public void writeItemToCell(DataItem dataItem, Cell cellObj)
        throws Exception {
        XSSFPicture xssfPicture = null;
        final Style style = prepareStyleFrom(dataItem);
        if (dataItem.isDataInheritedFrom(TextItem.class)) {
            workbook.getFontAt(cellObj.getCellStyle().getFontIndex()).setCharSet(fontCharset.getNativeId());
            if (StringUtils.hasText(dataItem.getText())) {
                cellObj.setCellValue(
                    LocalizedNumberUtils.applyDecimalFormat(dataItem.getText(), dataItem.getStyle(), decimalFormat)
                );
            } else {
                cellObj.setCellValue("");
            }
        }
        if (dataItem.isDataInheritedFrom(Picture.class)) {
            final Picture picture = (Picture) dataItem;
            final InputStream pictureStream = new ByteArrayInputStream(picture.getData());
            final int picInd =
                workbook.addPicture(
                    IOUtils.toByteArray(pictureStream),
                    toExcelPictureFormat(picture.getFormat())
                );
            final Sheet lastSheet = workbook.getSheetAt(workbook.getNumberOfSheets() - 1);
            final XSSFDrawing drawing = (XSSFDrawing) lastSheet.createDrawingPatriarch();
            final XSSFClientAnchor pictureAnchor = new XSSFClientAnchor();
            final int col1 = cellObj.getColumnIndex();
            final int col2 = cellObj.getColumnIndex() + 1;
            final int row1 = cellObj.getRowIndex();
            final int row2 = cellObj.getRowIndex() + 1;
            pictureAnchor.setCol1(col1);
            pictureAnchor.setCol2(col2);
            pictureAnchor.setRow1(row1);
            pictureAnchor.setRow2(row2);
            xssfPicture = drawing.createPicture(pictureAnchor, picInd);
        }
        if (style instanceof TextStyle) {
            convertTextStyleToCell(cellObj, (TextStyle) style);
        } else if (style instanceof LayoutStyle) {
            convertLayoutStyleToCell(cellObj, (LayoutStyle) style, xssfPicture);
        } else if (style instanceof LayoutTextStyle) {
            convertLayoutTextStyleToCell(cellObj, (LayoutTextStyle) style, xssfPicture);
        }
    }

    public void convertTextStyleToCell(Cell cell, TextStyle textStyle) {
        final Workbook wb = cell.getSheet().getWorkbook();
        CellStyle cellStyle = wb.createCellStyle();
        final Font font;
        if (textStyles.containsKey(textStyle)) {
            cellStyle = textStyles.get(textStyle);
        } else {
            font = createFontFromTextStyle(textStyle, wb);
            cellStyle.setFont(font);
        }
        cell.setCellStyle(cellStyle);
    }

    public void convertLayoutStyleToCell(Cell cell, LayoutStyle layoutStyle, XSSFPicture xssfPicture) {
        final CellStyle cellStyle;

        if (layoutStyles.containsKey(layoutStyle)) {
            cellStyle = layoutStyles.get(layoutStyle);
        } else {
            cellStyle = cell.getSheet().getWorkbook().createCellStyle();
            convertCommonStyleToCell(cell, cellStyle, layoutStyle, xssfPicture);
            layoutStyles.put(layoutStyle, cellStyle);
        }
        cell.setCellStyle(cellStyle);
        applyAutoWidth(cell, layoutStyle);
    }

    public void convertLayoutTextStyleToCell(Cell cell, LayoutTextStyle layoutTextStyle, XSSFPicture xssfPicture) {
        final CellStyle cellStyle;
        final Workbook wb = cell.getSheet().getWorkbook();
        final TextStyle textStyle = layoutTextStyle.getTextStyle();
        final LayoutStyle layoutStyle = layoutTextStyle.getLayoutStyle();

        if (layoutTextStyles.containsKey(layoutTextStyle)) {
            cellStyle = layoutTextStyles.get(layoutTextStyle);
        } else {
            cellStyle = wb.createCellStyle();
            final Font font = createFontFromTextStyle(textStyle, wb);
            cellStyle.setFont(font);

            convertCommonStyleToCell(cell, cellStyle, layoutStyle, xssfPicture);
            layoutTextStyles.put(layoutTextStyle, cellStyle);
        }
        cell.setCellStyle(cellStyle);
        applyAutoWidth(cell, layoutStyle);
    }

    public Font createFontFromTextStyle(TextStyle textStyle, Workbook wb) {
        final Font font;

        font = wb.createFont();
        final String fontName = textStyle.getFontNameResource();
        if (StringUtils.hasText(fontName)) {
            font.setFontName(fontName);
        }
        final Boolean isBold = textStyle.isBold();
        if (isBold != null) {
            font.setBold(textStyle.isBold());
        }
        final Short fontColor = toExcelColor(textStyle.getColor());
        if (fontColor != null) {
            font.setColor(toExcelColor(textStyle.getColor()));
        }
        final Short fontSize = textStyle.getFontSize();
        if (fontSize != null) {
            font.setFontHeightInPoints(textStyle.getFontSize());
        }
        final Boolean isItalic = textStyle.isItalic();
        if (isItalic != null) {
            font.setItalic(textStyle.isItalic());
        }
        final Byte underline = textStyle.getUnderline();
        if (underline != null) {
            font.setUnderline(textStyle.getUnderline());
        }
        return font;
    }

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("workbook", workbook)
                .add("fontCharset", fontCharset)
                .add("layoutStyles", layoutStyles)
                .add("textStyles", textStyles)
                .add("layoutTextStyles", layoutTextStyles)
                .toString();
    }

    public void checkAdjustHeaderCells(Cell cell, LayoutStyle layoutStyle) {
        if (layoutStyle == null) {
            return;
        }
        final Boolean autoWidth = layoutStyle.isAutoWidth();
        final GeometryDetails geometryDetails = layoutStyle.getGeometryDetails();
        if (autoWidth != null && autoWidth || geometryDetails != null && geometryDetails.getWidth() != null) {
            needAdjustHeaderCells.put(cell, layoutStyle);
        }
    }
}
