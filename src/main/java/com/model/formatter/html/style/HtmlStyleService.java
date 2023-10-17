package com.model.formatter.html.style;

import com.google.common.base.MoreObjects;
import com.model.domain.DocumentItem;
import com.model.domain.TableCell;
import com.model.domain.TableRow;
import com.model.domain.style.BorderStyle;
import com.model.domain.style.FontFamilyStyle;
import com.model.domain.style.LayoutStyle;
import com.model.domain.style.LayoutTextStyle;
import com.model.domain.style.Style;
import com.model.domain.style.StyleCondition;
import com.model.domain.style.StyleService;
import com.model.domain.style.StyleUtils;
import com.model.domain.style.TextStyle;
import com.model.domain.style.constant.BorderWeight;
import com.model.domain.style.constant.Color;
import com.model.domain.style.constant.HorAlignment;
import com.model.domain.style.constant.VertAlignment;
import com.model.domain.style.geometry.Geometry;
import com.model.domain.style.geometry.GeometryDetails;
import com.model.formatter.html.HtmlDetails;
import com.model.formatter.html.tag.Html4Font;
import com.model.formatter.html.tag.Html4StyledTag;
import com.model.formatter.html.tag.HtmlDiv;
import com.model.formatter.html.tag.HtmlTag;
import com.model.utils.ConverterUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Registers methods for writing styles
 * DocumentItem to OutputStreamWriter
 */

public class HtmlStyleService extends StyleService implements HtmlDetails {
    private static final Map<BorderWeight, String> borderWidthMap =
        new HashMap<BorderWeight, String>() {{
            put(BorderWeight.NONE, null);
            put(BorderWeight.THIN, "1px solid");
            put(BorderWeight.MEDIUM, "2px solid");
            put(BorderWeight.THICK, "3px solid");
            put(BorderWeight.DOUBLE, "double");
            put(BorderWeight.DASHED, "dashed");
            put(BorderWeight.DOTTED, "dotted");
        }};

    private static final Map<BorderWeight, String> separatorWidthMap =
        new HashMap<BorderWeight, String>() {{
            put(BorderWeight.THIN, "1px");
            put(BorderWeight.MEDIUM, "2px");
            put(BorderWeight.THICK, "3px");
        }};

    private static final Map<HorAlignment, String> horizontalAlignmentHtml4Map =
        new HashMap<HorAlignment, String>() {{
            put(HorAlignment.LEFT, "left");
            put(HorAlignment.CENTER, "center");
            put(HorAlignment.RIGHT, "right");
        }};

    private static final Map<HorAlignment, String> horizontalAlignmentMap =
        new HashMap<HorAlignment, String>() {{
            put(null, null);
            put(HorAlignment.GENERAL, "justify");
            put(HorAlignment.LEFT, "left");
            put(HorAlignment.CENTER, "center");
            put(HorAlignment.RIGHT, "right");
        }};

    private static final Map<VertAlignment, String> verticalTextAlignmentMap =
        new HashMap<VertAlignment, String>() {{
            put(null, null);
            put(VertAlignment.TOP, "top");
            put(VertAlignment.CENTER, "middle");
            put(VertAlignment.BOTTOM, "bottom");
        }};

    private static final Map<VertAlignment, String> verticalAlignmentMap =
        new HashMap<VertAlignment, String>() {{
            put(null, null);
            put(VertAlignment.TOP, "top");
            put(VertAlignment.CENTER, "center");
            put(VertAlignment.BOTTOM, "bottom");
        }};

    private static final Map<FontFamilyStyle, String> fontFamilyHtml4Map =
        new HashMap<FontFamilyStyle, String>() {{
            put(FontFamilyStyle.SERIF, "serif");
            put(FontFamilyStyle.SANS_SERIF, "sans-serif");
            put(FontFamilyStyle.MONOSPACED, "monospace");
        }};
    /**
     * If true then style will be written inside HTML4 tags
     */
    protected boolean useHtml4Tags;
    /**
     * If true then style will be written inside HTML5 tags,
     */
    protected boolean writeStyleInplace;
    protected HtmlColStyle htmlColStyle;

