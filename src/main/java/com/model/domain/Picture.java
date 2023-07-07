package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.domain.styles.constants.PictureFormat;
import com.model.formatter.FormatterVisitor;
import org.springframework.core.io.WritableResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Picture extends DocumentItem {

    protected PictureFormat pictureFormat;

    protected WritableResource data;

    protected String name;

    public static Picture create(WritableResource data, PictureFormat pictureFormat) {
        return new Picture()
            .setData(data)
            .setPictureFormat(pictureFormat);
    }

    public static Picture create() {
        return new Picture();
    }

    /**
     * @return picture height in pixels
     * @throws IOException if an error occurs during reading
     */
    public int getHeightFromData() throws IOException {
        if (data == null) {
            return 0;
        }
        final BufferedImage buf = ImageIO.read(data.getInputStream());
        return buf.getHeight();
    }

    /**
     * @return picture width in pixels
     * @throws IOException if an error occurs during reading
     */
    public int getWidthFromData() throws IOException {
        if (data == null) {
            return 0;
        }
        final BufferedImage buf = ImageIO.read(data.getInputStream());
        return buf.getWidth();
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

    public String getName() {
        return name;
    }

    public Picture setName(String name) {
        this.name = name;
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
            .add("name", name)
            .toString();
    }
}
