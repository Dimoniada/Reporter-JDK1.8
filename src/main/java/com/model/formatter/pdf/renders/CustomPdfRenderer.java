package com.model.formatter.pdf.renders;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.Transform;
import com.model.domain.style.constant.HorAlignment;
import com.model.domain.style.constant.VertAlignment;
import com.model.domain.style.geometry.GeometryDetails;
import com.model.formatter.pdf.PdfDetails;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface CustomPdfRenderer {
    GeometryDetails getGeometryDetails();

    Rectangle getOccupiedAreaBBox();

    IPropertyContainer getModelElement();

    <T> T getProperty(int key);

    LayoutArea getOccupiedArea();

    Rectangle applyMargins(Rectangle rect, boolean reverse);

    default void customApplyRotationLayout() {
        if (getGeometryDetails().getTransformCenter() != null) {
            getGeometryDetails()
                .getTransformCenter()
                .getValueFor(PdfDetails.EXTENSION)
                .ifPresent(center -> {
                    final Rectangle rectangle = getOccupiedAreaBBox();

                    final Map<HorAlignment, Function<Rectangle, Float>> xPointMap =
                        new HashMap<HorAlignment, Function<Rectangle, Float>>() {{
                            put(HorAlignment.GENERAL, r -> (r.getLeft() + r.getRight()) / 2);
                            put(HorAlignment.LEFT, Rectangle::getLeft);
                            put(HorAlignment.CENTER, r -> (r.getLeft() + r.getRight()) / 2);
                            put(HorAlignment.RIGHT, Rectangle::getRight);
                        }};

                    final Map<VertAlignment, Function<Rectangle, Float>> yPointMap =
                        new HashMap<VertAlignment, Function<Rectangle, Float>>() {{
                            put(VertAlignment.TOP, Rectangle::getTop);
                            put(VertAlignment.CENTER, r -> (r.getTop() + r.getBottom()) / 2);
                            put(VertAlignment.BOTTOM, Rectangle::getBottom);
                        }};

                    // Rotation (and aligned) center
                    getModelElement()
                        .setProperty(
                            Property.ROTATION_POINT_X,
                            xPointMap.get(center.getKey()).apply(rectangle)
                        );
                    getModelElement()
                        .setProperty(
                            Property.ROTATION_POINT_Y,
                            yPointMap.get(center.getValue()).apply(rectangle)
                        );
                    getModelElement()
                        .setProperty(
                            Property.POSITION,
                            LayoutPosition.RELATIVE
                        );
                });
        }
    }

    default void customBeginTransformationIfApplied(PdfCanvas canvas) {
        // Horizontal & Vertical scaling
        final Rectangle backgroundArea = applyMargins(getOccupiedArea().clone().getBBox(), false);
        final float x = backgroundArea.getX();
        final float y = backgroundArea.getY();
        final float height = backgroundArea.getHeight();
        final float width = backgroundArea.getWidth();

        final AffineTransform transform = AffineTransform.getTranslateInstance(-x, -1 * (y + height));
        transform.preConcatenate(Transform.getAffineTransform(getProperty(Property.TRANSFORM), width, height));
        transform.preConcatenate(AffineTransform.getTranslateInstance(x, y + height));
        canvas.saveState().concatMatrix(transform);
    }
}
