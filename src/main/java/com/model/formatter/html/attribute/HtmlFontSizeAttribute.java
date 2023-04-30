package com.model.formatter.html.attribute;

public class HtmlFontSizeAttribute extends HtmlAttribute {
    public static final String ATTR_NAME = "size";

    @Override
    public String getAttribute() {
        return ATTR_NAME;
    }

    public HtmlFontSizeAttribute setFontSize(int fontSize) {
        setAttributeValue(fontSize);
        return this;
    }
}
