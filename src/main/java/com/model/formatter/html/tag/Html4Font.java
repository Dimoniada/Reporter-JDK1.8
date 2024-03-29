package com.model.formatter.html.tag;

import com.model.formatter.html.attribute.HtmlColorAttribute;
import com.model.formatter.html.attribute.HtmlFontFaceAttribute;
import com.model.formatter.html.attribute.HtmlFontSizeAttribute;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Html4Font extends HtmlTag {
    public static final String TAG_NAME = "font";

    final HtmlColorAttribute color = new HtmlColorAttribute();
    final HtmlFontFaceAttribute face = new HtmlFontFaceAttribute();
    final HtmlFontSizeAttribute size = new HtmlFontSizeAttribute();

    public Html4Font() {
        availableAttributes =
            Stream.of(
                    size.getAttributeMapper(),
                    face.getAttributeMapper(),
                    color.getAttributeMapper()
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    public Html4Font setSize(int size) {
        this.size.setFontSize(size);
        return this;
    }

    public Html4Font setFace(String face) {
        this.face.setFace(face);
        return this;
    }

    public Html4Font setColor(String color) {
        this.color.setColor(color);
        return this;
    }
}
