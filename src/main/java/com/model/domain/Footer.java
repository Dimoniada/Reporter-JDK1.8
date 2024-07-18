package com.model.domain;

import com.model.domain.core.DataItem;
import com.model.domain.core.PictureItem;
import com.model.domain.core.TextItem;
import com.model.formatter.FormatterVisitor;

/**
 * Text footer
 */
public class Footer extends DataItem<Footer> {

    /**
     * Delegated item for storing either text or picture
     */
    protected DataItem<Footer> dataItem;

    public static Footer create() {
        return new Footer();
    }

    public static Footer create(String text) {
        final Footer footer = new Footer();
        footer.dataItem = new TextItem<DataItem<Footer>>().setText(text);
        return footer;
    }

    public static Footer create(byte[] data) {
        final Footer footer = new Footer();
        footer.dataItem = new PictureItem<DataItem<Footer>>().setData(data);
        return footer;
    }

    @Override
    public Footer accept(FormatterVisitor visitor) throws Throwable {
        visitor.visitFooter(this);
        return this;
    }

    @Override
    public boolean isDataInheritedFrom(Class<?> type) {
        return type.isAssignableFrom(dataItem.getClass());
    }

    @Override
    public String getText() {
        return dataItem.getText();
    }

    @Override
    public byte[] getData() {
        return dataItem.getData();
    }
}
