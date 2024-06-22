package com.model.utils;

import com.model.domain.style.constant.PictureFormat;
import org.apache.maven.surefire.shared.lang3.ArrayUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.function.Function;

public abstract class PictureUtils {

    public static PictureFormat getFormat(byte[] data) {
        //Parse format with ImageIO
        try (InputStream inputStream = new ByteArrayInputStream(data)) {
            final ImageInputStream iis = ImageIO.createImageInputStream(inputStream);
            final Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
            if (imageReaders.hasNext()) {
                final ImageReader reader = imageReaders.next();
                return mapContentTypeToPictureFormat(reader.getFormatName().toUpperCase(Locale.getDefault()));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't determine data format", e);
        }

        // Parse format manually
        for (final PictureFormat pictureFormat : PictureFormat.values()) {
            final Function<Byte[], Boolean> fileParser = pictureFormat.getFormatChecker();
            if (fileParser == null) {
                continue;
            }
            if (fileParser.apply(ArrayUtils.toObject(data))) {
                return pictureFormat;
            }
        }

        return null;
    }

    public static PictureFormat getFormat(Object obj) {
        return getFormat(serializePicture(obj));
    }

    public static byte[] serializePicture(Object obj) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(obj);
            out.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static PictureFormat mapContentTypeToPictureFormat(String format) {
        if ("JPEG".equals(format)) {
            return PictureFormat.JPG;
        }
        return PictureFormat.valueOf(format);
    }
}
