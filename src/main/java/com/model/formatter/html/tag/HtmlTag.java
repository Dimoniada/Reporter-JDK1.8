package com.model.formatter.html.tag;

import com.google.common.base.Objects;
import com.model.domain.DocumentItem;
import com.model.domain.style.Style;

import java.util.Locale;

public abstract class HtmlTag extends Html5StyledTag {
    /**
     * Gets the unique style name for the entry
     *
     * @param style Style class of {@link DocumentItem}
     * @return unique name
     */
    public static String htmlStyleId(Style style) {
        return "_" + Integer.toHexString(Objects.hashCode(style));
    }

    /**
     * @return tag name
     */
    public abstract String getTagName();

    public String toLower(Locale locale) {
        return getTagName().toLowerCase(locale);
    }

    public String toLower() {
        return getTagName().toLowerCase(Locale.ENGLISH);
    }

    public String toUpper(Locale locale) {
        return getTagName().toUpperCase(locale);
    }

    public String toUpper() {
        return getTagName().toUpperCase(Locale.ENGLISH);
    }

    /**
     * Writes an opening tag
     *
     * @return this
     */
    public String open() {
        return String.format("<%s>", getTagName());
    }

    /**
     * Writes a closing tag
     *
     * @return this
     */
    public String close() {
        return String.format("</%s>", getTagName());
    }

    @Override
    public String toString() {
        return getTagName();
    }
}
