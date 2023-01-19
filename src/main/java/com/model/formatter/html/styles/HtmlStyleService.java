package com.model.formatter.html.styles;

import com.google.common.base.MoreObjects;
import com.model.domain.DocumentItem;
import com.model.domain.TableCell;
import com.model.domain.TableRow;
import com.model.domain.styles.*;
import com.model.domain.styles.constants.BorderWeight;
import com.model.domain.styles.constants.Color;
import com.model.domain.styles.constants.HorAlignment;
import com.model.formatter.html.tag.Html4Font;
import com.model.formatter.html.tag.Html4StyledTag;
import com.model.formatter.html.tag.HtmlTag;
import org.springframework.web.util.HtmlUtils;

import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.StringJoiner;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Registers methods for writing styles
 * DocumentItem to OutputStreamWriter
 */

public class HtmlStyleService extends StyleService {
    private static final Map<BorderWeight, String> borderWidthMap =
        new HashMap<BorderWeight, String>() {{
            put(BorderWeight.NONE, "");
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
            put(HorAlignment.GENERAL, "justify");
            put(HorAlignment.LEFT, "left");
            put(HorAlignment.CENTER, "center");
            put(HorAlignment.RIGHT, "right");
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
    protected HtmlColgroupTag htmlColgroupTag;

    public HtmlStyleService(
        boolean useHtml4Tags,
        boolean writeStyleInplace,
        HtmlColgroupTag htmlColgroupTag,
        DecimalFormat decimalFormat
    ) {
        this.useHtml4Tags = useHtml4Tags;
        this.writeStyleInplace = writeStyleInplace;
        this.htmlColgroupTag = htmlColgroupTag;
        this.decimalFormat = decimalFormat;
    }

    public static String toHtmlColor(Color color) {
        return color != null ? "#" + color.buildColorString() : null;
    }

    public static String toHtmlBorderWidth(BorderWeight borderWeight) {
        if (borderWidthMap.containsKey(borderWeight)) {
            return borderWidthMap.get(borderWeight);
        } else {
            throw new IllegalArgumentException("Undefined BorderWeight type");
        }
    }

    public static String toHtmlHorAlignment(HorAlignment horAlignment) {
        if (horizontalAlignmentMap.containsKey(horAlignment)) {
            return horizontalAlignmentMap.get(horAlignment);
        } else {
            throw new IllegalArgumentException("Undefined HorizontalAlignment type");
        }
    }

    public static String toHtml4HorAlignment(HorAlignment horAlignment) {
        return horizontalAlignmentHtml4Map.getOrDefault(horAlignment, "left");
    }

    public static String toHtml4FontFace(FontFamilyStyle fontFamilyStyle) {
        return fontFamilyHtml4Map.getOrDefault(fontFamilyStyle, "serif");
    }

    public static HtmlStyleService create(
        boolean useHtml4Tags,
        boolean writeTagsInplace,
        HtmlColgroupTag useHtml4ColgroupTag,
        DecimalFormat decimalFormat
    ) {
        return new HtmlStyleService(useHtml4Tags, writeTagsInplace, useHtml4ColgroupTag, decimalFormat);
    }

    public static HtmlStyleService create(boolean useHtml4Tags, DecimalFormat decimalFormat) {
        return create(useHtml4Tags, true, HtmlColgroupTag.create(), decimalFormat);
    }

    public static HtmlStyleService create(boolean useHtml4Tags) {
        return create(useHtml4Tags, true, HtmlColgroupTag.create(), null);
    }

    public static HtmlStyleService create() {
        return create(false, true, HtmlColgroupTag.create(), null);
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

    public static LayoutStyle extractLayoutStyle(Style style) {
        LayoutStyle layoutStyle = null;
        if (style instanceof LayoutStyle) {
            layoutStyle = (LayoutStyle) style;
        } else if (style instanceof LayoutTextStyle) {
            layoutStyle = ((LayoutTextStyle) style).getLayoutStyle();
        }
        return layoutStyle;
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
            cssStyle.setFontSize(textStyle.getFontSize());
            if (textStyle.isBold()) {
                cssStyle.setFontWeight("bold");
            }
            if (textStyle.isItalic()) {
                cssStyle.setFontStyle("italic");
            }
            cssStyle.setFontColor(toHtmlColor(textStyle.getColor()));
            cssStyle.setFontFamily(textStyle.getFontNameResource());
        } else if (style instanceof LayoutStyle) {
            final LayoutStyle layoutStyle = (LayoutStyle) style;
            cssStyle.setTextAlign(toHtmlHorAlignment(layoutStyle.getHorAlignment()));
            cssStyle.setBorderTop(formHtmlBorder(layoutStyle.getBorderTop(), layoutStyle.getBorderTop().getColor()));
            cssStyle.setBorderLeft(formHtmlBorder(layoutStyle.getBorderLeft(),
                layoutStyle.getBorderLeft().getColor()));
            cssStyle.setBorderRight(formHtmlBorder(layoutStyle.getBorderRight(),
                layoutStyle.getBorderRight().getColor()));
            cssStyle.setBorderBottom(formHtmlBorder(layoutStyle.getBorderBottom(),
                layoutStyle.getBorderBottom().getColor()));
            cssStyle.setBackgroundColor(toHtmlColor(layoutStyle.getFillBackgroundColor()));
            cssStyle.setBorderCollapse("collapse");
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
    private static void fillCssStyleFromBorderStyle(CssStyle cssStyle, BorderStyle borderStyle) {
        if (separatorWidthMap.containsKey(borderStyle.getWeight())) {
            cssStyle.setHeight(separatorWidthMap.get(borderStyle.getWeight()));
        }
        cssStyle.setBorder("none");
        cssStyle.setColor(toHtmlColor(borderStyle.getColor()));
        cssStyle.setBackgroundColor(toHtmlColor(borderStyle.getColor()));
    }

    public static String formHtmlBorder(BorderStyle borderStyle, Color color) {
        final StringJoiner stringJoiner = new StringJoiner(" ");
        stringJoiner.add(toHtmlBorderWidth(borderStyle.getWeight()));
        stringJoiner.add(toHtmlColor(color));
        return stringJoiner.toString().trim();
    }

    /**
     * Joins styles for table cell
     *
     * @param tableCustomCell TableCell or TableHeaderCell
     * @return cell style
     */
    public Style handleTableCustomCell(DocumentItem tableCustomCell) throws Exception {
        final boolean skipStyleInplace = htmlColgroupTag.getEnabled()
            && !htmlColgroupTag.getWriteInplace()
            && tableCustomCell instanceof TableCell;
        if (skipStyleInplace) {
            return null;
        }
        return prepareStyleFrom(tableCustomCell);
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

            BiFunction<Class<?>, Style, Boolean> checkConditionClass = (clazz, style) -> {
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

            final List<Style> cellStyles =
                styles
                    .stream()
                    .filter(s -> checkConditionClass.apply(TableCell.class, s))
                    .collect(Collectors.toList());

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

    public HtmlColgroupTag getHtmlColgroupTag() {
        return htmlColgroupTag;
    }

    public HtmlStyleService setHtmlColgroupTag(HtmlColgroupTag htmlColgroupTag) {
        this.htmlColgroupTag = htmlColgroupTag;
        return this;
    }
}
