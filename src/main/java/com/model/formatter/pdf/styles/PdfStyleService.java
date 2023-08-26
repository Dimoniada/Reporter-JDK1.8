package com.model.formatter.pdf.styles;

import com.google.common.base.MoreObjects;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.DottedBorder;
import com.itextpdf.layout.borders.DoubleBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AbstractElement;
import com.itextpdf.layout.element.BlockElement;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.Transform;
import com.itextpdf.layout.properties.UnitValue;
import com.model.domain.FontService;
import com.model.domain.Heading;
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
import com.model.domain.styles.constants.VertAlignment;
import com.model.domain.styles.geometry.GeometryDetails;
import com.model.formatter.pdf.PdfDetails;
import com.model.formatter.pdf.renders.CustomCellRenderer;
import com.model.formatter.pdf.renders.CustomParagraphRenderer;
import com.model.utils.ConverterUtils;
import com.model.utils.LocalizedNumberUtils;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * The class caches {@link TextStyle} and
 * transforms it and {@link LayoutStyle}
 * in pdf style iText {@link PdfFont} and
 * pdf cell layout style
 */
public final class PdfStyleService extends StyleService implements PdfDetails {

    private static final int PDF_HEADING_CONST = 20;

    /**
     * Map of native itextpdf border types.
     * Key - type BorderWeight, value - Border
     */
    private static final Map<BorderWeight, com.itextpdf.layout.borders.Border> borderWeightMap =
        new HashMap<BorderWeight, com.itextpdf.layout.borders.Border>() {{
            put(null, com.itextpdf.layout.borders.Border.NO_BORDER);
            put(BorderWeight.NONE, com.itextpdf.layout.borders.Border.NO_BORDER);
            put(BorderWeight.THIN, new SolidBorder(1));
            put(BorderWeight.MEDIUM, new SolidBorder(2));
            put(BorderWeight.THICK, new SolidBorder(3));
            put(BorderWeight.DOUBLE, new DoubleBorder(2));   //width - distance between borders
            put(BorderWeight.DOTTED, new DottedBorder(1));   //width – width of the border
            put(BorderWeight.DASHED, new DashedBorder(1));   //width – width of the border
        }};

    /**
     * Map of native itextpdf canvas ILineDrawer drawing types
     * Key - type BorderWeight, value - ILineDrawer
     */
    private static final Map<BorderWeight, com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer> lineWeightMap =
        new HashMap<BorderWeight, com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer>() {{
            put(null, null);
            put(BorderWeight.NONE, null);
            put(BorderWeight.THIN, new SolidLine(1));    //width - distance between borders
            put(BorderWeight.MEDIUM, new SolidLine(2));  //width - distance between borders
            put(BorderWeight.THICK, new SolidLine(3));   //width - distance between borders
            put(BorderWeight.DOTTED, new DottedLine(1)); //width – width of the border
            put(BorderWeight.DASHED, new DashedLine(1)); //width – width of the border
        }};

    /**
     * Map of native itextpdf horizontal text layout types
     * Key - HorAlignment type, value - TextAlignment
     */
    private static final Map<HorAlignment, com.itextpdf.layout.properties.TextAlignment> horizontalAlignmentMap =
        new HashMap<HorAlignment, com.itextpdf.layout.properties.TextAlignment>() {{
            put(null, null);
            put(HorAlignment.GENERAL, com.itextpdf.layout.properties.TextAlignment.JUSTIFIED);
            put(HorAlignment.LEFT, com.itextpdf.layout.properties.TextAlignment.LEFT);
            put(HorAlignment.CENTER, com.itextpdf.layout.properties.TextAlignment.CENTER);
            put(HorAlignment.RIGHT, com.itextpdf.layout.properties.TextAlignment.RIGHT);
        }};

