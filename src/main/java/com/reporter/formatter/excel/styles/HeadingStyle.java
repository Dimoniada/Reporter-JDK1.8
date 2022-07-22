package com.reporter.formatter.excel.styles;

import com.reporter.domain.styles.BorderStyle;
import com.reporter.domain.styles.LayoutStyle;
import com.reporter.domain.styles.LayoutTextStyle;
import com.reporter.domain.styles.TextStyle;
import com.reporter.domain.styles.constants.Color;
import com.reporter.domain.styles.constants.FillPattern;
import com.reporter.domain.styles.constants.HorAlignment;
import com.reporter.domain.styles.constants.VertAlignment;


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