    public HtmlStyleService(
        boolean useHtml4Tags,
        boolean writeStyleInplace,
        HtmlColStyle htmlColStyle,
        DecimalFormat decimalFormat
    ) {
        this.useHtml4Tags = useHtml4Tags;
        this.writeStyleInplace = writeStyleInplace;
        this.htmlColStyle = htmlColStyle;
        this.decimalFormat = decimalFormat;
    }

    private static String geometryToString(String prefix, Geometry<?> geometry, String suffix) {
        final AtomicReference<String> res = new AtomicReference<>(null);
        if (geometry != null) {
            geometry
                .getValueFor(EXTENSION)
                .ifPresent(value -> res.set(prefix + ConverterUtils.convert(value) + suffix));
        }
        return res.get();
    }

    public static String toHtmlColor(Color color) {
        return color != null ? "#" + color.buildColorString() : null;
    }

    public static String toHtmlBorderWidth(BorderWeight borderWeight) {
        if (borderWidthMap.containsKey(borderWeight)) {
            return borderWidthMap.get(borderWeight);
        }
        return null;
    }

    public static String toHtmlHorAlignment(HorAlignment horAlignment) {
        if (horizontalAlignmentMap.containsKey(horAlignment)) {
            return horizontalAlignmentMap.get(horAlignment);
        }
        return null;
    }

    public static String toHtmlTextVertAlignment(VertAlignment vertAlignment) {
        if (verticalTextAlignmentMap.containsKey(vertAlignment)) {
            return verticalTextAlignmentMap.get(vertAlignment);
        }
        return null;
    }

    public static String toHtmlTransform(GeometryDetails geometryDetails) {
        return Stream
            .of(
                geometryToString("scaleX(", geometryDetails.getScaleX(), ")"),
                geometryToString("scaleY(", geometryDetails.getScaleY(), ")"),
                geometryToString("rotate(", geometryDetails.getAngle(), ")")
            )
            .filter(StringUtils::hasText)
            .collect(Collectors.joining(" "));
    }

    public static String toHtmlWidth(Geometry<Object> width) {
        return geometryToString("", width, "");
    }

    public static String toHtmlHeight(Geometry<Object> height) {
        return geometryToString("", height, "");
    }

    public static String toHtmlTransformCenter(Geometry<Map.Entry<HorAlignment, VertAlignment>> transformCenter) {
        final AtomicReference<String> res = new AtomicReference<>(null);
        if (transformCenter != null) {
            transformCenter
                .getValueFor(EXTENSION)
                .ifPresent(value -> {
                    if (!horizontalAlignmentMap.containsKey(value.getKey())) {
                        throw new IllegalArgumentException("Undefined HorAlignment type for transform-origin");
                    }
                    HorAlignment horAlignment = value.getKey();
                    if (horAlignment == HorAlignment.GENERAL) {
                        horAlignment = HorAlignment.CENTER;
                    }

                    if (!verticalAlignmentMap.containsKey(value.getValue())) {
                        throw new IllegalArgumentException("Undefined VertAlignment type for transform-origin");
                    }
                    res.set(
                        String.join(" ",
                            horizontalAlignmentMap.get(horAlignment),
                            verticalAlignmentMap.get(value.getValue())
                        )
                    );
                });
        }
        return res.get();
    }

    public static String toHtml4HorAlignment(HorAlignment horAlignment) {
        if (horizontalAlignmentHtml4Map.containsKey(horAlignment)) {
            return horizontalAlignmentHtml4Map.get(horAlignment);
        }
        return null;
    }

    public static String toHtml4FontFace(FontFamilyStyle fontFamilyStyle) {
        if (fontFamilyHtml4Map.containsKey(fontFamilyStyle)) {
            return fontFamilyHtml4Map.get(fontFamilyStyle);
        }
        return null;
    }

