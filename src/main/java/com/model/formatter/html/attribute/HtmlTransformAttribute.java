package com.model.formatter.html.attribute;

public class HtmlTransformAttribute extends HtmlAttribute {
    public static final String ATTR_NAME = "transform";

    @Override
    public String getAttribute() {
        return ATTR_NAME;
    }

    public HtmlTransformAttribute setTransform(String transform) {
        setAttributeValue(transform);
        return this;
    }
}
