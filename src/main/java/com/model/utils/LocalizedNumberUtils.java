package com.model.utils;

import com.model.domain.style.Style;
import com.model.domain.style.StyleUtils;
import com.model.domain.style.TextStyle;
import org.springframework.util.StringUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * A set of methods for representing numbers in different {@link DecimalFormat} locales
 */
public abstract class LocalizedNumberUtils {
    /**
     * Number pattern per line
     */
    private static final String floatRegex = "[-+]?[0-9]*[,.]?[0-9]+";
    /**
     * Separator of the integer and fractional part of the number
     */
    private static final char DEFAULT_DECIMAL_SEPARATOR = ',';
    /**
     * The number of digits of the fractional part of the number
     */
    private static final int DEFAULT_DECIMAL_FRACTION_DIGITS = 3;

    /**
     * @param text          input string
     * @param style         domain style
     * @param decimalFormat number format
     * @return formatted string if it is a number or input string if it has text, empty string otherwise
     * @throws ParseException number in string could not be resolved
     */
    public static String applyDecimalFormat(
        String text,
        Style style,
        DecimalFormat decimalFormat
    ) throws ParseException {
        if (!StringUtils.hasText(text)) {
            return "";
        }
        final TextStyle textStyle = StyleUtils.extractTextStyle(style);
        if (isNumber(text)) {
            final DecimalFormat format = (decimalFormat != null)
                ? decimalFormat
                : textStyle != null
                    ? textStyle.getDecimalFormat()
                    : null;
            final Locale locale = (textStyle != null)
                ? textStyle.getFontLocale()
                : null;
            return localizeNumber(text, format, locale);
        }
        return text;
    }

    public static Boolean isNumber(String text) {
        return Pattern.matches(floatRegex, text);
    }

    /**
     * @param number string representation of a number
     * @param format {@link DecimalFormat} number format
     * @return - formatted text
     * @throws ParseException number in string could not be resolved
     */
    public static String localizeNumber(String number, DecimalFormat format, Locale locale) throws ParseException {
        if (format != null) {
            return format.format(format.parse(number).floatValue());
        } else {
            if (locale == null) {
                return number;
            }
            final DecimalFormat localFormat = (DecimalFormat) NumberFormat.getInstance(locale);
            localFormat.setRoundingMode(RoundingMode.DOWN);
            localFormat.setMinimumFractionDigits(DEFAULT_DECIMAL_FRACTION_DIGITS);
            localFormat.setMaximumFractionDigits(DEFAULT_DECIMAL_FRACTION_DIGITS);
            final DecimalFormatSymbols symbols = localFormat.getDecimalFormatSymbols();
            symbols.setDecimalSeparator(DEFAULT_DECIMAL_SEPARATOR);
            localFormat.setDecimalFormatSymbols(symbols);
            return localFormat.format(localFormat.parse(number).floatValue());
        }
    }
}