    public static HtmlStyleService create(
        boolean useHtml4Tags,
        boolean writeStyleInplace,
        HtmlColStyle htmlColStyle,
        DecimalFormat decimalFormat
    ) {
        return new HtmlStyleService(useHtml4Tags, writeStyleInplace, htmlColStyle, decimalFormat);
    }

    public static HtmlStyleService create(boolean useHtml4Tags, DecimalFormat decimalFormat) {
        return create(useHtml4Tags, true, HtmlColStyle.create(), decimalFormat);
    }

    public static HtmlStyleService create(boolean useHtml4Tags) {
        return create(useHtml4Tags, true, HtmlColStyle.create(), null);
    }

    public static HtmlStyleService create() {
        return create(false, true, HtmlColStyle.create(), null);
    }

    public static TextStyle extractTextStyle(Style style) {
        TextStyle textStyle = null;
        if (style instanceof TextStyle) {
            textStyle = (TextStyle) style;
        } else if (style instanceof LayoutTextStyle) {
            textStyle = ((LayoutTextStyle) style).getTextStyle();
        }
        return textStyle;
    }

    public static String createHtmlClassInHeader(Style style) {
        return
            "<style type=\"text/css\">" +
                "." +
                HtmlTag.htmlStyleId(style) +
                "{" +
                HtmlStyleService.convert(style).toCssStyleString() +
                "}" +
                "</style>";
    }

    public static Html4Font convertHtml4Font(TextStyle textStyle) {
        final Html4Font html4Font = new Html4Font();
        fillHtml4FontFromStyle(html4Font, textStyle);
        return html4Font;
    }

    public static CssStyle convert(Style style) {
        final CssStyle cssStyle = new CssStyle();
        fillCssStyleFromStyle(cssStyle, style, false, false);
        return cssStyle;
    }

    public static String escapeHtml(String s) {
        return HtmlUtils.htmlEscape(s);
    }

    public static void fillHtml4StyleTagsFromStyle(Html4StyledTag html4StyledTag, Style style, Boolean isTable) {
        LayoutStyle layoutStyle = null;
        if (style instanceof LayoutStyle) {
            layoutStyle = (LayoutStyle) style;
        } else if (style instanceof LayoutTextStyle) {
            layoutStyle = ((LayoutTextStyle) style).getLayoutStyle();
        }
        if (layoutStyle != null) {
            html4StyledTag
                .setBgColor(toHtmlColor(layoutStyle.getFillBackgroundColor()));
            if (isTable) {
                html4StyledTag
                    .setCellSpacing(0)
                    .setBorder(1);
            } else {
                html4StyledTag.setAlign(toHtml4HorAlignment(layoutStyle.getHorAlignment()));
            }
        }
    }

