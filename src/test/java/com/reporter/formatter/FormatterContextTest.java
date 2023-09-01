package com.reporter.formatter;

import com.model.domain.FontService;
import com.model.domain.TableCell;
import com.model.domain.TableHeaderCell;
import com.model.domain.style.BorderStyle;
import com.model.domain.style.FontFamilyStyle;
import com.model.domain.style.LayoutStyle;
import com.model.domain.style.LayoutTextStyle;
import com.model.domain.style.Style;
import com.model.domain.style.StyleCondition;
import com.model.domain.style.TextStyle;
import com.model.domain.style.constant.BorderWeight;
import com.model.domain.style.constant.Color;
import com.model.domain.style.constant.FillPattern;
import com.model.domain.style.constant.HorAlignment;
import com.model.domain.style.constant.VertAlignment;
import com.model.formatter.FormatterContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.supercsv.prefs.CsvPreference;

import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Objects;

class FormatterContextTest extends BaseFormatterContext {

    @BeforeEach
    public void init() {
        super.initFormatterContext();
    }

    @Test
    void testCreateFormatterContext() {
        Assertions.assertEquals(encoding, formatterContext.getEncoding());
        Assertions.assertEquals(locale, formatterContext.getLocale());
        Assertions.assertEquals(timeZone, formatterContext.getTimeZone());
        Assertions.assertEquals(decimalFormat, formatterContext.getDecimalFormat());
        Assertions.assertEquals(csvDelimiter, formatterContext.getCsvDelimiter());
    }

    @Test
    void testCreateCsvPreference() {
        final CsvPreference csvPreference = formatterContext.createCsvPreference();
        Assertions.assertEquals(csvDelimiter.charValue(), csvPreference.getDelimiterChar());
        Assertions.assertEquals(FormatterContext.CSV_QUOTE, csvPreference.getQuoteChar());
        Assertions.assertEquals(FormatterContext.CSV_END_OF_LINE, csvPreference.getEndOfLineSymbols());
    }

