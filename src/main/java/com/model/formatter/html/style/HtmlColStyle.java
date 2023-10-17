package com.model.formatter.html.style;

import com.model.domain.style.Style;

public class HtmlColStyle {
    /**
     * ColGroup style
     */
    protected Style style;

    /**
     * Collapsing borders in table
     */
    protected boolean isBorderCollapse;

    public static HtmlColStyle create() {
        return new HtmlColStyle();
    }

    public static HtmlColStyle create(Style style) {
        return new HtmlColStyle().setStyle(style);
    }

    public static HtmlColStyle create(Style style, Boolean isBorderCollapse) {
        return new HtmlColStyle().setStyle(style).setBorderCollapse(isBorderCollapse);
    }

    public Style getStyle() {
        return style;
    }

    public HtmlColStyle setStyle(Style style) {
        this.style = style;
        return this;
    }

    public boolean isBorderCollapse() {
        return isBorderCollapse;
    }

    public HtmlColStyle setBorderCollapse(boolean borderCollapse) {
        this.isBorderCollapse = borderCollapse;
        return this;
    }
}