    /**
     * @param isTable used for Html4 style
     */
    public static void fillCssStyleFromStyle(CssStyle cssStyle, Style style, Boolean isTable, Boolean useHtml4Tags) {
        if (style instanceof TextStyle) {
            final TextStyle textStyle = (TextStyle) style;
            final Short fontSize = textStyle.getFontSize();
            if (fontSize != null) {
                cssStyle.setFontSize(fontSize);
            }
            final Boolean isBold = textStyle.isBold();
            if (isBold != null && isBold) {
                cssStyle.setFontWeight("bold");
            }
            final Boolean isItalic = textStyle.isItalic();
            if (isItalic != null && isItalic) {
                cssStyle.setFontStyle("italic");
            }
            cssStyle.setFontColor(toHtmlColor(textStyle.getColor()));
            cssStyle.setFontFamily(textStyle.getFontNameResource());
        } else if (style instanceof LayoutStyle) {
            final LayoutStyle layoutStyle = (LayoutStyle) style;
            cssStyle.setTextHorAlignment(toHtmlHorAlignment(layoutStyle.getHorAlignment()));
            cssStyle.setTextVertAlignment(toHtmlTextVertAlignment(layoutStyle.getVertAlignment()));
            final GeometryDetails geometryDetails = layoutStyle.getGeometryDetails();
            if (geometryDetails != null) {
                cssStyle.setTransform(toHtmlTransform(geometryDetails));
                cssStyle.setWidth(toHtmlWidth(geometryDetails.getWidth()));
                cssStyle.setHeight(toHtmlHeight(geometryDetails.getHeight()));
                cssStyle.setTransformCenter(toHtmlTransformCenter(geometryDetails.getTransformCenter()));
            }
            cssStyle.setBorderTop(formHtmlBorder(layoutStyle.getBorderTop()));
            cssStyle.setBorderLeft(formHtmlBorder(layoutStyle.getBorderLeft()));
            cssStyle.setBorderRight(formHtmlBorder(layoutStyle.getBorderRight()));
            cssStyle.setBorderBottom(formHtmlBorder(layoutStyle.getBorderBottom()));
            cssStyle.setBackgroundColor(toHtmlColor(layoutStyle.getFillBackgroundColor()));
            if (useHtml4Tags) {
                if (isTable) {
                    if (
                        layoutStyle.getBorderTop().getWeight() == BorderWeight.DOUBLE
                            || layoutStyle.getBorderLeft().getWeight() == BorderWeight.DOUBLE
                            || layoutStyle.getBorderRight().getWeight() == BorderWeight.DOUBLE
                            || layoutStyle.getBorderBottom().getWeight() == BorderWeight.DOUBLE
                    ) {
                        cssStyle.setBorderHtml4(1);
                    } else if (
                        layoutStyle.getBorderTop().getWeight() != BorderWeight.NONE
                            || layoutStyle.getBorderLeft().getWeight() != BorderWeight.NONE
                            || layoutStyle.getBorderRight().getWeight() != BorderWeight.NONE
                            || layoutStyle.getBorderBottom().getWeight() != BorderWeight.NONE
                    ) {
                        cssStyle.setBorderHtml4(1);
                        cssStyle.setCellspacingHtml4(0);
                    }
                } else {
                    cssStyle.setBgcolorHtml4(toHtmlColor(layoutStyle.getFillBackgroundColor()));
                }
                cssStyle.setAlignHtml4(toHtml4HorAlignment(layoutStyle.getHorAlignment()));
            }
        } else if (style instanceof LayoutTextStyle) {
            final LayoutTextStyle layoutTextStyle = (LayoutTextStyle) style;
            fillCssStyleFromStyle(cssStyle, layoutTextStyle.getTextStyle(), isTable, useHtml4Tags);
            fillCssStyleFromStyle(cssStyle, layoutTextStyle.getLayoutStyle(), isTable, useHtml4Tags);
        }
    }

    private static void fillHtml4FontFromStyle(Html4Font html4Font, TextStyle textStyle) {
        html4Font.setSize(textStyle.getFontSize());
        html4Font.setColor(toHtmlColor(textStyle.getColor()));
        html4Font.setFace(toHtml4FontFace(textStyle.getFontFamilyStyle()));
    }

    /**
     * (<a href="http://htmlbook.ru/faq/kak-s-pomoshchyu-stiley-zadat-tsvet-linii">...</a>)
     */
    protected static void fillCssStyleFromBorderStyle(CssStyle cssStyle, BorderStyle borderStyle) {
        if (separatorWidthMap.containsKey(borderStyle.getWeight())) {
            cssStyle.setHeight(separatorWidthMap.get(borderStyle.getWeight()));
        }
        cssStyle.setBorder("none");
        cssStyle.setColor(toHtmlColor(borderStyle.getColor()));
        cssStyle.setBackgroundColor(toHtmlColor(borderStyle.getColor()));
    }

    public static String formHtmlBorder(BorderStyle borderStyle) {
        if (borderStyle == null) {
            return null;
        }
        final StringJoiner stringJoiner = new StringJoiner(" ");
        stringJoiner.add(toHtmlBorderWidth(borderStyle.getWeight()));
        if (borderStyle.getColor() != null) {
            stringJoiner.add(toHtmlColor(borderStyle.getColor()));
        }
        return stringJoiner.toString().trim();
    }

