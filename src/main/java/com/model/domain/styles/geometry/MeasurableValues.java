package com.model.domain.styles.geometry;

import com.google.common.base.MoreObjects;

/**
 * Represents measurable values of an element
 */
public class MeasurableValues {
    /**
     * Element width, null when not applicable
     */
    protected Geometry width = null;
    /**
     * Element height, null when not applicable
     */
    protected Geometry height = null;
    /**
     * Element angle, null when not applicable
     */
    protected Geometry angle = null;

    public static MeasurableValues create(Geometry height, Geometry width, Geometry angle) {
        return
            new MeasurableValues()
                .setWidth(width)
                .setHeight(height)
                .setAngle(angle);
    }

    public static MeasurableValues create(Geometry height, Geometry width) {
        return MeasurableValues.create(height, width, null);
    }

    public static MeasurableValues create(Geometry height) {
        return MeasurableValues.create(height, null, null);
    }

    public static MeasurableValues create() {
        return MeasurableValues.create(null, null, null);
    }

    public Geometry getWidth() {
        return width;
    }

    public MeasurableValues setWidth(Geometry width) {
        this.width = width;
        return this;
    }

    public Geometry getHeight() {
        return height;
    }

    public MeasurableValues setHeight(Geometry height) {
        this.height = height;
        return this;
    }

    public Geometry getAngle() {
        return angle;
    }

    public MeasurableValues setAngle(Geometry angle) {
        this.angle = angle;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("width", width)
            .add("height", height)
            .add("angle", angle)
            .toString();
    }
}
