package com.model.domain.styles.geometry;

import com.google.common.base.MoreObjects;

/**
 * Represent dimensions of element
 */
public class Dimensions {
    /**
     * Element width
     */
    protected Geometry width = null;
    /**
     * Element height
     */
    protected Geometry height = null;
    /**
     * Element rotation
     */
    protected Geometry rotation = null;

    public static Dimensions create(Geometry height, Geometry width, Geometry rotation) {
        return
            new Dimensions()
                .setWidth(width)
                .setHeight(height)
                .setRotation(rotation);
    }

    public static Dimensions create(Geometry height, Geometry width) {
        return Dimensions.create(height, width, null);
    }

    public static Dimensions create(Geometry height) {
        return Dimensions.create(height, null, null);
    }

    public static Dimensions create() {
        return Dimensions.create(null, null, null);
    }

    public Geometry getWidth() {
        return width;
    }

    public Dimensions setWidth(Geometry width) {
        this.width = width;
        return this;
    }

    public Geometry getHeight() {
        return height;
    }

    public Dimensions setHeight(Geometry height) {
        this.height = height;
        return this;
    }

    public Geometry getRotation() {
        return rotation;
    }

    public Dimensions setRotation(Geometry rotation) {
        this.rotation = rotation;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("width", width)
            .add("height", height)
            .add("rotation", rotation)
            .toString();
    }
}
