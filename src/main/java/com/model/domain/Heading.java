package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.domain.core.TextItem;
import com.model.formatter.FormatterVisitor;

/**
 * Document header class
 * {@link Heading#depth} - heading level
 */
public class Heading extends TextItem<Heading> {
    /**
     * header level,
     * is always greater than or equal to 0.
     * For html - no more than 6.
     */
    protected int depth;

    public Heading(int depth) {
        this.depth = depth;
    }

    public static Heading create(String text, int depth) {
        return new Heading(depth).setText(text);
    }

    public static Heading create(int depth) {
        return new Heading(depth);
    }

    @Override
    public Heading accept(FormatterVisitor visitor) throws Throwable {
        visitor.visitHeading(this);
        return this;
    }

    @Override
    public String toString() {
        final MoreObjects.ToStringHelper toStringHelper = MoreObjects.toStringHelper(this);
        return toStringHelper
            .add("text", getText())
            .add("depth", depth)
            .add("style", this.getStyle())
            .add("parentObject", this.getParentObject())
            .toString();
    }

    public int getDepth() {
        return depth;
    }

    public Heading setDepth(int depth) {
        this.depth = depth;
        return this;
    }
}
