package com.model.formatter.pdf.renders;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.model.domain.style.geometry.GeometryDetails;

public class CustomCellPdfRenderer extends CellRenderer implements CustomPdfRenderer {
    private final GeometryDetails geometryDetails;

    public CustomCellPdfRenderer(
            Cell modelElement,
            GeometryDetails geometryDetails
    ) {
        super(modelElement);
        this.geometryDetails = geometryDetails;
    }

    @Override
    public IRenderer getNextRenderer() {
        return new CustomCellPdfRenderer((Cell) modelElement, geometryDetails);
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

    @Override
    public GeometryDetails getGeometryDetails() {
        return geometryDetails;
    }
}