    @Test
    void testCreateFormattedZoneDateTime() {
        final String formattedZoneDateTime = formatterContext.createFormattedZoneDateTime();
        final String formatPattern = DateTimeFormatterBuilder
            .getLocalizedDateTimePattern(
                FormatStyle.LONG,
                FormatStyle.LONG,
                IsoChronology.INSTANCE,
                locale
            );
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern, locale);
        Assertions.assertEquals(
            ZonedDateTime.now(timeZone.toZoneId()).getMinute(),
            ZonedDateTime.parse(formattedZoneDateTime, formatter).getMinute()
        );
    }

    @Test
    void testCreateFontService() {
        Assertions.assertDoesNotThrow(() -> {
            final FontService fontService = formatterContext.createFontService();
            Assertions.assertTrue(
                fontService
                    .getFonts()
                    .values()
                    .stream()
                    .anyMatch(Objects::nonNull)
            );
        });
    }

    @Test
    void testCreateHeaderCellStyle() {
        final Style style = formatterContext.createHeaderCellStyle();
        Assertions.assertEquals(LayoutTextStyle.class, style.getClass());

        final StyleCondition condition = style.getCondition();
        Assertions.assertEquals(TableHeaderCell.class, condition.getClazz());

        final TextStyle textStyle = ((LayoutTextStyle) style).getTextStyle();
        Assertions.assertEquals(FontFamilyStyle.SANS_SERIF, textStyle.getFontFamilyStyle());
        Assertions.assertEquals(FormatterContext.STYLE_BIG_FONT_SIZE, textStyle.getFontSize());
        Assertions.assertTrue(textStyle.isBold());

        final LayoutStyle layoutStyle = ((LayoutTextStyle) style).getLayoutStyle();
        final BorderStyle borderHeader =
            BorderStyle.create(Color.GREY_50_PERCENT, BorderWeight.DOUBLE);
        Assertions.assertEquals(HorAlignment.LEFT, layoutStyle.getHorAlignment());
        Assertions.assertEquals(VertAlignment.CENTER, layoutStyle.getVertAlignment());
        Assertions.assertEquals(FillPattern.SOLID_FOREGROUND, layoutStyle.getFillPattern());
        Assertions.assertEquals(borderHeader, layoutStyle.getBorderTop());
        Assertions.assertEquals(borderHeader, layoutStyle.getBorderLeft());
        Assertions.assertEquals(borderHeader, layoutStyle.getBorderRight());
        Assertions.assertEquals(borderHeader, layoutStyle.getBorderBottom());
    }

    @Test
    void testCreateRowStyleInterlinear() {
        final Style style = formatterContext.createRowStyleInterlinear();
        Assertions.assertEquals(LayoutTextStyle.class, style.getClass());

        final StyleCondition condition = style.getCondition();
        Assertions.assertEquals(TableCell.class, condition.getClazz());

        final TextStyle textStyle = ((LayoutTextStyle) style).getTextStyle();
        Assertions.assertEquals(FontFamilyStyle.SERIF, textStyle.getFontFamilyStyle());
        Assertions.assertEquals(FormatterContext.STYLE_NORMAL_FONT_SIZE, textStyle.getFontSize());

        final LayoutStyle layoutStyle = ((LayoutTextStyle) style).getLayoutStyle();
        final BorderStyle borderHeader =
            BorderStyle.create(Color.GREY_50_PERCENT, BorderWeight.THIN);
        Assertions.assertEquals(HorAlignment.LEFT, layoutStyle.getHorAlignment());
        Assertions.assertEquals(VertAlignment.CENTER, layoutStyle.getVertAlignment());
        Assertions.assertEquals(FillPattern.SOLID_FOREGROUND, layoutStyle.getFillPattern());
        Assertions.assertEquals(Color.GREEN_LIGHT, layoutStyle.getFillForegroundColor());
        Assertions.assertEquals(Color.GREEN_LIGHT, layoutStyle.getFillBackgroundColor());
        Assertions.assertEquals(borderHeader, layoutStyle.getBorderTop());
        Assertions.assertEquals(borderHeader, layoutStyle.getBorderLeft());
        Assertions.assertEquals(borderHeader, layoutStyle.getBorderRight());
        Assertions.assertEquals(borderHeader, layoutStyle.getBorderBottom());
    }

    @Test
    void testCreateRowStyleNormal() {
        final Style style = formatterContext.createRowStyleNormal();
        Assertions.assertEquals(LayoutTextStyle.class, style.getClass());

        final StyleCondition condition = style.getCondition();
        Assertions.assertEquals(TableCell.class, condition.getClazz());

        final TextStyle textStyle = ((LayoutTextStyle) style).getTextStyle();
        Assertions.assertEquals(FontFamilyStyle.SERIF, textStyle.getFontFamilyStyle());
        Assertions.assertEquals(FormatterContext.STYLE_NORMAL_FONT_SIZE, textStyle.getFontSize());

        final LayoutStyle layoutStyle = ((LayoutTextStyle) style).getLayoutStyle();
        final BorderStyle borderHeader =
            BorderStyle.create(Color.GREY_50_PERCENT, BorderWeight.THIN);
        Assertions.assertEquals(HorAlignment.LEFT, layoutStyle.getHorAlignment());
        Assertions.assertEquals(FillPattern.SOLID_FOREGROUND, layoutStyle.getFillPattern());
        Assertions.assertEquals(borderHeader, layoutStyle.getBorderTop());
        Assertions.assertEquals(borderHeader, layoutStyle.getBorderLeft());
        Assertions.assertEquals(borderHeader, layoutStyle.getBorderRight());
        Assertions.assertEquals(borderHeader, layoutStyle.getBorderBottom());
    }

    @Test
    void testCreateStyleFooter() {
        final Style style = formatterContext.createStyleFooter();
        Assertions.assertEquals(TextStyle.class, style.getClass());

        final TextStyle textStyle = (TextStyle) style;
        Assertions.assertEquals(FontFamilyStyle.SERIF, textStyle.getFontFamilyStyle());
        Assertions.assertEquals(FormatterContext.STYLE_SMALL_FONT_SIZE, textStyle.getFontSize());
    }
}