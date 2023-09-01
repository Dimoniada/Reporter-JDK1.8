package com.model.formatter.excel.style;

import com.model.domain.style.BorderStyle;
import com.model.domain.style.LayoutStyle;
import com.model.domain.style.LayoutTextStyle;
import com.model.domain.style.TextStyle;
import com.model.domain.style.constant.Color;
import com.model.domain.style.constant.FillPattern;
import com.model.domain.style.constant.HorAlignment;
import com.model.domain.style.constant.VertAlignment;


/**
 * Heading style
 */
public class HeadingStyle extends LayoutTextStyle {

    public static HeadingStyle create() {
        final HeadingStyle headerStyle = new HeadingStyle();

        final BorderStyle border = BorderStyle.create().setColor(Color.BLACK);

        final LayoutStyle layoutStyle = LayoutStyle.create()
            .setBorderTop(border)
            .setBorderLeft(border)
            .setBorderRight(border)
            .setBorderBottom(border)
            .setFillForegroundColor(Color.LIGHT_TURQUOISE)
            .setFillPattern(FillPattern.SOLID_FOREGROUND)
            .setHorAlignment(HorAlignment.CENTER)
            .setVertAlignment(VertAlignment.CENTER);
        headerStyle.setLayoutStyle(layoutStyle);

        final TextStyle textStyle = TextStyle.create("UTF-8")
            .setFontSize((short) 14)
            .setBold(true)
            .setColor(Color.BLUE);
        headerStyle.setTextStyle(textStyle);

        return headerStyle;
    }

}
