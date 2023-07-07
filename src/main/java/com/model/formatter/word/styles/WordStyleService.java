package com.model.formatter.word.styles;

import com.google.common.base.MoreObjects;
import com.model.domain.DocumentItem;
import com.model.domain.Heading;
import com.model.domain.Picture;
import com.model.domain.TextItem;
import com.model.domain.styles.BorderStyle;
import com.model.domain.styles.LayoutStyle;
import com.model.domain.styles.LayoutTextStyle;
import com.model.domain.styles.Style;
import com.model.domain.styles.StyleService;
import com.model.domain.styles.TextStyle;
import com.model.domain.styles.constants.BorderWeight;
import com.model.domain.styles.constants.Color;
import com.model.domain.styles.constants.HorAlignment;
import com.model.domain.styles.constants.PictureFormat;
import com.model.domain.styles.constants.VertAlignment;
import com.model.domain.styles.geometry.GeometryDetails;
import com.model.formatter.word.DocDetails;
import com.model.utils.ConverterUtils;
import com.model.utils.LocalizedNumberUtils;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableWidthType;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPBdr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class WordStyleService extends StyleService implements DocDetails {
    /**
     * Reverse twip (1/567) constant for cm
     */
    private static final int DOCX_HEADING_CONST = 567;

    /**
     * Reverse inch (1/1440) constant for width in inch
     */
    private static final int DOCX_INCH_CONST = 1440;

    /**
     * Map of native xwpf border types.
     * Key - type BorderWeight, value - Border
     */
    private static final Map<BorderWeight, Borders> borderMap =
        new HashMap<BorderWeight, Borders>() {{
            put(null, Borders.NONE);
            put(BorderWeight.NONE, Borders.NONE);
            put(BorderWeight.THIN, Borders.THIN_THICK_MEDIUM_GAP);
            put(BorderWeight.MEDIUM, Borders.THICK_THIN_MEDIUM_GAP);
            put(BorderWeight.THICK, Borders.THICK);
            put(BorderWeight.DOUBLE, Borders.DOUBLE);
            put(BorderWeight.DOTTED, Borders.DOTTED);
            put(BorderWeight.DASHED, Borders.DASHED);
        }};

    /**
     * Map of native xwpf horizontal paragraph layout types
     * Key - HorAlignment type, value - ParagraphAlignment
     */
    private static final Map<HorAlignment, ParagraphAlignment> horizontalAlignmentMap =
        new HashMap<HorAlignment, ParagraphAlignment>() {{
            put(null, null);
            put(HorAlignment.GENERAL, ParagraphAlignment.BOTH);
            put(HorAlignment.LEFT, ParagraphAlignment.LEFT);
            put(HorAlignment.CENTER, ParagraphAlignment.CENTER);
            put(HorAlignment.RIGHT, ParagraphAlignment.RIGHT);
        }};

    /**
     * Map of native xwpf vertical text layout types
     * Key - type VertAlignment, value - TextAlignment
     */
    private static final Map<VertAlignment, TextAlignment> verticalAlignmentMap =
        new HashMap<VertAlignment, TextAlignment>() {{
            put(null, null);
            put(VertAlignment.TOP, TextAlignment.TOP);
            put(VertAlignment.CENTER, TextAlignment.CENTER);
            put(VertAlignment.BOTTOM, TextAlignment.BOTTOM);
        }};

    /**
     * Map of native xwpf vertical text layout types
     * Key - type VertAlignment, value - XWPFTableCell.XWPFVertAlign
     */
    private static final Map<VertAlignment, XWPFTableCell.XWPFVertAlign> verticalAlignmentCellMap =
        new HashMap<VertAlignment, XWPFTableCell.XWPFVertAlign>() {{
            put(null, null);
            put(VertAlignment.TOP, XWPFTableCell.XWPFVertAlign.TOP);
            put(VertAlignment.CENTER, XWPFTableCell.XWPFVertAlign.CENTER);
            put(VertAlignment.BOTTOM, XWPFTableCell.XWPFVertAlign.BOTTOM);
        }};

    /**
     * Map of native xwpf picture types
     * Key - type PictureFormat, value - integer {@link org.apache.poi.xwpf.usermodel.Document}
     */
    private static final Map<PictureFormat, Integer> pictureFormatMap =
        new HashMap<PictureFormat, Integer>() {{
            put(null, -1);
            put(PictureFormat.JPEG, XWPFDocument.PICTURE_TYPE_JPEG);
            put(PictureFormat.JPG, XWPFDocument.PICTURE_TYPE_JPEG);
            put(PictureFormat.PNG, XWPFDocument.PICTURE_TYPE_PNG);
            put(PictureFormat.WMF, XWPFDocument.PICTURE_TYPE_WMF);
            put(PictureFormat.EMF, XWPFDocument.PICTURE_TYPE_EMF);
            put(PictureFormat.DIB, XWPFDocument.PICTURE_TYPE_DIB);
            put(PictureFormat.PICT, XWPFDocument.PICTURE_TYPE_PICT);
        }};

    private final FontCharset fontCharset;

    public WordStyleService(FontCharset fontCharset, DecimalFormat decimalFormat) {
        this.fontCharset = fontCharset;
        this.decimalFormat = decimalFormat;
    }

    protected Dimension getPictureDimension(Picture picture, LayoutStyle layoutStyle) throws IOException {
        int width = 0;
        int height = 0;
        if (layoutStyle != null) {
            final GeometryDetails geometryDetails = layoutStyle.getGeometryDetails();
            if (geometryDetails != null) {
                if (geometryDetails.getHeight() != null
                    && geometryDetails.getHeight().getValueFor(EXTENSION).isPresent()
                ) {
                    height = ConverterUtils.<Integer>convert(geometryDetails.getHeight().getValueFor(EXTENSION).get());
                } else {
                    height = picture.getHeightFromData();
                }
                if (geometryDetails.getWidth() != null
                    && geometryDetails.getWidth().getValueFor(EXTENSION).isPresent()
                ) {
                    width = ConverterUtils.<Integer>convert(geometryDetails.getWidth().getValueFor(EXTENSION).get());
                } else {
                    width = picture.getWidthFromData();
                }
            }
        }
        return new Dimension(width, height);
    }

    protected void convertGeometryRotation(XWPFPicture xwpfPicture, LayoutStyle layoutStyle) {
        final GeometryDetails geometryDetails = layoutStyle.getGeometryDetails();
        if (geometryDetails == null || geometryDetails.getAngle() == null) {
            return;
        }
        final CTTransform2D transform2D = xwpfPicture.getCTPicture().getSpPr().getXfrm();
        geometryDetails
            .getAngle()
            .getValueFor(EXTENSION)
            .ifPresent(v -> transform2D.setRot(ConverterUtils.<Integer>convert(v)));
    }

    /**
     * Adds text/picture/data to XWPFRun
     *
     * @param item  document item
     * @param run   XWPFRun run
     * @param style item's style
     * @throws ParseException         number in string could not be resolved
     * @throws IOException            if reading the picture-data from the stream fails
     * @throws InvalidFormatException if the format of the picture is not known
     */
    protected void addItemToRun(DocumentItem item, XWPFRun run, Style style)
        throws ParseException, IOException, InvalidFormatException {
        if (item instanceof TextItem<?>) {
            final TextItem<?> textItem = (TextItem<?>) item;
            final String text = LocalizedNumberUtils.applyDecimalFormat(textItem.getText(), style, decimalFormat);
            run.setText(text);
        }
        if (item instanceof Picture) {
            final Picture picture = (Picture) item;
            final LayoutStyle layoutStyle = LayoutStyle.extractLayoutStyle(style);
            final Dimension dimension = getPictureDimension(picture, layoutStyle);
            final int picFormat = toWordPictureFormat(picture.getPictureFormat());
            final XWPFPicture xwpfPicture = run.addPicture(
                picture.getData().getInputStream(),
                picFormat,
                picture.getName(),
                Units.toEMU(dimension.getWidth()),
                Units.toEMU(dimension.getHeight())
            );
            convertGeometryRotation(xwpfPicture, layoutStyle);
        }
    }

    public static StyleService create(FontCharset fontCharset, DecimalFormat decimalFormat) {
        return new WordStyleService(fontCharset, decimalFormat);
    }

    /**
     * Adjusts table width
     *
     * @param style     table LayoutStyle
     * @param docxTable native XWPFTable table
     */
    public void handleTable(Style style, XWPFTable docxTable) {
        if (style instanceof LayoutStyle) {
            final LayoutStyle tableLayoutStyle = (LayoutStyle) style;
            final boolean isTableAutoWidth = tableLayoutStyle.isAutoWidth();
            final GeometryDetails geometryDetails = tableLayoutStyle.getGeometryDetails();
            if (geometryDetails != null && geometryDetails.getWidth() != null) {
                geometryDetails
                    .getWidth()
                    .getValueFor(EXTENSION)
                    .ifPresent(value -> docxTable.setWidth(ConverterUtils.<Integer>convert(value) * DOCX_INCH_CONST));
            } else if (isTableAutoWidth) {
                docxTable.setWidth("auto");
            }
        }
    }

    /**
     * Creates a stylized run in paragraph of docx elements:
     * <p>
     * text style applied to {@link org.apache.poi.xwpf.usermodel.XWPFRun}
     * layout style applied to {@link org.apache.poi.xwpf.usermodel.XWPFParagraph}
     *
     * @param item    text element
     * @param element docx object: XWPFParagraph or XWPFTableCell
     * @throws Exception   on bad decimalFormat or font can't be found error
     * @throws IOException if picture details reading failure
     */
    public void handleCustomItem(DocumentItem item, Object element) throws Exception {
        final Style style = extractStyleFor(item).orElse(item.getStyle());
        if (element instanceof XWPFParagraph) {
            final XWPFParagraph paragraph = (XWPFParagraph) element;
            final XWPFRun run = paragraph.createRun();
            addItemToRun(item, run, style);
            convertStyleToElement(style, run, paragraph);
            if (item instanceof Heading) {
                paragraph.setIndentationHanging(((Heading) item).getDepth() * DOCX_HEADING_CONST);
            }
        } else if (element instanceof XWPFTableCell) {
            final XWPFTableCell cell = (XWPFTableCell) element;
            final XWPFParagraph paragraph = cell.getParagraphs().get(0);
            final XWPFRun run = paragraph.createRun();
            addItemToRun(item, run, style);
            if (style instanceof TextStyle) {
                convertStyleToElement(style, run, paragraph);
            } else if (style instanceof LayoutStyle) {
                final LayoutStyle layoutStyle = (LayoutStyle) style;
                convertLayoutStyleToCell(cell, layoutStyle);
            } else if (style instanceof LayoutTextStyle) {
                final LayoutTextStyle layoutTextStyle = (LayoutTextStyle) style;
                final TextStyle textStyle = layoutTextStyle.getTextStyle();
                final LayoutStyle layoutStyle = layoutTextStyle.getLayoutStyle();
                convertStyleToElement(textStyle, run, paragraph);
                convertLayoutStyleToCell(cell, layoutStyle);
            }
        }
    }

    /**
     * Converts style to native text style or element style
     *
     * @param style        input style
     * @param innerElement text element
     * @param outerElement element with text element
     */
    public void convertStyleToElement(Style style, XWPFRun innerElement, XWPFParagraph outerElement) {
        if (style instanceof TextStyle) {
            final TextStyle textStyle = (TextStyle) style;
            final Short fontSize = textStyle.getFontSize();
            if (fontSize != null) {
                innerElement.setFontSize(fontSize);
            }
            final String color = toWordColor(textStyle.getColor());
            if (color != null) {
                innerElement.setColor(color);
            }
            if (StringUtils.hasText(textStyle.getFontNameResource())) {
                innerElement.setFontFamily(textStyle.getFontNameResource());
            }
            final Boolean isBold = textStyle.isBold();
            if (isBold != null) {
                innerElement.setBold(isBold);
            }
            final Boolean isItalic = textStyle.isItalic();
            if (isItalic != null) {
                innerElement.setItalic(textStyle.isItalic());
            }
            final Byte underline = textStyle.getUnderline();
            if (underline != null) {
                innerElement.setUnderline(UnderlinePatterns.SINGLE);
            }
        } else if (style instanceof LayoutStyle) {
            convertLayoutStyleToElement(outerElement, (LayoutStyle) style);
        } else if (style instanceof LayoutTextStyle) {
            final LayoutTextStyle layoutTextStyle = (LayoutTextStyle) style;
            final TextStyle textStyle = layoutTextStyle.getTextStyle();
            final LayoutStyle layoutStyle = layoutTextStyle.getLayoutStyle();
            convertStyleToElement(textStyle, innerElement, outerElement);
            convertStyleToElement(layoutStyle, innerElement, outerElement);
        }
    }

    /**
     * Decorates the native XWPFParagraph with a LayoutStyle
     *
     * @param element     decoration element
     * @param layoutStyle input LayoutStyle style
     */
    public void convertLayoutStyleToElement(XWPFParagraph element, LayoutStyle layoutStyle) {
        convertGroundColor(element, layoutStyle);
        convertBorders(element, layoutStyle);
        convertHorizontalAlignment(element, layoutStyle);
        convertVerticalAlignment(element, layoutStyle);
    }

    public void convertLayoutStyleToCell(XWPFTableCell cell, LayoutStyle layoutStyle) {
        cell.setColor(toWordColor(layoutStyle.getFillBackgroundColor()));
        convertCellBorders(cell, layoutStyle);
        final XWPFParagraph paragraph = cell.getParagraphs().get(0);
        convertHorizontalAlignment(paragraph, layoutStyle);
        convertVerticalAlignmentCell(cell, layoutStyle);
        final boolean isCellAutoWidth = layoutStyle.isAutoWidth();
        final CTTblWidth tblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
        final GeometryDetails geometryDetails = layoutStyle.getGeometryDetails();
        if (geometryDetails != null && geometryDetails.getWidth() != null) {
            geometryDetails
                .getWidth()
                .getValueFor(EXTENSION)
                .ifPresent(value -> {
                    cell.setWidthType(TableWidthType.DXA);
                    cell.setWidth(String.valueOf(ConverterUtils.<Integer>convert(value)));
                });
        } else if (isCellAutoWidth) {
            tblWidth.setType(STTblWidth.AUTO);
        }
    }

    /**
     * Applies the layoutStyle backfill setting to the XWPFParagraph element
     *
     * @param element     XWPFParagraph element
     * @param layoutStyle input style
     */
    public static void convertGroundColor(XWPFParagraph element, LayoutStyle layoutStyle) {
        if (element.getCTP().getPPr() == null) {
            element.getCTP().addNewPPr();
        }
        final CTPPr ppr = element.getCTP().getPPr();
        if (ppr.getShd() != null) {
            ppr.unsetShd();
        }
        ppr.addNewShd();
        final CTShd shd = ppr.getShd();
        shd.setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd.CLEAR);
        shd.setColor("auto");
        final String fillColor = toWordColor(layoutStyle.getFillBackgroundColor());
        if (fillColor != null) {
            shd.setFill(fillColor);
        }
    }

    /**
     * Sets borders type and color for XWPFParagraph element
     *
     * @param paragraph   docx XWPFParagraph
     * @param layoutStyle input layout style
     */
    public static void convertBorders(XWPFParagraph paragraph, LayoutStyle layoutStyle) {
        final BorderStyle borderTop = layoutStyle.getBorderTop();
        final BorderStyle borderLeft = layoutStyle.getBorderLeft();
        final BorderStyle borderRight = layoutStyle.getBorderRight();
        final BorderStyle borderBottom = layoutStyle.getBorderBottom();
        final CTP ctp = paragraph.getCTP();
        if (ctp.getPPr() == null) {
            ctp.addNewPPr();
        }
        final CTPPr ppr = ctp.getPPr();
        if (ppr.getPBdr() == null) {
            ppr.addNewPBdr();
        }
        final CTPBdr pbd = ppr.getPBdr();
        convertBorderTop(borderTop, paragraph, pbd);
        convertBorderLeft(borderLeft, paragraph, pbd);
        convertBorderRight(borderRight, paragraph, pbd);
        convertBorderBottom(borderBottom, paragraph, pbd);
    }

    /**
     * Sets borders type and color for XWPFTableCell element
     *
     * @param cell        docx XWPFTableCell
     * @param layoutStyle input layout style
     */
    public void convertCellBorders(XWPFTableCell cell, LayoutStyle layoutStyle) {
        final BorderStyle borderTop = layoutStyle.getBorderTop();
        final BorderStyle borderLeft = layoutStyle.getBorderLeft();
        final BorderStyle borderRight = layoutStyle.getBorderRight();
        final BorderStyle borderBottom = layoutStyle.getBorderBottom();

        final CTTc ctTc = cell.getCTTc();
        final CTTcPr tcPr = ctTc.addNewTcPr();
        final CTTcBorders border = tcPr.addNewTcBorders();

        final CTBorder top = border.addNewTop();
        final CTBorder left = border.addNewLeft();
        final CTBorder right = border.addNewRight();
        final CTBorder bottom = border.addNewBottom();

        top.setColor(toWordColor(borderTop.getColor()));
        top.setVal(STBorder.Enum.forInt(toWordBorder(borderTop.getWeight()).getValue()));
        left.setColor(toWordColor(borderLeft.getColor()));
        left.setVal(STBorder.Enum.forInt(toWordBorder(borderLeft.getWeight()).getValue()));
        right.setColor(toWordColor(borderRight.getColor()));
        right.setVal(STBorder.Enum.forInt(toWordBorder(borderRight.getWeight()).getValue()));
        bottom.setColor(toWordColor(borderBottom.getColor()));
        bottom.setVal(STBorder.Enum.forInt(toWordBorder(borderBottom.getWeight()).getValue()));
    }

    public static void convertBorderTop(BorderStyle borderStyle, XWPFParagraph element, CTPBdr pbd) {
        if (pbd.getTop() == null) {
            pbd.addNewTop();
        }
        if (borderStyle != null) {
            pbd.getTop().setColor(toWordColor(borderStyle.getColor()));
            final Borders border = toWordBorder(borderStyle.getWeight());
            element.setBorderTop(border);
        }
    }

    public static void convertBorderLeft(BorderStyle borderStyle, XWPFParagraph element, CTPBdr pbd) {
        if (pbd.getLeft() == null) {
            pbd.addNewLeft();
        }
        if (borderStyle != null) {
            pbd.getLeft().setColor(toWordColor(borderStyle.getColor()));
            final Borders border = toWordBorder(borderStyle.getWeight());
            element.setBorderLeft(border);
        }
    }

    public static void convertBorderRight(BorderStyle borderStyle, XWPFParagraph element, CTPBdr pbd) {
        if (pbd.getRight() == null) {
            pbd.addNewRight();
        }
        if (borderStyle != null) {
            pbd.getRight().setColor(toWordColor(borderStyle.getColor()));
            final Borders border = toWordBorder(borderStyle.getWeight());
            element.setBorderRight(border);
        }
    }

    public static void convertBorderBottom(BorderStyle borderStyle, XWPFParagraph element, CTPBdr pbd) {
        if (pbd.getBottom() == null) {
            pbd.addNewBottom();
        }
        if (borderStyle != null) {
            pbd.getBottom().setColor(toWordColor(borderStyle.getColor()));
            final Borders border = toWordBorder(borderStyle.getWeight());
            element.setBorderBottom(border);
        }
    }

    public static void convertHorizontalAlignment(XWPFParagraph element, LayoutStyle layoutStyle) {
        final ParagraphAlignment horAlignment = toWordHorAlignment(layoutStyle.getHorAlignment());
        if (horAlignment != null) {
            element.setAlignment(horAlignment);
        }
    }

    public static void convertVerticalAlignment(XWPFParagraph element, LayoutStyle layoutStyle) {
        final TextAlignment vertAlignment = toWordVertAlignment(layoutStyle.getVertAlignment());
        if (vertAlignment != null) {
            element.setVerticalAlignment(vertAlignment);
        }
    }

    public static void convertVerticalAlignmentCell(XWPFTableCell cell, LayoutStyle layoutStyle) {
        final XWPFTableCell.XWPFVertAlign vertAlignmentCell = toWordVertAlignmentCell(layoutStyle.getVertAlignment());
        if (vertAlignmentCell != null) {
            cell.setVerticalAlignment(vertAlignmentCell);
        }
    }

    /**
     * Utility Methods
     **/
    public static Borders toWordBorder(BorderWeight border) {
        if (borderMap.containsKey(border)) {
            return borderMap.get(border);
        } else {
            throw new IllegalArgumentException("Undefined BorderWeight type");
        }
    }

    public static String toWordColor(Color color) {
        if (color == null) {
            return null;
        }
        return color.buildColorString();
    }

    public static TextAlignment toWordVertAlignment(VertAlignment vertAlignment) {
        if (verticalAlignmentMap.containsKey(vertAlignment)) {
            return verticalAlignmentMap.get(vertAlignment);
        } else {
            throw new IllegalArgumentException("Undefined VerticalAlignment type");
        }
    }

    public static XWPFTableCell.XWPFVertAlign toWordVertAlignmentCell(VertAlignment vertAlignment) {
        if (verticalAlignmentCellMap.containsKey(vertAlignment)) {
            return verticalAlignmentCellMap.get(vertAlignment);
        } else {
            throw new IllegalArgumentException("Undefined VerticalAlignmentCell type");
        }
    }

    public static ParagraphAlignment toWordHorAlignment(HorAlignment horAlignment) {
        if (horizontalAlignmentMap.containsKey(horAlignment)) {
            return horizontalAlignmentMap.get(horAlignment);
        } else {
            throw new IllegalArgumentException("Undefined HorizontalAlignment type");
        }
    }

    public static int toWordPictureFormat(PictureFormat pictureFormat) {
        if (pictureFormatMap.containsKey(pictureFormat)) {
            return pictureFormatMap.get(pictureFormat);
        } else {
            throw new IllegalArgumentException("Undefined PictureFormat type");
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("fontCharset", fontCharset)
            .add("parent", super.toString())
            .toString();
    }

    @Override
    public void writeStyles(Object o) {
        /*https://stackoverflow.com/questions/61251082/apache-poi-for-word-create-custom-style-with-textalignment*/
    }
}
