package com.model.formatter.html.style;

public class HtmlColGroupStyle {
    /**
     * Write style for <col> or not
     */
    protected boolean isColGroup;
    /**
     * In-place style in <col> tag or move it to header section and provide only class reference
     */
    protected boolean isStyleInplace = true;
    /**
     * Collapsing borders in table
     */
    protected boolean isBorderCollapse;

    public static HtmlColGroupStyle create() {
        return new HtmlColGroupStyle();
    }

    public static HtmlColGroupStyle create(Boolean enabled) {
        return new HtmlColGroupStyle().setColGroup(enabled);
    }

    public static HtmlColGroupStyle create(Boolean enabled, Boolean writeInplace) {
        return new HtmlColGroupStyle().setColGroup(enabled).setStyleInplace(writeInplace);
    }

    public boolean isColGroup() {
        return isColGroup;
    }

    public HtmlColGroupStyle setColGroup(Boolean colGroup) {
        this.isColGroup = colGroup;
        return this;
    }

    public boolean isStyleInplace() {
        return isStyleInplace;
    }

    public HtmlColGroupStyle setStyleInplace(Boolean writeInplace) {
        this.isStyleInplace = writeInplace;
        return this;
    }

    public boolean isBorderCollapse() {
        return isBorderCollapse;
    }

    public HtmlColGroupStyle setBorderCollapse(boolean borderCollapse) {
        this.isBorderCollapse = borderCollapse;
        return this;
    }
}