    /**
     * Joins styles for table cell
     *
     * @param documentItem TableCell or TableHeaderCell
     * @return cell style
     */
    public Style getCustomTableCellStyle(DocumentItem documentItem) throws Exception {
        return prepareStyleFrom(documentItem);
    }

    public Style getCustomTableCellDivStyle(HtmlDiv htmlDiv) throws Exception {
        return prepareStyleFrom(htmlDiv);
    }

    /**
     * Writes the styles from {@link StyleService#styles} to the (head)(/head) document,
     * adding styles applied to {@link TableRow} as additional styles,
     * overlaid on {@link TableCell}
     * (the condition for such styles remains styleGlue.getCondition().getClazz() == {@link TableRow}.class)
     */
    @Override
    public void writeStyles(Object o) throws Exception {
        final OutputStreamWriter OsWriter = (OutputStreamWriter) o;
        if (!useHtml4Tags && !writeStyleInplace) {

            final BiFunction<Class<?>, Style, Boolean> checkConditionClass = (clazz, style) -> {
                if (style.getCondition() != null) {
                    return clazz.equals(style.getCondition().getClazz());
                }
                return false;
            };

            final List<Style> rowStyles =
                styles
                    .stream()
                    .filter(s -> checkConditionClass.apply(TableRow.class, s))
                    .collect(Collectors.toList());

            final List<Style> cellDivStyles = new ArrayList<>();
            final List<Style> cellStyles =
                styles
                    .stream()
                    .filter(s -> checkConditionClass.apply(TableCell.class, s))
                    .peek(s -> {
                        GeometryDetails gd = null;
                        if (s instanceof LayoutStyle) {
                            gd = ((LayoutStyle) s).getGeometryDetails();
                            ((LayoutStyle) s).setGeometryDetails(null);
                        } else if (s instanceof LayoutTextStyle && ((LayoutTextStyle) s).getLayoutStyle() != null) {
                            gd = ((LayoutTextStyle) s).getLayoutStyle().getGeometryDetails();
                            ((LayoutTextStyle) s).getLayoutStyle().setGeometryDetails(null);
                        }
                        if (gd != null) {
                            cellDivStyles.add(
                                LayoutStyle.create()
                                    .setGeometryDetails(gd)
                                    .setCondition(
                                        StyleCondition.create(HtmlDiv.class, ignored -> true)
                                    )
                            );
                        }
                    })
                    .collect(Collectors.toList());

            styles.addAll(cellDivStyles);

            final List<Style> gluedStyles = new ArrayList<>(styles);

            for (final Style c : cellStyles) {
                for (final Style r : rowStyles) {
                    final Style rs = r.clone();
                    StyleUtils.joinWith(c, rs);
                    gluedStyles.add(rs);
                }
            }

            for (final Style style : gluedStyles) {
                final String htmlClass = createHtmlClassInHeader(style);
                OsWriter.write(htmlClass);
            }
        }
    }

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("useHtml4Tags", useHtml4Tags)
                .add("parent", super.toString())
                .toString();
    }

    public boolean isUseHtml4Tags() {
        return useHtml4Tags;
    }

    public HtmlStyleService setUseHtml4Tags(boolean useHtml4Tags) {
        this.useHtml4Tags = useHtml4Tags;
        return this;
    }

    public boolean isWriteStyleInplace() {
        return writeStyleInplace;
    }

    public HtmlStyleService setWriteStyleInplace(boolean writeStyleInplace) {
        this.writeStyleInplace = writeStyleInplace;
        return this;
    }

    public HtmlColStyle getHtmlColStyle() {
        return htmlColStyle;
    }

    public HtmlStyleService setHtmlColStyle(HtmlColStyle htmlColStyle) {
        this.htmlColStyle = htmlColStyle;
        return this;
    }
}
