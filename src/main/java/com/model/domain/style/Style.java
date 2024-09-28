package com.model.domain.style;

import com.google.common.base.MoreObjects;
import com.model.domain.core.DocumentItem;

/**
 * Style class of {@link DocumentItem},
 * contains a condition for the style to be applied to the DocumentItem object
 */
public class Style implements Cloneable {
    /**
     * Style applicability condition
     */
    protected StyleCondition styleCondition;

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("styleCondition", styleCondition)
                .toString();
    }

    /**
     * Removes the condition from the style instance
     */
    @SuppressWarnings("unchecked")
    public <T extends Style> T removeCondition() {
        this.styleCondition = null;
        return (T) this;
    }

    public StyleCondition getStyleCondition() {
        return styleCondition;
    }

    @SuppressWarnings("unchecked")
    public <T extends Style> T setStyleCondition(StyleCondition styleCondition) {
        this.styleCondition = styleCondition;
        return (T) this;
    }

    public Style clone() throws CloneNotSupportedException {
        return ((Style) super.clone()).setStyleCondition(styleCondition);
    }
}
