package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.domain.styles.constants.PictureFormat;
import com.model.formatter.FormatterVisitor;
import org.springframework.core.io.WritableResource;

public class Picture extends DocumentItem {

    protected PictureFormat pictureFormat;

    protected WritableResource data;

    public static Picture create(PictureFormat pictureFormat) {
        return new Picture().setPictureFormat(pictureFormat);
    }

    public PictureFormat getPictureFormat() {
        return pictureFormat;
    }

    public Picture setPictureFormat(PictureFormat pictureFormat) {
        this.pictureFormat = pictureFormat;
        return this;
    }

    public WritableResource getData() {
        return data;
    }

    public Picture setData(WritableResource data) {
        this.data = data;
        return this;
    }

    @Override
    public Picture accept(FormatterVisitor visitor) throws Throwable {
        visitor.visitPicture(this);
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("pictureFormat", pictureFormat)
            .add("data", data)
            .toString();
    }
}
