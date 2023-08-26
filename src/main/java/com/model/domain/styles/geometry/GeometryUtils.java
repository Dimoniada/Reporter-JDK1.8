package com.model.domain.styles.geometry;

import com.model.domain.FontService;
import com.model.domain.Picture;
import com.model.domain.styles.LayoutStyle;
import com.model.domain.styles.Style;
import com.model.domain.styles.TextStyle;
import com.model.utils.ConverterUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class GeometryUtils {

    public static Dimension getTextDimension(String text, TextStyle textStyle) {
        final Font font = FontService.getFontResourceByTextStyle(textStyle);

        final BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = image.createGraphics();
        graphics2D.setFont(font);

        final FontMetrics fontMetrics = graphics2D.getFontMetrics();
        final int width = fontMetrics.stringWidth(text);
        final int height = fontMetrics.getHeight();
        graphics2D.dispose();

        return new Dimension(width, height);
    }

    /**
     * Retrieves picture dimensions regarding layout style
     *
     * @param picture input picture
     * @param style layout style with geometry details
     * @param extension input formatter extension to get details from style
     * @return dimension of picture regarding layout style
     * @throws IOException if an error occurs during picture data reading
     */
    public static Dimension getPictureDimension(Picture picture, Style style, String extension) throws IOException {
        final AtomicInteger width = new AtomicInteger(picture.getPictureWidth());
        final AtomicInteger height = new AtomicInteger(picture.getPictureHeight());
        final LayoutStyle layoutStyle = LayoutStyle.extractLayoutStyle(style);
        if (layoutStyle == null) {
            return new Dimension(width.get(), height.get());
        }
        final GeometryDetails geometryDetails = layoutStyle.getGeometryDetails();
        if (geometryDetails != null) {
            if (geometryDetails.getHeight() != null) {
                geometryDetails
                    .getHeight()
                    .getValueFor(extension)
                    .ifPresent(value -> height.set(ConverterUtils.<Integer>convert(value)));
            }
            if (geometryDetails.getWidth() != null) {
                geometryDetails
                    .getWidth()
                    .getValueFor(extension)
                    .ifPresent(value -> width.set(ConverterUtils.<Integer>convert(value)));
            }
        }
        return new Dimension(width.get(), height.get());
    }
}
