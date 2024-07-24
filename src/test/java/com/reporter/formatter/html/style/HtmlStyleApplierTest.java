package com.reporter.formatter.html.style;

import com.model.domain.style.constant.Color;
import com.model.formatter.html.style.CssStyle;
import com.model.formatter.html.tag.Html4Font;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HtmlStyleApplierTest {
    private CssStyle cssStyle;

    @BeforeEach
    public void init() {
        cssStyle = new CssStyle();
    }

    @Test
    public void testHtml4FontAttributes() {
        final Html4Font font = new Html4Font();

        font.setSize(13)
            .setColor("#" + Color.GREEN.buildColorString())
            .setFace("monospace");

        Assertions.assertEquals(
            "color=\"#00FF00\" face=\"monospace\" size=\"13\"",
            font.attributesToHtmlString(true).trim()
        );
    }

    @Test
    public void testConvertCssPropertyFontSize() {

        cssStyle.setFontSize(12);

        Assertions.assertEquals("font-size:12pt", cssStyle.toCssStyleString());
    }

    @Test
    public void testConvertCssPropertyFontWeight() {

        cssStyle.setFontWeight("bold");

        Assertions.assertEquals("font-weight:bold", cssStyle.toCssStyleString());
    }

    @Test
    public void testConvertCssPropertyFontStyle() {

        cssStyle.setFontStyle("italic");

        Assertions.assertEquals("font-style:italic", cssStyle.toCssStyleString());
    }

    @Test
    public void testConvertCssPropertyFontColor() {

        cssStyle.setFontColor("#F0C35E");

        Assertions.assertEquals("color:#F0C35E", cssStyle.toCssStyleString());
    }

    @Test
    public void testConvertCssPropertyFontFamily() {

        cssStyle.setFontFamily("Tahoma");

        Assertions.assertEquals("font-family:Tahoma,monospace", cssStyle.toCssStyleString());
    }

    @Test
    public void testConvertCssPropertyTextHorAlign() {

        cssStyle.setTextHorAlignment("right");

        Assertions.assertEquals("text-align:right", cssStyle.toCssStyleString());
    }

    @Test
    public void testConvertCssPropertyTextVertAlign() {

        cssStyle.setTextVertAlignment("bottom");

        Assertions.assertEquals("display:table-cell;vertical-align:bottom", cssStyle.toCssStyleString());
    }

    @Test
    public void testConvertCssPropertyWidth() {

        cssStyle.setWidth("100px");

        Assertions.assertEquals("width:100px", cssStyle.toCssStyleString());
    }

    @Test
    public void testConvertCssPropertyHeight() {

        cssStyle.setHeight("100px");

        Assertions.assertEquals("height:100px", cssStyle.toCssStyleString());
    }

    @Test
    public void testConvertCssPropertyTransformCenter() {

        cssStyle.setTransformCenter("top top");

        Assertions.assertEquals("transform-origin:top top", cssStyle.toCssStyleString());
    }

    @Test
    public void testConvertCssPropertyTransform() {

        cssStyle.setTransform("scaleX(0.7)");

        Assertions.assertEquals("transform:scaleX(0.7)", cssStyle.toCssStyleString());
    }

    @Test
    public void testConvertCssPropertyBorderCollapse() {

        cssStyle.setBorderCollapse("separate");

        Assertions.assertEquals("border-collapse:separate", cssStyle.toCssStyleString());
    }

    @Test
    public void testConvertCssPropertyBorderTop() {

        cssStyle.setBorderTop("2px solid");

        Assertions.assertEquals("border-collapse:collapse;border-top:2px solid", cssStyle.toCssStyleString());
    }

    @Test
    public void testConvertCssPropertyBorderLeft() {

        cssStyle.setBorderLeft("1px solid");

        Assertions.assertEquals("border-collapse:collapse;border-left:1px solid", cssStyle.toCssStyleString());
    }

    @Test
    public void testConvertCssPropertyBorderRight() {

        cssStyle.setBorderRight("3px solid");

        Assertions.assertEquals("border-collapse:collapse;border-right:3px solid", cssStyle.toCssStyleString());
    }

    @Test
    public void testConvertCssPropertyBorderBottom() {

        cssStyle.setBorderBottom("double");

        Assertions.assertEquals("border-bottom:double;border-collapse:collapse", cssStyle.toCssStyleString());
    }

    @Test
    public void testConvertCssPropertyPageBreakAfter() {

        cssStyle.setPageBreakAfter("right");

        Assertions.assertEquals("page-break-after:right", cssStyle.toCssStyleString());
    }

    @Test
    public void testConvertCssPropertyBackgroundColor() {

        cssStyle.setBackgroundColor("#A36BCD");

        Assertions.assertEquals("background-color:#A36BCD", cssStyle.toCssStyleString());
    }

    @Test
    public void testConvertPropertyBorderHtml4() {

        cssStyle.setBorderHtml4(1);

        Assertions.assertEquals("border=\"1\"", cssStyle.toHtml4StyleString());
    }

    @Test
    public void testConvertPropertyCellspacingHtml4() {

        cssStyle.setCellspacingHtml4(5);

        Assertions.assertEquals("cellspacing=\"5\"", cssStyle.toHtml4StyleString());
    }

    @Test
    public void testConvertPropertyBgcolorHtml4() {

        cssStyle.setBgcolorHtml4("#123456");

        Assertions.assertEquals("bgcolor=\"#123456\"", cssStyle.toHtml4StyleString());
    }

    @Test
    public void testConvertPropertyBgcolorAndCellspacingHtml4() {

        cssStyle.setCellspacingHtml4(5);
        cssStyle.setBgcolorHtml4("#123456");

        Assertions.assertEquals("bgcolor=\"#123456\" cellspacing=\"5\"", cssStyle.toHtml4StyleString());
    }

    @Test
    public void testConvertPropertyAlignHtml4() {

        cssStyle.setAlignHtml4("left");

        Assertions.assertEquals("align=\"left\"", cssStyle.toHtml4StyleString());
    }

    @Test
    public void testConvertProperties() {

        cssStyle.setFontSize(12);
        cssStyle.setFontWeight("bold");
        cssStyle.setFontStyle("italic");
        cssStyle.setFontColor("#F0C35E");
        cssStyle.setFontFamily("Tahoma");

        cssStyle.setTextHorAlignment("right");
        cssStyle.setTransform("scaleX(0.7) scaleY(2.0) rotate(370deg)");
        cssStyle.setBorderCollapse("separate");
        cssStyle.setBorderTop("2px solid");
        cssStyle.setBorderLeft("1px solid");
        cssStyle.setBorderRight("3px solid");
        cssStyle.setBorderBottom("double");
        cssStyle.setBackgroundColor("#A36BCD");

        cssStyle.setBorderHtml4(1);
        cssStyle.setCellspacingHtml4(5);
        cssStyle.setBgcolorHtml4("#123456");
        cssStyle.setAlignHtml4("left");

        Assertions.assertEquals(
            "background-color:#A36BCD;" +
                "border-bottom:double;" +
                "border-collapse:separate;" +
                "border-left:1px solid;" +
                "border-right:3px solid;" +
                "border-top:2px solid;" +
                "color:#F0C35E;" +
                "font-family:Tahoma,monospace;" +
                "font-size:12pt;" +
                "font-style:italic;" +
                "font-weight:bold;" +
                "text-align:right;" +
                "transform:scaleX(0.7) scaleY(2.0) rotate(370deg)",
            cssStyle.toCssStyleString()
        );

        Assertions.assertEquals("align=\"left\" " +
            "bgcolor=\"#123456\" " +
            "border=\"1\" " +
            "cellspacing=\"5\"", cssStyle.toHtml4StyleString());
    }

}
