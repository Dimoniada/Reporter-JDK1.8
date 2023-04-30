package com.model.domain.styles.geometry;

import com.google.common.base.MoreObjects;

/**
 * Represents measurable values of an element
 */
public class MeasurableValues {
    /**
     * Element width, null when not provided
     */
    protected Geometry width;
    /**
     * Element height, null when not provided
     */
    protected Geometry height;
    /**
     * Element angle, null when not provided
     */
    protected Geometry angle;

    public static MeasurableValues create(Geometry width, Geometry height, Geometry angle) {
        return
            new MeasurableValues()
                .setWidth(width)
                .setHeight(height)
                .setAngle(angle);
    }

    public static MeasurableValues create(Geometry width, Geometry height) {
        return MeasurableValues.create(width, height, null);
    }

    public static MeasurableValues create(Geometry width) {
        return MeasurableValues.create(width, null, null);
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
