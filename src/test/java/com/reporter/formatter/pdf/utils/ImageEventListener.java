package com.reporter.formatter.pdf.utils;

import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class ImageEventListener implements IEventListener, AutoCloseable {

    public ImageEventListener(String imageName) {
        this.imagePath = new File("./" + imageName).toPath();
    }

    private final Path imagePath;

    public void eventOccurred(IEventData eventData, EventType type) {
        if (eventData instanceof ImageRenderInfo) {
            final ImageRenderInfo imageRenderInfo = (ImageRenderInfo) eventData;
            try {
                if (imageRenderInfo.getImage() != null) {
                    final PdfImageXObject imageXObject = imageRenderInfo.getImage();
                    Files.write(imagePath, imageXObject.getImageBytes());
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return null;
    }

    @Override
    public void close() throws Exception {
        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
        }
    }

    public Path getImagePath() {
        return imagePath;
    }
}