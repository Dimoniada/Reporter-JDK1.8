package com.model.domain;

import com.model.domain.core.DataItem;
import com.model.domain.core.PictureItem;
import com.model.domain.core.TextItem;
import com.model.formatter.FormatterVisitor;

/**
 * Text footer
 */
public class Footer extends DataItem {

    protected String text;

    protected byte[] data;

    private Class<?> clazz;

    public Footer() {
        /**/
    }

    public Footer(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static Footer create() {
        return new Footer();
    }

    public static Footer create(String text) {
        return new Footer(TextItem.class).setText(text);
    }

    public static Footer create(byte[] data) {
        return new Footer(PictureItem.class).setData(data);
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
}
