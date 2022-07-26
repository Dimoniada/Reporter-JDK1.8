package com.reporter.formatter.word.styles;

import com.google.common.base.MoreObjects;
import com.reporter.domain.Heading;
import com.reporter.domain.TextItem;
import com.reporter.domain.styles.*;
import com.reporter.domain.styles.constants.BorderWeight;
import com.reporter.domain.styles.constants.Color;
import com.reporter.domain.styles.constants.HorAlignment;
import com.reporter.domain.styles.constants.VertAlignment;
import com.reporter.utils.LocalizedNumberUtils;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class WordStyleService extends StyleService {
    /**
     * Reverse twip (1/567) constant for cm
     */
    private static final int XLSX_HEADING_CONST = 567;

    /**
     * Reverse inch (1/1440) constant for width in inch
     */
    private static final int XLSX_INCH_CONST = 1440;

    /**
     * Map of native xwpf border types.
     * Key - type BorderWeight, value - Border
     */
    private static final Map<BorderWeight, Borders> borderMap =
        new HashMap<BorderWeight, Borders>() {{
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
            put(VertAlignment.TOP, XWPFTableCell.XWPFVertAlign.TOP);
            put(VertAlignment.CENTER, XWPFTableCell.XWPFVertAlign.CENTER);
            put(VertAlignment.BOTTOM, XWPFTableCell.XWPFVertAlign.BOTTOM);
        }};

    private final FontCharset fontCharset;

    public WordStyleService(FontCharset fontCharset, DecimalFormat decimalFormat) {
        this.fontCharset = fontCharset;
        this.decimalFormat = decimalFormat;
    }

    public static StyleService create(FontCharset fontCharset, DecimalFormat decimalFormat) {
        return new WordStyleService(fontCharset, decimalFormat);
    }

    /**
     * Adjusts table width
     *
     * @param style table LayoutStyle
     * @param docxTable native XWPFTable table
     */
    public void handleTable(Style style, XWPFTable docxTable) {
        if (style instanceof LayoutStyle) {
            final LayoutStyle tableLayoutStyle = (LayoutStyle) style;
            final boolean isTableAutoWidth = tableLayoutStyle.isAutoWidth();
            if (!isTableAutoWidth && tableLayoutStyle.getWidth() != 0) {
                final int tableWidth = tableLayoutStyle.getWidth();
                docxTable.setWidth(tableWidth * XLSX_INCH_CONST);
            } else {
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
     * @throws Exception on bad decimalFormat or font can't be found error
     */
    public void handleCustomText(TextItem<?> item, Object element) throws Exception {
        final Style style = extractStyleFor(item).orElse(item.getStyle());
        final String text = LocalizedNumberUtils.applyDecimalFormat(item, decimalFormat);
        if (element instanceof XWPFParagraph) {
            final XWPFParagraph paragraph = (XWPFParagraph) element;
            final XWPFRun run = paragraph.createRun();
            run.setText(text);

            convertStyleToElement(style, run, paragraph);
            if (item instanceof Heading) {
                paragraph.setIndentationHanging(((Heading) item).getDepth() * XLSX_HEADING_CONST);
            }
        } else if (element instanceof XWPFTableCell) {
            final XWPFTableCell cell = (XWPFTableCell) element;
            final XWPFParagraph paragraph = cell.getParagraphs().get(0);
            final XWPFRun run = paragraph.createRun();
            run.setText(text);
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
            innerElement.setFontSize(textStyle.getFontSize());
            innerElement.setColor(toWordColor(textStyle.getColor()));
            if (StringUtils.hasText(textStyle.getFontNameResource())) {
                innerElement.setFontFamily(textStyle.getFontNameResource());
            }
            innerElement.setBold(textStyle.isBold());
            innerElement.setItalic(textStyle.isItalic());
            if (textStyle.getUnderline() != 0) {
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
        if (!isCellAutoWidth && layoutStyle.getWidth() > 0) {
            final int width = layoutStyle.getWidth();
            cell.setWidthType(TableWidthType.DXA);
            cell.setWidth(String.valueOf(width));
        } else {
            tblWidth.setType(STTblWidth.AUTO);
        }
    }

    private static XWPFStyle createNewStyle(XWPFStyles styles, STStyleType.Enum styleType, String styleId) {
        if (styles == null || styleId == null) {
            return null;
        }
        XWPFStyle style = styles.getStyle(styleId);
        if (style == null) {
            final CTStyle ctStyle = CTStyle.Factory.newInstance();
            ctStyle.addNewName().setVal(styleId);
            ctStyle.setCustomStyle(STOnOff.GREATER_THAN);
            style = new XWPFStyle(ctStyle, styles);
            style.setType(styleType);
            style.setStyleId(styleId);
            styles.addStyle(style);
        }
        return style;
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
        shd.setFill(toWordColor(layoutStyle.getFillBackgroundColor()));
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
        pbd.getTop().setColor(toWordColor(borderStyle.getColor()));
        final Borders border = toWordBorder(borderStyle.getWeight());
        element.setBorderTop(border);
    }

    public static void convertBorderLeft(BorderStyle borderStyle, XWPFParagraph element, CTPBdr pbd) {
        if (pbd.getLeft() == null) {
            pbd.addNewLeft();
        }
        pbd.getLeft().setColor(toWordColor(borderStyle.getColor()));
        final Borders border = toWordBorder(borderStyle.getWeight());
        element.setBorderLeft(border);
    }

    public static void convertBorderRight(BorderStyle borderStyle, XWPFParagraph element, CTPBdr pbd) {
        if (pbd.getRight() == null) {
            pbd.addNewRight();
        }
        pbd.getRight().setColor(toWordColor(borderStyle.getColor()));
        final Borders border = toWordBorder(borderStyle.getWeight());
        element.setBorderRight(border);
    }

    public static void convertBorderBottom(BorderStyle borderStyle, XWPFParagraph element, CTPBdr pbd) {
        if (pbd.getBottom() == null) {
            pbd.addNewBottom();
        }
        pbd.getBottom().setColor(toWordColor(borderStyle.getColor()));
        final Borders border = toWordBorder(borderStyle.getWeight());
        element.setBorderBottom(border);
    }

    public static void convertHorizontalAlignment(XWPFParagraph element, LayoutStyle layoutStyle) {
        final ParagraphAlignment horAlignment = toWordHorAlignment(layoutStyle.getHorAlignment());
        element.setAlignment(horAlignment);
    }

    public static void convertVerticalAlignment(XWPFParagraph element, LayoutStyle layoutStyle) {
        final TextAlignment vertAlignment = toWordVertAlignment(layoutStyle.getVertAlignment());
        element.setVerticalAlignment(vertAlignment);
    }

    public static void convertVerticalAlignmentCell(XWPFTableCell cell, LayoutStyle layoutStyle) {
        final XWPFTableCell.XWPFVertAlign vertAlignmentCell = toWordVertAlignmentCell(layoutStyle.getVertAlignment());
        cell.setVerticalAlignment(vertAlignmentCell);
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
