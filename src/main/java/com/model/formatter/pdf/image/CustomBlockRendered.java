package com.model.formatter.pdf.image;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import com.model.domain.styles.constants.HorAlignment;
import com.model.domain.styles.constants.VertAlignment;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CustomBlockRendered extends ParagraphRenderer {
    Map.Entry<HorAlignment, VertAlignment> center;

    public CustomBlockRendered(
        Paragraph modelElement,
        Map.Entry<HorAlignment, VertAlignment> center
    ) {
        super(modelElement);
        this.center = center;
    }

    @Override
    public IRenderer getNextRenderer() {
        return new CustomBlockRendered((Paragraph) modelElement, center);
    }

    @Override
    protected void applyRotationLayout(Rectangle layoutBox) {
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
        modelElement
            .setProperty(
                Property.ROTATION_POINT_X,
                xPointMap.get(center.getKey()).apply(rectangle)
            );
        modelElement
            .setProperty(
                Property.ROTATION_POINT_Y,
                yPointMap.get(center.getValue()).apply(rectangle)
            );
        modelElement
            .setProperty(
                Property.POSITION,
                LayoutPosition.RELATIVE
            );
        super.applyRotationLayout(layoutBox);
    }
}
