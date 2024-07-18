package com.reporter.formatter.pdf.style;

import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.BlockElement;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.Transform;
import com.itextpdf.layout.properties.UnitValue;
import com.model.domain.style.BorderStyle;
import com.model.domain.style.LayoutStyle;
import com.model.domain.style.constant.BorderWeight;
import com.model.domain.style.constant.Color;
import com.model.domain.style.constant.HorAlignment;
import com.model.domain.style.geometry.Geometry;
import com.model.domain.style.geometry.GeometryDetails;
import com.model.formatter.pdf.renders.CustomTransform;
import com.model.formatter.pdf.style.PdfStyleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PdfStyleApplierTest {
    private final Text text = new Text("");
    private final SolidBorder border = new SolidBorder(1);

    private final LayoutStyle layoutStyle = LayoutStyle.create()
        .setBorderTop(BorderStyle.create(Color.BLACK, BorderWeight.DOUBLE))
        .setBorderLeft(BorderStyle.create(Color.WHITE, BorderWeight.MEDIUM))
        .setBorderRight(BorderStyle.create(Color.RED, BorderWeight.THICK))
        .setBorderBottom(BorderStyle.create(Color.PINK, BorderWeight.THIN))
        .setFillBackgroundColor(Color.TEAL)
        .setHorAlignment(HorAlignment.RIGHT);


    @Test
    public void testConvertGroundColor() {
        PdfStyleService.convertGroundColor(text, layoutStyle);

        Assertions.assertEquals(
            PdfStyleService.toPdfColor(Color.TEAL),
            ((Background) text.getProperty(Property.BACKGROUND)).getColor()
        );
    }

    @Test
    public void testConvertHorizontalAlignment() {
        PdfStyleService.convertHorizontalAlignment(text, layoutStyle);

        Assertions.assertEquals(
            PdfStyleService.toPdfHorAlignment(HorAlignment.RIGHT),
            text.getProperty(Property.TEXT_ALIGNMENT)
        );
    }

    @Test
    public void testConvertBorderColor() {
        PdfStyleService.convertBorderColor(border, Color.PINK);

        Assertions.assertEquals(PdfStyleService.toPdfColor(Color.PINK), border.getColor());
    }

    @Test
    public void testConvertBorder() {
        PdfStyleService.convertBorder(layoutStyle.getBorderLeft(), text::setBorderLeft, false);

        Assertions.assertEquals(
            PdfStyleService.toPdfBorder(BorderWeight.MEDIUM),
            text.getProperty(Property.BORDER_LEFT)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConvertGeometryDetails() {
        final UnitValue uv = new UnitValue(UnitValue.POINT, 0);
        final CustomTransform transform = new CustomTransform(1);
        transform
            .addSingleTransform(
                new Transform.SingleTransform(1.5f, 0, 0, 2.0f, uv, uv)
            );

        // Arrange
        final BlockElement<?> mockElement = mock(BlockElement.class);
        final LayoutStyle layoutStyle = new LayoutStyle();
        final GeometryDetails geometryDetails = new GeometryDetails();

        final Geometry<Object> width = mock(Geometry.class);
        when(width.getValueFor(any())).thenReturn(Optional.of(100f));
        geometryDetails.setWidth(width);

        final Geometry<Object> height = mock(Geometry.class);
        when(height.getValueFor(any())).thenReturn(Optional.of(200f));
        geometryDetails.setHeight(height);

        final Geometry<Object> angle = mock(Geometry.class);
        when(angle.getValueFor(any())).thenReturn(Optional.of(45f));
        geometryDetails.setAngle(angle);

        final Geometry<Object> scaleX = mock(Geometry.class);
        when(scaleX.getValueFor(any())).thenReturn(Optional.of(1.5f));
        geometryDetails.setScaleX(scaleX);

        final Geometry<Object> scaleY = mock(Geometry.class);
        when(scaleY.getValueFor(any())).thenReturn(Optional.of(2.0f));
        geometryDetails.setScaleY(scaleY);

        layoutStyle.setGeometryDetails(geometryDetails);

        // Act
        PdfStyleService.convertGeometryDetails(mockElement, layoutStyle);

        // Assert
        verify((BlockElement<?>) mockElement).setWidth(100f);
        verify((BlockElement<?>) mockElement).setHeight(200f);
        verify((BlockElement<?>) mockElement).setRotationAngle((float) Math.PI / 4);

        final ArgumentCaptor<CustomTransform> siteIdCaptor = ArgumentCaptor.forClass(CustomTransform.class);
        verify((BlockElement<?>) mockElement).setProperty(eq(Property.TRANSFORM), siteIdCaptor.capture());

        final CustomTransform tr = siteIdCaptor.getValue();
        Assertions.assertArrayEquals(new float[]{1.5f, 0, 0, 2.0f}, tr.getLastSingleTransform().getFloats());
        Assertions.assertArrayEquals(new UnitValue[]{uv, uv}, tr.getLastSingleTransform().getUnitValues());
    }
}
