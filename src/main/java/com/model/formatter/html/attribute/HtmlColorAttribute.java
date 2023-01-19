package com.model.formatter.html.attribute;

public class HtmlColorAttribute extends HtmlAttribute {
    public static final String ATTR_NAME = "color";

    @Override
    public String getAttribute() {
        return ATTR_NAME;
    }

    public HtmlColorAttribute setColor(String color) {
        setAttributeValue(color);
        return this;
    }
}
