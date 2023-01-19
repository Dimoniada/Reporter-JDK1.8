package com.model.formatter.html.tag;

import com.model.formatter.html.attribute.HtmlClassAttribute;
import com.model.formatter.html.attribute.HtmlStyleAttribute;
import com.model.formatter.html.styles.CssStyle;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The class stores attributes that describe the style of the element for HTML.
 */
public abstract class HtmlStyledTag extends Html4StyledTag {

    final HtmlStyleAttribute style = new HtmlStyleAttribute();
    final HtmlClassAttribute clazz = new HtmlClassAttribute();

    public HtmlStyledTag() {
        availableAttributes.putAll(
            Stream.of(
                    style.getAttributeMapper(),
                    clazz.getAttributeMapper()
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }

    public HtmlStyledTag setStyle(CssStyle cssStyle) {
        this.style.setStyle(cssStyle);
        return this;
    }

    public HtmlStyledTag setClass(String clazz) {
        this.clazz.setClass(clazz);
        return this;
    }
}
