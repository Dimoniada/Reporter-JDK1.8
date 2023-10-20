package com.model.formatter.html;

import com.google.common.base.Objects;
import com.model.domain.style.LayoutStyle;
import com.model.domain.style.Style;
import com.model.domain.style.TextStyle;
import com.model.formatter.html.style.CssStyle;
import com.model.formatter.html.style.HtmlStyleService;
import com.model.formatter.html.tag.Html4Font;
import com.model.formatter.html.tag.HtmlTable;
import com.model.formatter.html.tag.HtmlTag;
import com.model.utils.LocalizedNumberUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.ParseException;

public class TagCreator {
    protected OutputStreamWriter outputStreamWriter;
    protected DecimalFormat decimalFormat;

    public TagCreator(OutputStreamWriter outputStreamWriter, DecimalFormat decimalFormat) {
        this.outputStreamWriter = outputStreamWriter;
        this.decimalFormat = decimalFormat;
    }

    public static String htmlStyleId(Style style) {
        return "_" + Integer.toHexString(Objects.hashCode(style));
    }

    public TagCreator write(String text) throws IOException {
        outputStreamWriter.write(text);
        return this;
    }

    public void writeTag(
        HtmlTag tag,
        String text,
        Style style,
        boolean isUseHtml4Tags,
        boolean isStyleInHeader,
        boolean isBordersCollapse,
        Boolean needCloseTag
    ) throws IOException, ParseException {
        final CssStyle cssStyle = new CssStyle();
        final boolean isHtmlTable = tag instanceof HtmlTable;
        if (isUseHtml4Tags) {
            final LayoutStyle layoutStyle = LayoutStyle.extractLayoutStyle(style);
            HtmlStyleService.fillHtml4StyleTagsFromStyle(tag, layoutStyle, isHtmlTable);
            write(String.format("<%s%s>", tag.getTagName(), tag.attributesToHtmlString(true)));
            if (StringUtils.hasText(text)) {
                final String formattedText =
                    HtmlStyleService.escapeHtml(
                        LocalizedNumberUtils.applyDecimalFormat(text, style, decimalFormat)
                    );
                final TextStyle textStyle = HtmlStyleService.extractTextStyle(style);
                if (textStyle != null) {
                    final Html4Font html4Font = HtmlStyleService.convertHtml4Font(textStyle);
                    write(
                        String
                            .format(
                                "<%s%s>",
                                html4Font.getTagName(),
                                html4Font.attributesToHtmlString(true)
                            )
                            + formattedText
                            + html4Font.close()
                    );
                } else {
                    write(formattedText);
                }
            }
        } else {
            if (isStyleInHeader) {
                tag.setClass(htmlStyleId(style));
            } else if (isBordersCollapse) {
                HtmlStyleService.fillCssStyleFromStyle(cssStyle, style, true, false);
                cssStyle.setBorderCollapse("collapse");
                tag.setStyle(cssStyle);
            } else if (style != null) {
                HtmlStyleService.fillCssStyleFromStyle(cssStyle, style, isHtmlTable, false);
                tag.setStyle(cssStyle);
            }
            write(String.format("<%s%s>", tag.getTagName(), tag.attributesToHtmlString(false)));
            if (StringUtils.hasText(text)) {
                write(
                    HtmlStyleService
                        .escapeHtml(
                            LocalizedNumberUtils.applyDecimalFormat(text, style, decimalFormat)
                        )
                );
            }
        }
        if (needCloseTag) {
            write(tag.close());
        }
    }

    public OutputStreamWriter getOutputStreamWriter() {
        return outputStreamWriter;
    }

    public TagCreator setOutputStreamWriter(OutputStreamWriter outputStreamWriter) {
        this.outputStreamWriter = outputStreamWriter;
        return this;
    }

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public TagCreator setDecimalFormat(DecimalFormat decimalFormat) {
        this.decimalFormat = decimalFormat;
        return this;
    }
}
