package com.model.domain.styles;

import com.google.common.base.MoreObjects;
import com.model.domain.Document;
import com.model.domain.DocumentItem;
import com.model.domain.FontService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * The class stores styles.
 * <p>
 * By default, registered styles are added to head of html-documents,
 * contra those are given in the {@link Document} structure -
 * they will be added into the tag via style="...".
 */

public abstract class StyleService implements StyleApplier {
    private static final Logger log = LoggerFactory.getLogger(StyleService.class);
    /**
     * List of registered styles
     */
    protected final List<Style> styles = new ArrayList<>();
    /**
     * Number representation format
     */
    protected DecimalFormat decimalFormat;

    protected FontService fontService;

    /**
     * Returns first matching style from List of Styles for the item object,
     * checking style's {@link StyleCondition}.
     * A style with {@link StyleCondition} equals null is returned as appropriate.
     *
     * @param o the object on which to test the conditions of styles.
     * @return first style with matched condition
     */
    public Optional<Style> extractStyleFor(Object o) {
        return styles
            .stream()
            .filter(s -> {
                if (s.getCondition() != null) {
                    final boolean passCondition = s.getCondition().test(o);
                    final Class<?> itemClass = o.getClass();
                    final Class<?> conditionClass = s.getCondition().getClazz();
                    return passCondition && itemClass.isAssignableFrom(conditionClass);
                }
                return true;
            })
            .findFirst();
    }

    /**
     * Prepares (if any) style from item and StyleService's style
     *
     * @param o is a donor of the style for native element
     * @return mixed style
     * @throws Exception on joining styles
     */
    public Style prepareStyleFrom(Object o) throws Exception {
        final Optional<Style> optStyle = extractStyleFor(o);
        Style style = null;
        if (o instanceof DocumentItem) {
            style = ((DocumentItem) o).getStyle();
        }
        if (optStyle.isPresent()) {
            if (style == null) {
                style = optStyle.get();
            } else {
                StyleUtils.joinWith(optStyle.get(), style);
            }
        }
        return style;
    }

    public Boolean contains(Style style) {
        return styles.contains(style);
    }

    /**
     * Adds styles to styles
     */
    @SuppressWarnings("unchecked")
    public <T extends StyleService> T addStyles(Style... styles) {
        log.debug("styles before addStyles - {}", this.styles);
        this.styles.addAll(Arrays.asList(styles));
        log.debug("addStyles called for styles - {}", Arrays.toString(styles));
        return (T) this;
    }

    /**
     * Remove styles from styles
     */
    @SuppressWarnings("unchecked")
    public <T extends StyleService> T removeStyles(Style... styles) {
        final List<Style> sList = new ArrayList<>(Arrays.asList(styles));
        log.debug("styles before removeStyles - {}", this.styles);
        this.styles.removeIf(sList::contains);
        log.debug("removeStyles called for styles - {}", Arrays.toString(styles));
        return (T) this;
    }

    public List<Style> getStyles() {
        return styles;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("styles", styles)
            .add("decimalFormat", decimalFormat)
            .add("fontService", fontService)
            .toString();
    }

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public StyleService setDecimalFormat(DecimalFormat decimalFormat) {
        this.decimalFormat = decimalFormat;
        return this;
    }

    public FontService getFontService() {
        return fontService;
    }

    public StyleService setFontService(FontService fontService) {
        this.fontService = fontService;
        return this;
    }
}
