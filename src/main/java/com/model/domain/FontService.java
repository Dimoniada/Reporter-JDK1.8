package com.model.domain;

import com.google.common.base.MoreObjects;
import com.itextpdf.kernel.font.PdfFont;
import com.model.domain.style.FontFamilyStyle;
import com.model.domain.style.TextStyle;
import com.model.utils.StringMetricUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class contains basic methods for getting a font resource according to the specified parameters.
 * Checks the font for the ability to display the characters of the given locales.
 * Returns suitable fonts from stored resources.
 */

public class FontService {
    private static final Logger log = LoggerFactory.getLogger(FontService.class);
    private static final String FONTS_ALPHABETS = "alphabets";
    private static final String FONTS_ALPHABETS_EXT = ".properties";
    private static final String FONTS_LOCALE_REGEXP = "(.+)(?=(=))";

    private static final String FONTS_LOCATION = "free_fonts/";
    private static final String FONTS_EXT = ".ttf";
    private static final ClassLoader loader = FontService.class.getClassLoader();
    private static final Map<FontFamilyStyle, String> fontFamilyPdfMap = new HashMap<FontFamilyStyle, String>() {{
        put(FontFamilyStyle.SERIF, "Serif");
        put(FontFamilyStyle.SANS_SERIF, "SansSerif");
        put(FontFamilyStyle.MONOSPACED, "Monospaced");
    }};

    protected final Map<String, Map.Entry<Font, Map<TextAttribute, Object>>> fonts =
        new HashMap<String, Map.Entry<Font, Map<TextAttribute, Object>>>() {{
            put("arial_SansSerif_(en-ru Arimo).ttf", null);
            put("courierNew_Monospaced_(en-ru AnonymousPro-Regular).ttf", null);
            put("helvetica_SansSerif_(en-ru OpenSans).ttf", null);
            put("tahoma_SansSerif_(en-ru PTSans).ttf", null);
            put("times_Serif_(en-ru Tinos).ttf", null);
            put("verdana_SansSerif_(en-ru Rubik).ttf", null);
        }};

    /**
     * Language locales supported by the font
     */
    protected Set<Locale> localeSet = new HashSet<>();

    public FontService() { /**/ }

    public static FontService create() {
        return new FontService();
    }

    public static String toPdfFontFamily(FontFamilyStyle fontFamilyStyle) {
        return fontFamilyPdfMap.getOrDefault(fontFamilyStyle, "Serif");
    }

    public String alphabet(String locale) {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(FONTS_LOCATION + FONTS_ALPHABETS);
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource.getMessage(locale, null, Locale.forLanguageTag(locale));
    }

    /**
     * Reads available locales from resource file
     *
     * @throws IllegalArgumentException if it can't open resource file
     */
    public void initializeLocales() throws IllegalArgumentException {
        final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(loader);
        final String alphabetProps = FONTS_LOCATION + FONTS_ALPHABETS + FONTS_ALPHABETS_EXT;
        final Resource resource = resolver.getResource(alphabetProps);
        try (InputStream is = resource.getInputStream()) {
            final String text = IOUtils.toString(is, StandardCharsets.UTF_8);
            final Pattern pattern = Pattern.compile(FONTS_LOCALE_REGEXP);
            final Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                localeSet.add(Locale.forLanguageTag(matcher.group()));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Can't read resource %s", alphabetProps), e);
        }
    }

    /**
     * Populates from internal resources a map {@link FontService#fonts}(font_file, its_attributes)
     * P.S.: since the TextAttribute.FAMILY property of the font file is the name of the font,
     * not the name of the font class as it should be (see.{@link TextAttribute#FAMILY}),
     * therefore the name of the font class is added to the .ttf file names of the resource
     * imprisoned "_", which is stored in TextAttribute.FAMILY
     *
     * @return FontService
     * @throws IOException              the font resource could not be read
     * @throws FontFormatException      invalid font entry format
     * @throws IllegalArgumentException the font resource is not {@code TRUETYPE_FONT} or {@code TYPE1_FONT}.
     */
    @SuppressWarnings("unchecked")
    public FontService initializeFonts() throws FontFormatException, IOException, IllegalArgumentException {
        initializeLocales();
        final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(loader);
        final Resource[] resources =
            resolver.getResources(FONTS_LOCATION + "*" + FONTS_EXT);
        for (final Resource fontRes : resources) {
            try (InputStream fontStream = fontRes.getInputStream()) {
                final Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
                final Map<TextAttribute, Object> attributeObjectMap = (Map<TextAttribute, Object>) font.getAttributes();
                final String fontName = fontRes.getFilename();
                if (!StringUtils.hasText(fontName)) {
                    continue;
                }
                final String[] attrs = fontName.split("_");

                if (attrs.length < 2) {
                    throw new IllegalStateException("Font name should contain font-family name imprisoned \"_\"");
                } else {
                    attributeObjectMap.replace(TextAttribute.FAMILY, attrs[1]);
                }

                fonts.put(fontName, new AbstractMap.SimpleEntry<>(font, attributeObjectMap));
            }
        }
        return this;
    }

