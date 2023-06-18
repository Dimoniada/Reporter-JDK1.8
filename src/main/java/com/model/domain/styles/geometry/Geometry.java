package com.model.domain.styles.geometry;

import java.util.HashMap;
import java.util.Optional;

/**
 * Represents measurable types (based on document type) and corresponded values.
 * Key is document extension, value is length/angle (measurable entity).
 * For width Geometry the length is:
 *    for .xlsx(int) it's units of 1/256th of a character width, see setColumnWidth() in {@link org.apache.poi.ss.usermodel.Sheet};
 *    for .docx(int) it's 20ths of a point, twips, see setWidth() in {@link org.apache.poi.xwpf.usermodel.XWPFTable};
 *    for .pdf(float) by default it's in points of (1/72)", see setWidth() in {@link com.itextpdf.layout.element.BlockElement};
 *    for .html(String) it's a width with dimension, see attribute width;
 * For height Geometry the length is:
 *    for .xlsx(short or float) see setHeight() or setHeightInPoints() in {@link org.apache.poi.ss.usermodel.Row};
 * For scale Geometry the length is:
 *    for .xlsx(double, double) use Double.MAX_VALUE for embedding image into the cell, see resize() in
 *      {@link org.apache.poi.xssf.usermodel.XSSFPicture};
 * For rotation Geometry the length is:
 *    for text .xlsx(short) see setRotation() in {@link org.apache.poi.ss.usermodel.CellStyle};
 *    for Picture .xlsx(int) it's units of 1/60000th of a degree, see setRot() in
 *      {@link org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D}
 *
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
        return new Geometry<>();
    }

    /**
     * Adds a geometry value for a characteristic for a specific document format
     *
     * @param key formal name: html/xlsx/pdf/...
     * @param value the value of variable in appropriate units
     * @return this
     */
    public Geometry<T> add(String key, T value) {
        this.put(key, value);
        return this;
    }

    public Geometry<T> delete(String key) {
        this.remove(key);
        return this;
    }
}
