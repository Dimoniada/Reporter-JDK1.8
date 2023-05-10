package com.model.domain.styles.geometry;

import java.util.HashMap;
import java.util.Optional;

/**
 * Represents measurable types (based on document type) and corresponded values.
 * Key is document extension, value is length.
 * For width Geometry the length is:
 *    for .xlsx(int) it's units of 1/256th of a character width, see setColumnWidth() in {@link org.apache.poi.ss.usermodel.Sheet},
 *    for .docx(int) it's 20ths of a point, twips, see setWidth() in {@link org.apache.poi.xwpf.usermodel.XWPFTable},
 *    for .pdf(float) by default it's in points of (1/72)", see setWidth() in {@link com.itextpdf.layout.element.BlockElement}
 *    for .html(String) it's a width with dimension, see attribute width
 */
public class Geometry<T> extends HashMap<String, T> {
    /**
     * Returns the optional value for which key.contains(format) == true is performed
     *
     * @param format is a string for search among keys in map
     * @return is an Optional value linked to a partially matching by-word key
     */
    public Optional<T> getValueFor(String format) {
        return this
            .entrySet()
            .stream()
            .filter(es -> es.getKey().contains(format))
            .map(HashMap.Entry::getValue)
            .findFirst();
    }

    public static <T> Geometry<T> create() {
        return new Geometry<T>();
    }

    public Geometry<T> add(String key, T value) {
        this.put(key, value);
        return this;
    }

    public Geometry<T> delete(String key) {
        this.remove(key);
        return this;
    }
}