    public boolean checkAvailableFontsLocale(Locale locale) {
        final String tag = locale.toLanguageTag();
        return !alphabet(tag).equals(tag);
    }

    /**
     * Returns one of embedded *.ttf fonts as a java.awt.Font, see {@link free_fonts} folder
     * <p>
     * Font styles (like Bold/Italic/Underline) can be obtained using software methods
     * (for instance, see {@link com.itextpdf.layout.element.AbstractElement#setBold or setItalic or setUnderline})
     * or directly from a stylized font-file.
     * <p>
     * If a stylized font-file is used, then you must specify the font-file name in
     * TextStyle#fontNameResource and set {@link TextStyle.useTtfFontAttributes} to true
     * and must not set the corresponding parameter(s) in {@link TextStyle},
     * they will be taken from font-file automatically
     *
     * @param textStyle The text style of the element for which the matching font is being searched
     * @param locale    provided to check: font glyphs can be displayed, and they can cover alphabet of locale
     * @return {@link PdfFont}
     * @throws FontFormatException invalid font entry format
     * @throws IOException         error reading font resource
     */
    @SuppressWarnings("unchecked")
    public Font getFontResource(TextStyle textStyle, Locale locale) throws IOException, FontFormatException {
//        log.info("Calling getFont() for present locale {}", locale);
        final Boolean useTtfFontAttributes = textStyle.isUseTtfFontAttributes();
        final Optional<Map.Entry<String, Map.Entry<Font, Map<TextAttribute, Object>>>> fontFileWithAttributes =
            fonts
                .entrySet()
                .stream()
                .filter(entry -> {
                    boolean fit = true;
                    final Map<TextAttribute, Object> attr = entry.getValue().getValue();
                    if (useTtfFontAttributes != null && useTtfFontAttributes) {
                        fit = checkFitBold(textStyle, attr)
                            && checkFitItalic(textStyle, attr)
                            && checkFitUnderline(textStyle, attr);
                    }
                    return checkFitFamily(textStyle, attr) && fit;
                })
                .reduce(
                    (entry1, entry2) -> {
                        final String fontName = textStyle.getFontNameResource();
                        final int distanceKey1 = StringMetricUtils
                            .levenshteinDistance(entry1.getKey(), fontName);
                        final int distanceKey2 = StringMetricUtils
                            .levenshteinDistance(entry2.getKey(), fontName);
                        return StringUtils.hasText(fontName)
                            && distanceKey1 < distanceKey2
                            ? entry1
                            : entry2;
                    }
                );
        log.debug("Selected font with attributes {}", fontFileWithAttributes);
        if (fontFileWithAttributes.isPresent()) {
            final String fontName = fontFileWithAttributes.get().getKey();
            final String fontPathName = FONTS_LOCATION + fontName;
            try (InputStream fontStream = loader.getResourceAsStream(fontPathName)) {
                if (fontStream == null) {
                    throw new IllegalArgumentException(
                        String.format("Font file \"%s\" can't be opened", fontPathName)
                    );
                }
                checkCanDisplayFont(fontName, locale);
                if (useTtfFontAttributes != null && useTtfFontAttributes) {
                    return Font.createFont(Font.TRUETYPE_FONT, fontStream);
                }
                final Map<TextAttribute, Object> attr = new HashMap<TextAttribute, Object>() {{
                    put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);
                    put(TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR);
                    put(TextAttribute.UNDERLINE, -1);
                    put(TextAttribute.SIZE, 12);
                    put(TextAttribute.FOREGROUND, Color.BLACK);
                }};
                if (Boolean.TRUE.equals(textStyle.isBold())) {
                    attr.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
                }
                if (Boolean.TRUE.equals(textStyle.isItalic())) {
                    attr.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
                }
                if (textStyle.getUnderline() != null && textStyle.getUnderline() != 0) {
                    attr.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                }
                if (textStyle.getFontSize() != null) {
                    attr.put(TextAttribute.SIZE, textStyle.getFontSize());
                }
                if (textStyle.getColor() != null) {
                    attr.put(
                        TextAttribute.FOREGROUND,
                        Color.decode("0x" + textStyle.getColor().buildColorString())
                    );
                }
                return Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(attr);
            }
        }
        throw new IllegalArgumentException(
            String.format("Can't find valid font with attributes: %s for locale %s", textStyle, locale)
        );
    }

    /**
     * Returns one of embedded *.ttf fonts as a java.awt.Font, see {@link free_fonts} folder
     * based on TextStyle
     **/
    public static Font getFontResourceByTextStyle(TextStyle textStyle) {
        final Locale fontLocale = textStyle.getFontLocale();
        try {
            return FontService
                .create()
                .initializeFonts()
                .getFontResource(textStyle, fontLocale);
        } catch (FontFormatException e) {
            throw new IllegalStateException(
                "While rendering text as a picture: " +
                    "the fontStream data does not contain the required font tables for the specified format",
                e
            );
        } catch (IOException e) {
            throw new IllegalStateException(
                "While rendering text as a picture: " +
                    "the fontStream cannot be completely read",
                e
            );
        }
    }

    private void checkCanDisplayFont(String fontName, Locale locale) {
        if (locale == null) {
            return;
        }
        if (!fonts.containsKey(fontName)) {
            throw new IllegalArgumentException(
                String.format("Can't find font %s in resources", fontName)
            );
        }
        final String charsString = alphabet(locale.toLanguageTag());
        final int undisplayedCharAt = fonts.get(fontName).getKey().canDisplayUpTo(charsString);
        if (undisplayedCharAt != -1) {
            throw new IllegalArgumentException(
                String.format(
                    "Can't display glyph %s with font %s for locale %s",
                    charsString.charAt(undisplayedCharAt),
                    fontName,
                    locale.toLanguageTag()
                )
            );
        }
    }

    /**
     * Checks if the text style belongs to the font family specified in attr
     *
     * @param textStyle text style
     * @param attr      font attributes
     * @return true if the families match or no family is specified in the file attributes
     */
    private boolean checkFitFamily(TextStyle textStyle, Map<TextAttribute, Object> attr) {
        if (attr.containsKey(TextAttribute.FAMILY)) {
            return toPdfFontFamily(textStyle.getFontFamilyStyle())
                .equals(attr.get(TextAttribute.FAMILY));
        }
        return true;
    }

    /**
     * Checks the Bold property of text style against the attribute specified in attr
     *
     * @param textStyle text style
     * @param attr      font attributes
     * @return true if values match or no property is specified in file attributes
     */
    private boolean checkFitBold(TextStyle textStyle, Map<TextAttribute, Object> attr) {
        if (attr.containsKey(TextAttribute.WEIGHT)) {
            final Object weight = attr.get(TextAttribute.WEIGHT);
            if (weight != null) {
                final boolean fitBold = textStyle.isBold() && weight.equals(TextAttribute.WEIGHT_BOLD);
                final boolean fitRegular = !textStyle.isBold() && weight.equals(TextAttribute.WEIGHT_REGULAR);
                return fitBold || fitRegular;
            }
        }
        return true;
    }

    /**
     * Checks the Italic property of text style against the attribute specified in attr
     *
     * @param textStyle text style
     * @param attr      font attributes
     * @return true if values match or no property is specified in file attributes
     */
    private boolean checkFitItalic(TextStyle textStyle, Map<TextAttribute, Object> attr) {
        if (attr.containsKey(TextAttribute.POSTURE)) {
            final Object italic = attr.get(TextAttribute.POSTURE);
            if (italic != null) {
                final boolean fitItalic = textStyle.isItalic() && italic.equals(TextAttribute.POSTURE_OBLIQUE);
                final boolean fitRegular = !textStyle.isItalic() && italic.equals(TextAttribute.POSTURE_REGULAR);
                return fitItalic || fitRegular;
            }
        }
        return true;
    }

    /**
     * Checks the Underline property of the text style against the attribute specified in attr
     *
     * @param textStyle text style
     * @param attr      font attributes
     * @return true if values match or no property is specified in file attributes
     */
    private boolean checkFitUnderline(TextStyle textStyle, Map<TextAttribute, Object> attr) {
        if (attr.containsKey(TextAttribute.UNDERLINE)) {
            final Object underlined = attr.get(TextAttribute.UNDERLINE);
            if (underlined != null) {
                final boolean fitUnderline =
                    textStyle.getUnderline() != 0 && underlined.equals(TextAttribute.UNDERLINE_ON);
                final boolean fitRegular = textStyle.getUnderline() == 0 && underlined.equals(-1);
                return fitUnderline || fitRegular;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("fonts", fonts)
            .add("localeSet", localeSet)
            .toString();
    }

    public Map<String, Map.Entry<Font, Map<TextAttribute, Object>>> getFonts() {
        return fonts;
    }

    public Set<Locale> getLocaleSet() {
        return localeSet;
    }

    public FontService setLocaleSet(Set<Locale> localeSet) {
        this.localeSet = localeSet;
        return this;
    }
}
