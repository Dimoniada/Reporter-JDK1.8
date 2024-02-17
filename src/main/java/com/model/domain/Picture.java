package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.domain.style.constant.PictureFormat;
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

    protected byte[] pictureData;

    protected String pictureText;

    public static Picture create(byte[] data, PictureFormat pictureFormat) {
        return new Picture()
            .setPictureData(data)
            .setPictureFormat(pictureFormat);
    }

    public static Picture create(String text, PictureFormat pictureFormat) {
        return new Picture()
            .setPictureText(text)
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
        if (pictureData == null) {
            return 0;
        }
        final BufferedImage buf = ImageIO.read(new ByteArrayInputStream(pictureData));
        return buf.getHeight();
    }

    /**
     * @return picture width in pixels
     * @throws IOException if an error occurs during picture data reading
     */
    public int getPictureWidth() throws IOException {
        if (pictureData == null) {
            return 0;
        }
        final BufferedImage buf = ImageIO.read(new ByteArrayInputStream(pictureData));
        return buf.getWidth();
    }

    public PictureFormat getPictureFormat() {
        return pictureFormat;
    }

    public Picture setPictureFormat(PictureFormat pictureFormat) {
        this.pictureFormat = pictureFormat;
        return this;
    }

    public byte[] getPictureData() {
        return pictureData;
    }

    public Picture setPictureData(byte[] pictureData) {
        this.pictureData = pictureData;
        return this;
    }

    public String getPictureText() {
        return pictureText;
    }

    public Picture setPictureText(String pictureText) {
        this.pictureText = pictureText;
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
            .add("pictureData", pictureData)
            .add("pictureText", pictureText)
            .toString();
    }
}
