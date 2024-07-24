package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.domain.core.DataItem;
import com.model.domain.core.PictureItem;
import com.model.domain.core.TextItem;
import com.model.formatter.FormatterVisitor;

/**
 * Text footer
 */
public class Footer extends DataItem {
    /**
     * Inner data type
     */
    protected Class<?> clazz;
    /**
     * Text data
     */
    protected String text;
    /**
     * Picture raw data
     */
    protected byte[] data;

    public static Footer create() {
        return new Footer();
    }

    public static Footer create(String text) {
        return new Footer()
            .setClazz(TextItem.class)
            .setText(text);
    }

    public static Footer create(byte[] data) {
        return new Footer()
            .setClazz(PictureItem.class)
            .setData(data);
    }

    @Override
    public Footer accept(FormatterVisitor visitor) throws Throwable {
        visitor.visitFooter(this);
        return this;
    }

    @Override
    public boolean isDataInheritedFrom(Class<?> type) {
        return type.isAssignableFrom(clazz);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Footer setText(String text) {
        this.text = text;
        this.clazz = TextItem.class;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Footer setData(byte[] data) {
        this.data = data;
        this.clazz = PictureItem.class;
        return this;
    }

    public Footer setClazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("text", text)
            .add("data", data)
            .add("clazz", clazz)
            .add("super", super.toString())
            .toString();
    }
}
