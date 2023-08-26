package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.domain.styles.constants.PictureFormat;
import com.model.formatter.FormatterVisitor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Picture class,
 * contains InputStream data, picture format {@link PictureFormat} and picture text if data == null
 */
public class Picture extends DocumentItem {

    protected PictureFormat pictureFormat;

    protected byte[] data;

    protected String text;

    public static Picture create(byte[] data, PictureFormat pictureFormat) {
        return new Picture()
            .setData(data)
            .setPictureFormat(pictureFormat);
    }

    public static Picture create(String text, PictureFormat pictureFormat) {
        return new Picture()
            .setText(text)
            .setPictureFormat(pictureFormat);
    }

    public static Picture create() {
        return new Picture();
    }

    /**
     * @return picture height in pixels
     * @throws IOException if an error occurs during picture data reading
     */
    public int getPictureHeight() throws IOException {
        if (data == null) {
            return 0;
        }
        final BufferedImage buf = ImageIO.read(new ByteArrayInputStream(data));
        return buf.getHeight();
    }

    /**
     * @return picture width in pixels
     * @throws IOException if an error occurs during picture data reading
     */
    public int getPictureWidth() throws IOException {
        if (data == null) {
            return 0;
        }
        final BufferedImage buf = ImageIO.read(new ByteArrayInputStream(data));
        return buf.getWidth();
    }

    public PictureFormat getPictureFormat() {
        return pictureFormat;
    }

    public Picture setPictureFormat(PictureFormat pictureFormat) {
        this.pictureFormat = pictureFormat;
        return this;
    }

    public byte[] getData() {
        return data;
    }

    public Picture setData(byte[] data) {
        this.data = data;
        return this;
    }

    public String getText() {
        return text;
    }

    public Picture setText(String text) {
        this.text = text;
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
            .add("text", text)
            .toString();
    }
}
