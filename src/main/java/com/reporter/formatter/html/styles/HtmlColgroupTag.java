package com.reporter.formatter.html.styles;

public class HtmlColgroupTag {
    /**
     * Write style or not
     */
    protected boolean enabled;
    /**
     * In-place style or not (write here or somewhere else if enabled)
     */
    protected boolean writeInplace = true;

    public static HtmlColgroupTag create() {
        return new HtmlColgroupTag();
    }

    public static HtmlColgroupTag create(Boolean enabled) {
        return new HtmlColgroupTag().setEnabled(enabled);
    }

    public static HtmlColgroupTag create(Boolean enabled, Boolean writeInplace) {
        return new HtmlColgroupTag().setEnabled(enabled).setWriteInplace(writeInplace);
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public HtmlColgroupTag setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public Boolean getWriteInplace() {
        return writeInplace;
    }

    public HtmlColgroupTag setWriteInplace(Boolean writeInplace) {
        this.writeInplace = writeInplace;
        return this;
    }
}
