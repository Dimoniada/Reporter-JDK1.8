package com.model.domain.styles.geometry;

import com.google.common.base.MoreObjects;

/**
 * Represents measurable details of an element
 */
public class SpecificDetails {
    /**
     * Element width, default is null when not provided
     */
    protected Geometry width;
    /**
     * Element height, default is null when not provided
     */
    protected Geometry height;
    /**
     * Element angle, default is null when not provided
     */
    protected Geometry angle;

    public static SpecificDetails create(Geometry width, Geometry height, Geometry angle) {
        return
            new SpecificDetails()
                .setWidth(width)
                .setHeight(height)
                .setAngle(angle);
    }

    public static SpecificDetails create(Geometry width, Geometry height) {
        return SpecificDetails.create(width, height, null);
    }

    public static SpecificDetails create(Geometry width) {
        return SpecificDetails.create(width, null, null);
    }

    public static SpecificDetails create() {
        return SpecificDetails.create(null, null, null);
    }

    public Geometry getWidth() {
        return width;
    }

    public SpecificDetails setWidth(Geometry width) {
        this.width = width;
        return this;
    }

    public Geometry getHeight() {
        return height;
    }

    public SpecificDetails setHeight(Geometry height) {
        this.height = height;
        return this;
    }

    public Geometry getAngle() {
        return angle;
    }

    public SpecificDetails setAngle(Geometry angle) {
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
