package com.model.formatter.html.style;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CssStyle {
    public static final String FONT_SIZE = "font-size";
    public static final String FONT_WEIGHT = "font-weight";
    public static final String FONT_STYLE = "font-style";
    public static final String FONT_COLOR = "color";
    public static final String FONT_FAMILY = "font-family";

    public static final String TEXT_HOR_ALIGN = "text-align";
    public static final String TEXT_VERT_ALIGN = "vertical-align";
    public static final String DISPLAY = "display";
    public static final String TRANSFORM = "transform";
    public static final String TRANSFORM_ORIGIN = "transform-origin";

    public static final String BORDER_COLLAPSE = "border-collapse";
    public static final String BORDER_TOP = "border-top";
    public static final String BORDER_LEFT = "border-left";
    public static final String BORDER_RIGHT = "border-right";
    public static final String BORDER_BOTTOM = "border-bottom";

    public static final String BACKGROUND_COLOR = "background-color";

    public static final String BORDER = "border";
    public static final String COLOR = "color";
    public static final String HEIGHT = "height";
    public static final String WIDTH = "width";
    public static final String TRANSFORM_CENTER = "transform-origin";

    public static final String BORDER_HTML4 = "border=";
    public static final String CELLSPACING_HTML4 = "cellspacing=";
    public static final String BGCOLOR_HTML4 = "bgcolor=";
    public static final String ALIGN_HTML4 = "align=";

    protected final Map<String, Object> attributes = new HashMap<>();

    protected final Map<String, Function<Object, String>> attributeMapper;
    protected final Map<String, Function<Object, String>> attributeHtml4Mapper;

    public CssStyle() {
        attributeMapper = new HashMap<String, Function<Object, String>>() {{
            put(FONT_SIZE, CssStyle::produceFontSizeAttribute);
            put(FONT_WEIGHT, CssStyle::produceDefaultStringAttribute);
            put(FONT_STYLE, CssStyle::produceDefaultStringAttribute);
            put(FONT_COLOR, CssStyle::produceDefaultStringAttribute);
            put(FONT_FAMILY, CssStyle::produceFontFamilyAttribute);
            put(TEXT_HOR_ALIGN, CssStyle::produceDefaultStringAttribute);
            put(TEXT_VERT_ALIGN, CssStyle::produceDefaultStringAttribute);
            put(DISPLAY, CssStyle::produceDefaultStringAttribute);
            put(TRANSFORM, CssStyle::produceDefaultStringAttribute);
            put(WIDTH, CssStyle::produceDefaultStringAttribute);
            put(HEIGHT, CssStyle::produceDefaultStringAttribute);
            put(TRANSFORM_ORIGIN, CssStyle::produceDefaultStringAttribute);
            put(BORDER_TOP, CssStyle::produceDefaultStringAttribute);
            put(BORDER_LEFT, CssStyle::produceDefaultStringAttribute);
            put(BORDER_RIGHT, CssStyle::produceDefaultStringAttribute);
            put(BORDER_BOTTOM, CssStyle::produceDefaultStringAttribute);
            put(BACKGROUND_COLOR, CssStyle::produceDefaultStringAttribute);
            put(BORDER_COLLAPSE, CssStyle::produceDefaultStringAttribute);
            put(BORDER, CssStyle::produceDefaultStringAttribute);
        }};

        attributeHtml4Mapper = new HashMap<String, Function<Object, String>>() {{
            put(BORDER_HTML4, CssStyle::produceBorderHtml4Attribute);
            put(CELLSPACING_HTML4, CssStyle::produceCellspacingHtml4Attribute);
            put(BGCOLOR_HTML4, CssStyle::produceDefaultStringAttribute);
            put(ALIGN_HTML4, CssStyle::produceDefaultStringAttribute);
        }};
    }

    public int getFontSize() {
        return (Integer) attributes.getOrDefault(FONT_SIZE, 0);
    }

    public String getFontWeight() {
        return (String) attributes.getOrDefault(FONT_WEIGHT, "normal");
    }

    public String getFontStyle() {
        return (String) attributes.getOrDefault(FONT_STYLE, "normal");
    }

    public String getFontColor() {
        return (String) attributes.getOrDefault(FONT_COLOR, "#FFFFFF");
    }

    public String getFontFamily() {
        return (String) attributes.getOrDefault(FONT_FAMILY, "inherit");
    }

    public String getTextHorAlign() {
        return (String) attributes.getOrDefault(TEXT_HOR_ALIGN, "justify");
    }

    public String getTextVertAlign() {
        return (String) attributes.getOrDefault(TEXT_VERT_ALIGN, "middle");
    }

    public String getDisplay() {
        return (String) attributes.getOrDefault(DISPLAY, "table-cell");
    }

    public String getBorderCollapse() {
        return (String) attributes.getOrDefault(BORDER_COLLAPSE, "collapse");
    }

    public String getBorderTop() {
        return (String) attributes.getOrDefault(BORDER_TOP, "inherit");
    }

    public String getBorderLeft() {
        return (String) attributes.getOrDefault(BORDER_LEFT, "inherit");
    }

    public String getBorderRight() {
        return (String) attributes.getOrDefault(BORDER_RIGHT, "inherit");
    }

    public String getBorderBottom() {
        return (String) attributes.getOrDefault(BORDER_BOTTOM, "inherit");
    }

    public String getBorder() {
        return (String) attributes.getOrDefault(BORDER, "none");
    }

    public String getColor() {
        return (String) attributes.getOrDefault(COLOR, "inherit");
    }

    public String getHeight() {
        return (String) attributes.getOrDefault(HEIGHT, "inherit");
    }

    public Integer getBorderHtml4() {
        return (Integer) attributes.getOrDefault(BORDER_HTML4, 0);
    }

    public Integer getCellspacingHtml4() {
        return (Integer) attributes.getOrDefault(CELLSPACING_HTML4, 0);
    }

    public String getBgcolorHtml4() {
        return (String) attributes.getOrDefault(BGCOLOR_HTML4, "#FFFFFF");
    }

    public String getAlignHtml4() {
        return (String) attributes.getOrDefault(ALIGN_HTML4, "center");
    }

    public String getBackgroundColor() {
        return (String) attributes.getOrDefault(BACKGROUND_COLOR, "#FFFFFF");
    }

    public CssStyle setFontSize(int size) {
        attributes.computeIfAbsent(FONT_SIZE, v -> size);
        return this;
    }

    public CssStyle setFontWeight(String fontWeight) {
        attributes.computeIfAbsent(FONT_WEIGHT, v -> fontWeight);
        return this;
    }

    public CssStyle setFontStyle(String fontStyle) {
        attributes.computeIfAbsent(FONT_STYLE, v -> fontStyle);
        return this;
    }

    public CssStyle setFontColor(String fontColor) {
        attributes.computeIfAbsent(FONT_COLOR, v -> fontColor);
        return this;
    }

    public CssStyle setFontFamily(String fontFamily) {
        attributes.computeIfAbsent(FONT_FAMILY, v -> fontFamily);
        return this;
    }

    public CssStyle setTextHorAlignment(String textHorAlignment) {
        attributes.computeIfAbsent(TEXT_HOR_ALIGN, v -> textHorAlignment);
        return this;
    }

    public CssStyle setTextVertAlignment(String textVertAlignment) {
        if (StringUtils.hasText(textVertAlignment)) {
            attributes.putIfAbsent(DISPLAY, "table-cell");
        }
        attributes.computeIfAbsent(TEXT_VERT_ALIGN, v -> textVertAlignment);
        return this;
    }

    public CssStyle setDisplay(String display) {
        attributes.computeIfAbsent(DISPLAY, v -> display);
        return this;
    }

    public CssStyle setTransform(String transform) {
        attributes.computeIfAbsent(TRANSFORM, v -> transform);
        return this;
    }

    public CssStyle setWidth(String width) {
        attributes.computeIfAbsent(WIDTH, v -> width);
        return this;
    }

    public CssStyle setHeight(String height) {
        attributes.computeIfAbsent(HEIGHT, v -> height);
        return this;
    }

    public CssStyle setTransformCenter(String transformCenter) {
        attributes.computeIfAbsent(TRANSFORM_CENTER, v -> transformCenter);
        return this;
    }

    public CssStyle setBorderCollapse(String borderCollapse) {
        attributes.computeIfAbsent(BORDER_COLLAPSE, v -> borderCollapse);
        return this;
    }

    public CssStyle setBorderTop(String borderTop) {
        if (StringUtils.hasText(borderTop)) {
            attributes.putIfAbsent(BORDER_COLLAPSE, "collapse");
        }
        attributes.computeIfAbsent(BORDER_TOP, v -> borderTop);
        return this;
    }

    public CssStyle setBorderLeft(String borderLeft) {
        if (StringUtils.hasText(borderLeft)) {
            attributes.putIfAbsent(BORDER_COLLAPSE, "collapse");
        }
        attributes.computeIfAbsent(BORDER_LEFT, v -> borderLeft);
        return this;
    }

    public CssStyle setBorderRight(String borderRight) {
        if (StringUtils.hasText(borderRight)) {
            attributes.putIfAbsent(BORDER_COLLAPSE, "collapse");
        }
        attributes.computeIfAbsent(BORDER_RIGHT, v -> borderRight);
        return this;
    }

    public CssStyle setBorderBottom(String borderBottom) {
        if (StringUtils.hasText(borderBottom)) {
            attributes.putIfAbsent(BORDER_COLLAPSE, "collapse");
        }
        attributes.computeIfAbsent(BORDER_BOTTOM, v -> borderBottom);
        return this;
    }

    public CssStyle setBackgroundColor(String backgroundColor) {
        attributes.computeIfAbsent(BACKGROUND_COLOR, v -> backgroundColor);
        return this;
    }

    public CssStyle setBorder(String border) {
        attributes.computeIfAbsent(BORDER, v -> border);
        return this;
    }

    public CssStyle setColor(String color) {
        attributes.computeIfAbsent(COLOR, v -> color);
        return this;
    }

    public CssStyle setBorderHtml4(Integer borderHtml4) {
        attributes.put(BORDER_HTML4, borderHtml4);
        return this;
    }

    public CssStyle setCellspacingHtml4(Integer cellspacingHtml4) {
        attributes.put(CELLSPACING_HTML4, cellspacingHtml4);
        return this;
    }

    public CssStyle setBgcolorHtml4(String bgcolorHtml4) {
        attributes.put(BGCOLOR_HTML4, bgcolorHtml4);
        return this;
    }

    public CssStyle setAlignHtml4(String alignHtml4) {
        if (alignHtml4 != null) {
            attributes.put(ALIGN_HTML4, alignHtml4);
        }
        return this;
    }

    public static String produceFontSizeAttribute(Object o) {

        return String.format("%dpt", (Integer) o);
    }

    public static String produceFontFamilyAttribute(Object o) {
        if (!StringUtils.hasText((String) o)) {
            return "monospace";
        } else {
            return String.format("%s,monospace", o);
        }
    }

    public static String produceBorderHtml4Attribute(Object o) {
        return String.format("%d", (Integer) o);
    }

    public static String produceCellspacingHtml4Attribute(Object o) {
        return String.format("%d", (Integer) o);
    }

    public static String produceDefaultStringAttribute(Object o) {
        return String.format("%s", o);
    }

    public String toCssStyleString() {
        return
            attributes
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .filter(s -> attributeMapper.containsKey(s.getKey()))
                .map(this::styleItemMapping)
                .collect(Collectors.joining(";"));
    }

    private String styleItemMapping(Map.Entry<String, Object> e) {
        final String key = e.getKey();

        final Object attributeValue = e.getValue();

        final String processedValue =
            attributeMapper
                .getOrDefault(e.getKey(), CssStyle::defaultAttributeMapping)
                .apply(attributeValue);

        return String.format("%s:%s", key, processedValue);
    }

    public String toHtml4StyleString() {
        return
            attributes
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .filter(s -> attributeHtml4Mapper.containsKey(s.getKey()))
                .map(e -> styleHtml4ItemMapping(e, attributeHtml4Mapper))
                .collect(Collectors.joining(" "));
    }

    public static String styleHtml4ItemMapping(
        Map.Entry<String, Object> e,
        Map<String, Function<Object, String>> map
    ) {
        final String key = e.getKey();

        final Object attributeValue = e.getValue();

        final String processedValue =
            map
                .getOrDefault(e.getKey(), CssStyle::defaultAttributeMapping)
                .apply(attributeValue);

        return String.format("%s\"%s\"", key, processedValue);
    }

    protected static String defaultAttributeMapping(Object o) {
        return String.valueOf(o);
    }
}
