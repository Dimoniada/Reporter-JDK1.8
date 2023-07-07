package com.model.domain.styles;

import com.google.common.base.MoreObjects;
import com.model.domain.DocumentItem;

/**
 * Style class of {@link DocumentItem},
 * contains a condition for the style to be applied to the DocumentItem object
 */

public class Style implements Cloneable {
    /**
     * Style applicability condition
     */
    protected StyleCondition condition;

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("condition", condition)
                .toString();
    }

    /**
     * Removes the condition from the style instance
     */
    @SuppressWarnings("unchecked")
    public <T extends Style> T removeCondition() {
        this.condition = null;
        return (T) this;
    }

    public StyleCondition getCondition() {
        return condition;
    }

    @SuppressWarnings("unchecked")
    public <T extends Style> T setCondition(StyleCondition condition) {
        this.condition = condition;
        return (T) this;
    }

    public Style clone() throws CloneNotSupportedException {
        return ((Style) super.clone()).setCondition(condition);
    }
}
