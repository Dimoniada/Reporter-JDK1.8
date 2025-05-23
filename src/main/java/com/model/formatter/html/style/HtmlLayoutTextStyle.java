package com.model.formatter.html.style;

import com.google.common.base.MoreObjects;
import com.model.domain.style.LayoutStyle;
import com.model.domain.style.LayoutTextStyle;
import com.model.domain.style.TextStyle;

public class HtmlLayoutTextStyle extends LayoutTextStyle {

    /**
     * Collapsing borders
     */
    protected boolean isBordersCollapse;
    /**
     * Colgroup style or not (table style) flag
     */
    protected boolean isColGroupStyle;

    /**
     * Type of page-break
     */
    protected String typePageBreakAfter;

    public static HtmlLayoutTextStyle create() {
        return new HtmlLayoutTextStyle();
    }

    public static HtmlLayoutTextStyle create(TextStyle textStyle, LayoutStyle layoutStyle) {
        final HtmlLayoutTextStyle htmlStyle = new HtmlLayoutTextStyle();
        htmlStyle.setTextStyle(textStyle);
        htmlStyle.setLayoutStyle(layoutStyle);
        return htmlStyle;
    }

    public static HtmlLayoutTextStyle create(LayoutTextStyle layoutTextStyle) {
        final HtmlLayoutTextStyle htmlStyle = new HtmlLayoutTextStyle();
        htmlStyle.setLayoutStyle(layoutTextStyle.getLayoutStyle());
        htmlStyle.setTextStyle(layoutTextStyle.getTextStyle());
        return htmlStyle;
    }

    public static HtmlLayoutTextStyle create(Boolean isBorderCollapse) {
        return new HtmlLayoutTextStyle().setBordersCollapse(isBorderCollapse);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("isBordersCollapse", isBordersCollapse)
            .add("isColGroupStyle", isColGroupStyle)
            .add("typePageBreakAfter", typePageBreakAfter)
            .toString();
    }

    public boolean isBordersCollapse() {
        return isBordersCollapse;
    }

    public HtmlLayoutTextStyle setBordersCollapse(boolean bordersCollapse) {
        this.isBordersCollapse = bordersCollapse;
        return this;
    }

    public boolean isColGroupStyle() {
        return isColGroupStyle;
    }

    public HtmlLayoutTextStyle setColGroupStyle(boolean colGroupStyle) {
        isColGroupStyle = colGroupStyle;
        return this;
    }

    public String getTypePageBreakAfter() {
        return typePageBreakAfter;
    }

    public HtmlLayoutTextStyle setTypePageBreakAfter(String typePageBreakAfter) {
        this.typePageBreakAfter = typePageBreakAfter;
        return this;
    }
}