    /**
     * Map of native itextpdf vertical text layout types
     * Key - type VertAlignment, value - VerticalAlignment
     */
    private static final Map<VertAlignment, com.itextpdf.layout.properties.VerticalAlignment> verticalAlignmentMap =
        new HashMap<VertAlignment, com.itextpdf.layout.properties.VerticalAlignment>() {{
            put(null, null);
            put(VertAlignment.TOP, com.itextpdf.layout.properties.VerticalAlignment.TOP);
            put(VertAlignment.CENTER, com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
            put(VertAlignment.BOTTOM, com.itextpdf.layout.properties.VerticalAlignment.BOTTOM);
        }};

    /**
     * Cached PdfFont font map by TextStyle text style
     * within one document
     */
    private final Map<TextStyle, PdfFont> textStyles = new HashMap<>();
    /**
     * Encoding for characters of PdfFont fonts
     */
    private String encoding;

    private PdfStyleService(
        String encoding,
        FontService fontService,
        DecimalFormat decimalFormat
    ) {
        this.encoding = encoding;
        this.fontService = fontService;
        this.decimalFormat = decimalFormat;
    }

    /**
     * Converts color to native itextpdf element color
     *
     * @param color input color
     * @return itextpdf element color
     */
    public static com.itextpdf.kernel.colors.Color
    toPdfColor(Color color) {
        if (color == null) {
            return null;
        }
        return new DeviceRgb(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Converts border to native itextpdf element border
     *
     * @param border input border type
     * @return itextpdf element border
     */
    public static com.itextpdf.layout.borders.Border
    toPdfBorder(BorderWeight border) {
        if (borderWeightMap.containsKey(border)) {
            return borderWeightMap.get(border);
        } else {
            throw new IllegalArgumentException("Undefined BorderWeight type");
        }
    }

    /**
     * Converts horizontal alignment type to itextpdf native type
     *
     * @param horAlignment horizontal alignment input type
     * @return itextpdf horizontal alignment type
     */
    public static com.itextpdf.layout.properties.TextAlignment
    toPdfHorAlignment(HorAlignment horAlignment) {
        if (horizontalAlignmentMap.containsKey(horAlignment)) {
            return horizontalAlignmentMap.get(horAlignment);
        } else {
            throw new IllegalArgumentException("Undefined HorizontalAlignment type");
        }
    }

    /**
     * Converts vertical alignment type to itextpdf native type
     *
     * @param vertAlignment vertical alignment input type
     * @return itextpdf vertical alignment type
     */
    public static com.itextpdf.layout.properties.VerticalAlignment
    toPdfVertAlignment(VertAlignment vertAlignment) {
        if (verticalAlignmentMap.containsKey(vertAlignment)) {
            return verticalAlignmentMap.get(vertAlignment);
        } else {
            throw new IllegalArgumentException("Undefined VerticalAlignment type");
        }
    }

    /**
     * Converts border to native itextpdf drawing type ILineDrawer
     *
     * @param border input border type
     * @return itextpdf draw type
     */
    public static com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer
    toPdfILineDrawer(BorderWeight border) {
        if (lineWeightMap.containsKey(border)) {
            return lineWeightMap.get(border);
        } else {
            throw new IllegalArgumentException("Undefined ILineDrawer type");
        }
    }

    public static PdfStyleService create(
        String encoding,
        FontService fontService,
        DecimalFormat decimalFormat
    ) {
        return new PdfStyleService(encoding, fontService, decimalFormat);
    }

    public static PdfStyleService create(String encoding, FontService fontService) {
        return new PdfStyleService(encoding, fontService, null);
    }

    public static PdfStyleService create(String encoding) {
        return new PdfStyleService(encoding, null, null);
    }

    public static PdfStyleService create() {
        return new PdfStyleService(FontCharset.DEFAULT.name(), null, null);
    }

    /**
     * Applies the layoutStyle backfill setting to the itextpdf element
     *
     * @param element     itextpdf element
     * @param layoutStyle input style
     */
    public static void convertGroundColor(AbstractElement<?> element, LayoutStyle layoutStyle) {
        element.setBackgroundColor(toPdfColor(layoutStyle.getFillBackgroundColor()));
    }

    /**
     * Calls the native setBorder method of the itextpdf element,
     * passing it BorderStyle style parameters
     *
     * @param borderStyle input border style
     * @param setBorder   itextpdf element method
     * @param isTableCell flag that the element is a table cell
     */
    public static void convertBorder(BorderStyle borderStyle, Function<Border, ?> setBorder, boolean isTableCell) {
        if (isTableCell && borderStyle == null) {
            return;
        }
        if (borderStyle != null) {
            final com.itextpdf.layout.borders.Border border = toPdfBorder(borderStyle.getWeight());
            convertBorderColor(border, borderStyle.getColor());
            setBorder.apply(border);
        }
    }

    /**
     * Sets the color of the native itextpdf border
     *
     * @param border itextpdf border
     * @param color  input color
     */
    public static void convertBorderColor(com.itextpdf.layout.borders.Border border, Color color) {
        if (border != null) {
            border.setColor(toPdfColor(color));
        }
    }

    /**
     * Sets the horizontal native itextpdf border type
     *
     * @param element     itextpdf border
     * @param layoutStyle input style
     */
    public static void convertHorizontalAlignment(AbstractElement<?> element, LayoutStyle layoutStyle) {
        element.setTextAlignment(toPdfHorAlignment(layoutStyle.getHorAlignment()));
    }

    /**
     * Sets the type of vertical native itextpdf border
     *
     * @param element     itextpdf border
     * @param layoutStyle input style
     */
    public static void convertVerticalAlignment(AbstractElement<?> element, LayoutStyle layoutStyle) {
        if (element instanceof BlockElement<?>) {
            final BlockElement<?> cell = (BlockElement<?>) element;
            cell.setVerticalAlignment(toPdfVertAlignment(layoutStyle.getVertAlignment()));
        }
    }

    /**
     * Removes space between characters in an itextpdf text element, depending on the layoutStyle
     *
     * @param element     text itextpdf element
     * @param layoutStyle input style
     */
    public static void convertShrinkToFit(AbstractElement<?> element, LayoutStyle layoutStyle) {
        final Boolean isShrinkToFit = layoutStyle.isShrinkToFit();
        if (isShrinkToFit != null && !isShrinkToFit) {
            element.setCharacterSpacing(0);
            element.setWordSpacing(0);
        }
    }

    /**
     * Adjusts width/height/angle/scale in an itextpdf text element, depending on the layoutStyle
     *
     * @param outerElement decoration element
     * @param layoutStyle  input style
     */
    public static void convertGeometryDetails(AbstractElement<?> outerElement, LayoutStyle layoutStyle) {
        final GeometryDetails geometryDetails = layoutStyle.getGeometryDetails();
        if (geometryDetails == null || !(outerElement instanceof BlockElement<?>)) {
            return;
        }
        // Width
        if (geometryDetails.getWidth() != null) {
            geometryDetails
                .getWidth()
                .getValueFor(EXTENSION)
                .ifPresent(value ->
                    ((BlockElement<?>) outerElement).setWidth(ConverterUtils.<Float>convert(value))
                );
        }
        // Height
        if (geometryDetails.getHeight() != null) {
            geometryDetails
                .getHeight()
                .getValueFor(EXTENSION)
                .ifPresent(value ->
                    ((BlockElement<?>) outerElement).setHeight(ConverterUtils.<Float>convert(value))
                );
        }
        // Rotation angle
        if (geometryDetails.getAngle() != null) {
            geometryDetails
                .getAngle()
                .getValueFor(EXTENSION)
                .ifPresent(value -> ((BlockElement<?>) outerElement)
                    .setRotationAngle((float) ConverterUtils.convert(value) * Math.PI / 180f)
                );
        }

        // Horizontal & Vertical scaling
        final AtomicReference<Float> scaleX = new AtomicReference<>(1f);
        if (geometryDetails.getScaleX() != null) {
            geometryDetails
                .getScaleX()
                .getValueFor(EXTENSION)
                .ifPresent(value -> scaleX.set(ConverterUtils.convert(value)));
        }
        final AtomicReference<Float> scaleY = new AtomicReference<>(1f);
        if (geometryDetails.getScaleY() != null) {
            geometryDetails
                .getScaleY()
                .getValueFor(EXTENSION)
                .ifPresent(value -> scaleY.set(ConverterUtils.convert(value)));
        }
        final UnitValue uv = new UnitValue(UnitValue.POINT, 0);
        final Transform transform = new Transform(1);
        transform
            .addSingleTransform(
                new Transform.SingleTransform(scaleX.get(), 0, 0, scaleY.get(), uv, uv)
            );
        outerElement
            .setProperty(
                Property.TRANSFORM,
                transform
            );
        // To apply Rotation center and scales
        if (outerElement instanceof Paragraph) {
            outerElement.setNextRenderer(new CustomParagraphRenderer((Paragraph) outerElement, geometryDetails));
        } else if (outerElement instanceof Cell) {
            outerElement.setNextRenderer(new CustomCellRenderer((Cell) outerElement, geometryDetails));
        }
    }

    /**
     * Implements, as needed for the format, writing styles to the native object
     *
     * @param o native object for writing styles {@link StyleService#styles}
     */
    @Override
    public void writeStyles(Object o) {
        /**/
    }

    /**
     * Creates a stylized bunch of pdf elements: text inside a paragraph.
     * A bundle is necessary for the correct processing of styles;
     * text style applied to {@link com.itextpdf.layout.element.Text}
     *
     * @param item text element
     * @param o    pdf document
     * @throws Exception on bad decimalFormat or font creation error
     */
    public void handleSimpleElement(TextItem<?> item, com.itextpdf.layout.Document o) throws Exception {
        final Text text =
            new Text(LocalizedNumberUtils.applyDecimalFormat(item.getText(), item.getStyle(), decimalFormat));
        final com.itextpdf.layout.element.Paragraph elParagraph = new com.itextpdf.layout.element.Paragraph(text);
        final Style style = extractStyleFor(item).orElse(item.getStyle());
        convertStyleToElement(style, text, elParagraph);
        if (item instanceof Heading) {
            elParagraph.setFirstLineIndent(((Heading) item).getDepth() * PDF_HEADING_CONST);
        }
        o.add(elParagraph);
    }

    /**
     * Creates a styled pdf table cell
     *
     * @param tableCustomCell TableCell or TableHeaderCell
     * @return native pdf cell
     * @throws Exception when converting style
     */
    public Cell handleTableCustomCell(TextItem<?> tableCustomCell) throws Exception {
        final Text text =
            new Text(
                LocalizedNumberUtils.applyDecimalFormat(
                    tableCustomCell.getText(),
                    tableCustomCell.getStyle(),
                    decimalFormat
                )
            );
        final com.itextpdf.layout.element.Paragraph paragraph = new com.itextpdf.layout.element.Paragraph(text);
        final Cell cell = new Cell().add(paragraph);
        final Style style = prepareStyleFrom(tableCustomCell);
        convertStyleToElement(style, text, cell);
        return cell;
    }

    /**
     * Converts style to native text style or element style
     * Depending on the style type, Text or
     * AbstractElement element, or both
     *
     * @param style        input style
     * @param innerElement text element
     * @param outerElement element with text element
     * @throws Exception when creating PdfFontService
     */
    public void convertStyleToElement(Style style, AbstractElement<?> innerElement, AbstractElement<?> outerElement)
        throws Exception {
        if (style instanceof TextStyle) {
            convertTextStyleToElement(innerElement, (TextStyle) style);
        } else if (style instanceof LayoutStyle) {
            convertLayoutStyleToElement(outerElement, (LayoutStyle) style);
        } else if (style instanceof LayoutTextStyle) {
            convertTextStyleToElement(innerElement, ((LayoutTextStyle) style).getTextStyle());
            convertLayoutStyleToElement(outerElement, ((LayoutTextStyle) style).getLayoutStyle());
        }
    }

    /**
     * Converts text style to AbstractElement element's native text style
     *
     * @param element   decoration element
     * @param textStyle input text style
     * @throws Exception when creating PdfFontService
     */
    public void convertTextStyleToElement(AbstractElement<?> element, TextStyle textStyle) throws Exception {
        if (textStyle == null) {
            return;
        }
        final PdfFont font;
        if (fontService == null) {
            fontService = FontService.create();
        }
        fontService.initializeFonts();
        final Locale fontLocale = textStyle.getFontLocale();
        if (textStyles.containsKey(textStyle)) {
            font = textStyles.get(textStyle);
        } else {
            if (fontLocale == null || fontService.checkAvailableFontsLocale(fontLocale)) {
                try {
                    final Font embeddedFont = fontService.getFontResource(textStyle, fontLocale);
                    final Method method = Font.class.getDeclaredMethod("getFont2D");
                    method.setAccessible(true);
                    String rawFont = method.invoke(embeddedFont).toString();
                    rawFont = rawFont
                        .substring(rawFont.indexOf(" fileName=") + 10)
                        .replace("\r\n", "\n") + "\n";
                    rawFont = rawFont.substring(0, rawFont.indexOf("\n"));
                    final byte[] fontResource = Files.readAllBytes(Paths.get(rawFont));
                    if (StandardCharsets.UTF_8.name().equals(encoding)) {
                        font =
                            PdfFontFactory.createFont(
                                fontResource,
                                PdfEncodings.IDENTITY_H,
                                PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
                            );
                    } else {
                        font = PdfFontFactory.createFont(
                            fontResource,
                            encoding,
                            PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
                        );
                    }
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to create PdfFont.", e);
                }
            } else {
                if (StringUtils.hasText(textStyle.getFontNameResource())) {
                    try {
                        // encoding must match the font resource, otherwise IOException
                        font = PdfFontFactory.createFont(
                            textStyle.getFontNameResource(),
                            encoding,
                            PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
                        );
                    } catch (IOException e) {
                        throw new IllegalArgumentException(
                            String
                                .format(
                                    "Can't create PdfFont from TextStyle with font name \"%s\" and encoding \"%s\"",
                                    textStyle.getFontNameResource(),
                                    encoding
                                ),
                            e);
                    }
                } else {
                    font = PdfFontFactory.createFont(
                        StandardFonts.TIMES_ROMAN,
                        "Cp1251",
                        PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
                    );
                }
            }
            textStyles.put(textStyle, font);
        }
        final Boolean useTtfAttributes = textStyle.isUseTtfFontAttributes();
        if (font != null) {
            element.setFont(font);
        }
        if (element instanceof Text) {
            ((Text) element)
                .setFontSize(textStyle.getFontSize())
                .setFontColor(toPdfColor(textStyle.getColor()));
        }
        if (useTtfAttributes == null || useTtfAttributes) {
            return;
        }
        if (Boolean.TRUE.equals(textStyle.isBold())) {
            element.setBold();
        }
        if (Boolean.TRUE.equals(textStyle.isItalic())) {
            element.setItalic();
        }
        if (textStyle.getUnderline() != null && textStyle.getUnderline() != 0) {
            element.setUnderline();
        }
    }

    /**
     * Decorates the native AbstractElement with a LayoutStyle
     *
     * @param outerElement decoration element
     * @param layoutStyle  input LayoutStyle style
     */
    public void convertLayoutStyleToElement(AbstractElement<?> outerElement, LayoutStyle layoutStyle) {
        if (layoutStyle == null) {
            return;
        }
        final boolean isTableCell = outerElement instanceof com.itextpdf.layout.element.Cell;
        convertGroundColor(outerElement, layoutStyle);
        convertBorder(layoutStyle.getBorderTop(), outerElement::setBorderTop, isTableCell);
        convertBorder(layoutStyle.getBorderLeft(), outerElement::setBorderLeft, isTableCell);
        convertBorder(layoutStyle.getBorderRight(), outerElement::setBorderRight, isTableCell);
        convertBorder(layoutStyle.getBorderBottom(), outerElement::setBorderBottom, isTableCell);
        convertHorizontalAlignment(outerElement, layoutStyle);
        convertVerticalAlignment(outerElement, layoutStyle);
        convertShrinkToFit(outerElement, layoutStyle);
        convertGeometryDetails(outerElement, layoutStyle);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("encoding", encoding)
            .add("textStyles", textStyles)
            .add("fontService", fontService)
            .add("parent", super.toString())
            .toString();
    }

    public String getEncoding() {
        return encoding;
    }

    public PdfStyleService setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }
}
