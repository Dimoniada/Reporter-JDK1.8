package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.domain.core.PictureItem;
import com.model.domain.style.constant.PictureFormat;
import com.model.formatter.FormatterVisitor;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Picture class,
 * contains InputStream data, picture format {@link PictureFormat} and picture text if data == null
 */
public class Picture extends PictureItem<Picture> {

    protected PictureFormat format;

    public static Picture create(byte[] data, PictureFormat pictureFormat) {
        return new Picture()
            .setData(data)
            .setFormat(pictureFormat);
    }

    public static Picture create(PictureFormat pictureFormat) {
        return new Picture()
            .setFormat(pictureFormat);
    }

    public static Picture create() {
        return new Picture();
    }

    /**
     * @return picture height in pixels
     * @throws IOException if an error occurs during picture data reading
     */
    public int getHeight() throws IOException {
        return data == null
            ? 0
            : ImageIO.read(new ByteArrayInputStream(data)).getHeight();
    }

    /**
     * @return picture width in pixels
     * @throws IOException if an error occurs during picture data reading
     */
    public int getWidth() throws IOException {
        return data == null
            ? 0
            : ImageIO.read(new ByteArrayInputStream(data)).getWidth();
    }

    public PictureFormat getFormat() {
        return format;
    }

    public Picture setFormat(PictureFormat format) {
        this.format = format;
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
            .add("format", format)
            .add("super", super.toString())
            .toString();
    }
}
