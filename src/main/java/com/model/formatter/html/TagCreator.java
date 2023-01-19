package com.model.formatter.html;

import com.google.common.base.Objects;
import com.model.domain.DocumentItem;
import com.model.domain.TextItem;
import com.model.domain.styles.LayoutStyle;
import com.model.domain.styles.Style;
import com.model.domain.styles.TextStyle;
import com.model.formatter.html.styles.CssStyle;
import com.model.formatter.html.styles.HtmlColgroupTag;
import com.model.formatter.html.styles.HtmlStyleService;
import com.model.formatter.html.tag.*;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.ParseException;

import static com.model.utils.LocalizedNumberUtils.applyDecimalFormat;

public class TagCreator {
    protected OutputStreamWriter outputStreamWriter;
    protected DecimalFormat decimalFormat;
    protected DocumentItem item;

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
        Style style,
        boolean isUseHtml4Tags,
        boolean isStyleInHeader,
        HtmlColgroupTag useHtml4ColgroupTag,
        Boolean needCloseTag
    ) throws IOException, ParseException {
        final CssStyle cssStyle = new CssStyle();
        final boolean isHtmlTable = tag instanceof HtmlTable;
        final boolean isCol = tag instanceof HtmlCol;
        final boolean isCell = tag instanceof HtmlTableCell;
        if (isUseHtml4Tags) {
            if (!(isCell && useHtml4ColgroupTag.getEnabled())) {
                final LayoutStyle layoutStyle = HtmlStyleService.extractLayoutStyle(style);
                HtmlStyleService.fillHtml4StyleTagsFromStyle(tag, layoutStyle, isHtmlTable);
            }
            write(String.format("<%s%s>", tag.getTagName(), tag.attributesToHtmlString(true)));
            if (item instanceof TextItem<?>) {
                final TextItem<?> textItem = (TextItem<?>) item;
                final String formattedText = HtmlStyleService.escapeHtml(applyDecimalFormat(textItem, decimalFormat));
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
            if (isStyleInHeader &&
                !(isCol && useHtml4ColgroupTag.getEnabled() && useHtml4ColgroupTag.getWriteInplace())
            ) {
                tag.setClass(htmlStyleId(style));
            } else if (isHtmlTable && useHtml4ColgroupTag.getEnabled()) {
                HtmlStyleService.fillCssStyleFromStyle(cssStyle, style, true, false);
                cssStyle.setBorderCollapse("collapse");
                tag.setStyle(cssStyle);
            } else if (isCol && useHtml4ColgroupTag.getWriteInplace()) {
                HtmlStyleService.fillCssStyleFromStyle(cssStyle, style, false, false);
                tag.setStyle(cssStyle);
            } else if (item != null && (item.getStyle() != null || style != null)) {
                HtmlStyleService.fillCssStyleFromStyle(cssStyle, style, isHtmlTable, false);
                tag.setStyle(cssStyle);
            }
            write(String.format("<%s%s>", tag.getTagName(), tag.attributesToHtmlString(false)));
            if (item instanceof TextItem<?>) {
                write(HtmlStyleService.escapeHtml(applyDecimalFormat((TextItem<?>) item, decimalFormat)));
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

    public DocumentItem getItem() {
        return item;
    }

    public TagCreator setItem(DocumentItem item) {
        this.item = item;
        return this;
    }
}
