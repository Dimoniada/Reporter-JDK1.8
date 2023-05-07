package com.model.formatter.pdf.image;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;

import java.net.MalformedURLException;

class CellImageRenderer extends ParagraphRenderer {
    private final String text;

    CellImageRenderer(Paragraph modelElement, String text) {
        super(modelElement);
        this.text = text;
    }

    @Override
    public IRenderer getNextRenderer() {
        return new CellImageRenderer((Paragraph) modelElement, text);
    }

    @Override
    public void draw(DrawContext drawContext) {
        super.draw(drawContext);
        try (com.itextpdf.layout.Canvas canvas =
                new com.itextpdf.layout.Canvas(drawContext.getCanvas(), getOccupiedAreaBBox())
        ) {
            final com.itextpdf.layout.element.Paragraph p = new com.itextpdf.layout.element.Paragraph(text);
            final com.itextpdf.kernel.geom.Rectangle rect = getOccupiedAreaBBox();
            final Image image = new Image(ImageDataFactory.create(""));
            canvas.add(image);
            canvas.showTextAligned(
                p,
                (rect.getLeft() + rect.getRight()) / 2,
                (rect.getBottom() + rect.getTop()) / 2,
                getOccupiedArea().getPageNumber(),
                TextAlignment.CENTER,
                VerticalAlignment.MIDDLE,
                (float) Math.PI / 6
            );
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
