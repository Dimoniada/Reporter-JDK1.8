package com.model.domain.styles;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.model.domain.styles.constants.Color;
import com.model.domain.styles.constants.FillPattern;
import com.model.domain.styles.constants.HorAlignment;
import com.model.domain.styles.constants.VertAlignment;
import com.model.domain.styles.geometry.GeometryDetails;

/**
 * The style is used to set:
 * the location of the content in the element,
 * element width, border, color and its fill type and nearby environment
 */
public class LayoutStyle extends Style {
    /**
     * Measurable values of a content
     */
    protected GeometryDetails geometryDetails;
    /**
     * Content auto alignment
     */
    protected Boolean autoWidth;
    /**
     * Stretch content to fit width
     */
    protected Boolean shrinkToFit;
    /**
     * Type of top line limiting content
     */
    protected BorderStyle borderTop;
    /**
     * Type of left line limiting content
     */
    protected BorderStyle borderLeft;
    /**
     * Type of right line limiting content
     */
    protected BorderStyle borderRight;
    /**
     * Type of bottom line limiting content
     */
    protected BorderStyle borderBottom;
    /**
     * Back color of all content
     */
    protected Color fillBackgroundColor;
    /**
     * Rear content color
     */
    protected Color fillForegroundColor;
    /**
     * The way to fill the back color of the content
     */
    protected FillPattern fillPattern;
    /**
     * Content horizontal alignment type
     */
    protected HorAlignment horAlignment;
    /**
     * Content vertical alignment type
     */
    protected VertAlignment vertAlignment;

    public static LayoutStyle create() {
        return new LayoutStyle();
    }

    @Override
    public LayoutStyle clone() throws CloneNotSupportedException {
        return
            ((LayoutStyle) super
                .clone())
                .setBorderTop(borderTop != null ? borderTop.clone() : null)
                .setBorderLeft(borderLeft != null ? borderLeft.clone() : null)
                .setBorderRight(borderRight != null ? borderRight.clone() : null)
                .setBorderBottom(borderBottom != null ? borderBottom.clone() : null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final LayoutStyle that = (LayoutStyle) o;

        return
            Objects.equal(this.geometryDetails, that.geometryDetails)
                && Objects.equal(this.autoWidth, that.autoWidth)
                && Objects.equal(this.shrinkToFit, that.shrinkToFit)
                && Objects.equal(this.borderTop, that.borderTop)
                && Objects.equal(this.borderLeft, that.borderLeft)
                && Objects.equal(this.borderRight, that.borderRight)
                && Objects.equal(this.borderBottom, that.borderBottom)
                && Objects.equal(this.fillBackgroundColor, that.fillBackgroundColor)
                && Objects.equal(this.fillForegroundColor, that.fillForegroundColor)
                && Objects.equal(this.fillPattern, that.fillPattern)
                && Objects.equal(this.horAlignment, that.horAlignment)
                && Objects.equal(this.vertAlignment, that.vertAlignment);
    }

    @Override
    public int hashCode() {
        return
            Objects
                .hashCode(
                    geometryDetails,
                    autoWidth,
                    shrinkToFit,
                    borderTop,
                    borderLeft,
                    borderRight,
                    borderBottom,
                    fillBackgroundColor,
                    fillForegroundColor,
                    fillPattern,
                    horAlignment,
                    vertAlignment
                );
    }

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("geometryDetails", getGeometryDetails())
                .add("autoWidth", isAutoWidth())
                .add("shrinkToFit", isShrinkToFit())
                .add("borderTop", getBorderTop())
                .add("borderLeft", getBorderLeft())
                .add("borderRight", getBorderRight())
                .add("borderBottom", getBorderBottom())
                .add("fillBackgroundColor", getFillBackgroundColor())
                .add("fillForegroundColor", getFillForegroundColor())
                .add("fillPattern", getFillPattern())
                .add("horAlignment", getHorAlignment())
                .add("vertAlignment", getVertAlignment())
                .add("parent", super.toString())
                .toString();
    }

    public GeometryDetails getGeometryDetails() {
        return geometryDetails;
    }

    public LayoutStyle setGeometryDetails(GeometryDetails geometryDetails) {
        this.geometryDetails = geometryDetails;
        return this;
    }

    public Boolean isAutoWidth() {
        return autoWidth;
    }

    public LayoutStyle setAutoWidth(Boolean autoWidth) {
        this.autoWidth = autoWidth;
        return this;
    }

    public Boolean isShrinkToFit() {
        return shrinkToFit;
    }

    public LayoutStyle setShrinkToFit(Boolean shrinkToFit) {
        this.shrinkToFit = shrinkToFit;
        return this;
    }

    public BorderStyle getBorderTop() {
        return borderTop;
    }

    public LayoutStyle setBorderTop(BorderStyle borderTop) {
        this.borderTop = borderTop;
        return this;
    }

    public BorderStyle getBorderLeft() {
        return borderLeft;
    }

    public LayoutStyle setBorderLeft(BorderStyle borderLeft) {
        this.borderLeft = borderLeft;
        return this;
    }

    public BorderStyle getBorderRight() {
        return borderRight;
    }

    public LayoutStyle setBorderRight(BorderStyle borderRight) {
        this.borderRight = borderRight;
        return this;
    }

    public BorderStyle getBorderBottom() {
        return borderBottom;
    }

    public LayoutStyle setBorderBottom(BorderStyle borderBottom) {
        this.borderBottom = borderBottom;
        return this;
    }

    public Color getFillBackgroundColor() {
        return fillBackgroundColor;
    }

    public LayoutStyle setFillBackgroundColor(Color fillBackgroundColor) {
        this.fillBackgroundColor = fillBackgroundColor;
        return this;
    }

    public Color getFillForegroundColor() {
        return fillForegroundColor;
    }

    public LayoutStyle setFillForegroundColor(Color fillForegroundColor) {
        this.fillForegroundColor = fillForegroundColor;
        return this;
    }

    public FillPattern getFillPattern() {
        return fillPattern;
    }

    public LayoutStyle setFillPattern(FillPattern fillPattern) {
        this.fillPattern = fillPattern;
        return this;
    }

    public HorAlignment getHorAlignment() {
        return horAlignment;
    }

    public LayoutStyle setHorAlignment(HorAlignment horAlignment) {
        this.horAlignment = horAlignment;
        return this;
    }

    public VertAlignment getVertAlignment() {
        return vertAlignment;
    }

    public LayoutStyle setVertAlignment(VertAlignment vertAlignment) {
        this.vertAlignment = vertAlignment;
        return this;
    }
}
