package com.model.formatter.html.style;

public class HtmlColgroupTag {
    /**
     * Write style or not
     */
    protected boolean enabled;
    /**
     * In-place style in <col> tag or move it to header section and provide only class reference
     */
    protected boolean writeStyleInplace = true;
    /**
     * Collapsing borders in table
     */
    protected boolean borderCollapse;

    public static HtmlColgroupTag create() {
        return new HtmlColgroupTag();
    }

    public static HtmlColgroupTag create(Boolean enabled) {
        return new HtmlColgroupTag().setEnabled(enabled);
    }

    public static HtmlColgroupTag create(Boolean enabled, Boolean writeInplace) {
        return new HtmlColgroupTag().setEnabled(enabled).setWriteStyleInplace(writeInplace);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public HtmlColgroupTag setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isWriteStyleInplace() {
        return writeStyleInplace;
    }

    public HtmlColgroupTag setWriteStyleInplace(Boolean writeInplace) {
        this.writeStyleInplace = writeInplace;
        return this;
    }

    public boolean isBorderCollapse() {
        return borderCollapse;
    }

    public HtmlColgroupTag setBorderCollapse(boolean borderCollapse) {
        this.borderCollapse = borderCollapse;
        return this;
    }
}
