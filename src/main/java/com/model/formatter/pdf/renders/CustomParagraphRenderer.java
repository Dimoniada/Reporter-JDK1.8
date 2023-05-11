package com.model.formatter.pdf.renders;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import com.model.domain.styles.geometry.GeometryDetails;

public class CustomParagraphRenderer extends ParagraphRenderer implements CustomRenderer {
    GeometryDetails geometryDetails;

    public CustomParagraphRenderer(
        Paragraph modelElement,
        GeometryDetails geometryDetails
    ) {
        super(modelElement);
        this.geometryDetails = geometryDetails;
    }

    @Override
    public IRenderer getNextRenderer() {
        return new CustomParagraphRenderer((Paragraph) modelElement, geometryDetails);
    }

    @Override
    public GeometryDetails getGeometryDetails() {
        return geometryDetails;
    }

    @Override
    protected void applyRotationLayout(Rectangle layoutBox) {
        customApplyRotationLayout();
        super.applyRotationLayout(layoutBox);
    }

    @Override
    protected void beginTransformationIfApplied(PdfCanvas canvas) {
        customBeginTransformationIfApplied(canvas);
    }
}
