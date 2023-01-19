package com.model.formatter.html.tag;

import com.model.formatter.html.attribute.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The class stores attributes that describe the style of the element for HTML4.
 */
public abstract class Html4StyledTag {
    protected Map<String, HtmlAttribute> availableAttributes = new HashMap<>();

    final HtmlBorderAttribute border = new HtmlBorderAttribute();
    final HtmlCellSpacingAttribute cellSpacing = new HtmlCellSpacingAttribute();
    final HtmlBgColorAttribute bgColor = new HtmlBgColorAttribute();
    final HtmlAlignAttribute align = new HtmlAlignAttribute();

    public Html4StyledTag() {
        availableAttributes.putAll(
            Stream.of(
                    bgColor.getAttributeMapper(),
                    cellSpacing.getAttributeMapper(),
                    border.getAttributeMapper(),
                    align.getAttributeMapper()
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }

    public Html4StyledTag setBgColor(String bgColor) {
        this.bgColor.setBgColor(bgColor);
        return this;
    }

    public Html4StyledTag setCellSpacing(int cellSpacing) {
        this.cellSpacing.setCellSpacing(cellSpacing);
        return this;
    }

    public Html4StyledTag setBorder(int border) {
        this.border.setBorder(border);
        return this;
    }

    public Html4StyledTag setAlign(String align) {
        this.align.setAlign(align);
        return this;
    }

    public Map<String, HtmlAttribute> getAvailableAttributes() {
        return availableAttributes;
    }
}
