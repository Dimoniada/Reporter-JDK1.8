package com.reporter.formatter.pdf.style;

import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.Property;
import com.model.domain.style.BorderStyle;
import com.model.domain.style.LayoutStyle;
import com.model.domain.style.constant.BorderWeight;
import com.model.domain.style.constant.Color;
import com.model.domain.style.constant.HorAlignment;
import com.model.formatter.pdf.style.PdfStyleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

        Assertions.assertEquals(PdfStyleService.toPdfColor(Color.TEAL),
            ((Background) text.getProperty(Property.BACKGROUND)).getColor());
    }

    @Test
    public void testConvertHorizontalAlignment() {
        PdfStyleService.convertHorizontalAlignment(text, layoutStyle);

        Assertions.assertEquals(PdfStyleService.toPdfHorAlignment(HorAlignment.RIGHT),
            text.getProperty(Property.TEXT_ALIGNMENT));
    }

    @Test
    public void testConvertBorderColor() {
        PdfStyleService.convertBorderColor(border, Color.PINK);

        Assertions.assertEquals(PdfStyleService.toPdfColor(Color.PINK), border.getColor());
    }

    @Test
    public void testConvertBorder() {
        PdfStyleService.convertBorder(layoutStyle.getBorderLeft(), text::setBorderLeft, false);

        Assertions.assertEquals(PdfStyleService.toPdfBorder(BorderWeight.MEDIUM),
            text.getProperty(Property.BORDER_LEFT));
    }

}
