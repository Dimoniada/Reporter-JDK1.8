package com.reporter.domain;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.model.domain.FontService;
import com.model.domain.styles.FontFamilyStyle;
import com.model.domain.styles.TextStyle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

class FontServiceTest {
    public final Locale localeEn = Locale.ENGLISH;
    public final Locale localeZh = Locale.forLanguageTag("zh");
    public final Locale localeRu = Locale.forLanguageTag("ru");
    public FontService fontService;

    @BeforeEach
    public void initFontService() throws IOException, FontFormatException {
        fontService = FontService.create().initializeFonts();
    }

    @Test
    void testCreateFontService() {
        final Set<Locale> localeList = fontService.getLocaleSet();
        Assertions.assertTrue(
            localeList.contains(localeEn)
                && localeList.contains(localeZh)
                && localeList.contains(localeRu)
        );
    }

    @Test
    void testToPdfFontFamily() {
        Assertions.assertEquals("SansSerif", FontService.toPdfFontFamily(FontFamilyStyle.SANS_SERIF));
        Assertions.assertEquals("Serif", FontService.toPdfFontFamily(FontFamilyStyle.SERIF));
        Assertions.assertEquals("Monospaced", FontService.toPdfFontFamily(FontFamilyStyle.MONOSPACED));
    }

    @Test
    void testInitializeFonts() {
        Assertions.assertDoesNotThrow(
            () -> fontService.initializeFonts()
        );
        Assertions.assertTrue(
            fontService
                .getFonts()
                .values()
                .stream()
                .anyMatch(Objects::nonNull)
        );
    }

    @Test
    void testCheckAvailableFontsLocale() {
        Assertions.assertTrue(
            fontService.checkAvailableFontsLocale(Locale.ENGLISH)
        );
        Assertions.assertTrue(
            fontService.checkAvailableFontsLocale(Locale.forLanguageTag("zh"))
        );
        Assertions.assertTrue(
            fontService.checkAvailableFontsLocale(Locale.forLanguageTag("ru"))
        );
    }

    @Test
    void testGetFontResource() {
        final TextStyle testStyle = TextStyle.create("helvetica_SansSerif_(en-ru OpenSans).ttf");
        final AtomicReference<Font> fontRef = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> {
            fontService.initializeFonts();
            fontRef.set(fontService.getFontResource(testStyle, Locale.forLanguageTag("en")));
        });

        Assertions.assertDoesNotThrow(() -> {
                final Method method = Font.class.getDeclaredMethod("getFont2D");
                method.setAccessible(true);
                String rawFont = method.invoke(fontRef.get()).toString();
                rawFont = rawFont
                    .substring(rawFont.indexOf(" fileName=") + 10)
                    .replace("\r\n", "\n") + "\n";
                rawFont = rawFont.substring(0, rawFont.indexOf("\n"));
                final byte[] fontResource = Files.readAllBytes(Paths.get(rawFont));
                PdfFontFactory.createFont(
                    fontResource,
                    PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
                );
            }
        );
    }
}