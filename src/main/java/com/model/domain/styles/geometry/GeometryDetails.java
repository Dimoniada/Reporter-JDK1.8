package com.model.domain.styles.geometry;

import com.google.common.base.MoreObjects;

/**
 * Represents measurable details of an element
 */
public class GeometryDetails {
    /**
     * Element width, default is null when not provided
     */
    protected Geometry width;
    /**
     * Element height, default is null when not provided
     */
    protected Geometry height;
    /**
     * Scale X of element, default is null when not provided
     */
    protected Geometry scaleX;
    /**
     * Scale Y of element, default is null when not provided
     */
    protected Geometry scaleY;
    /**
     * Element angle, default is null when not provided
     */
    protected Geometry angle;
    /**
     * Scaling and rotation center for element, default is null when not provided
     * Geometry here is a Map
     *      key = extension (String)
     *      value = center position, relative itself (AbstractMap.SimpleEntry(HorAlignment, VertAlignment))
     * See
     *      {@link com.model.domain.styles.constants.HorAlignment},
     *      {@link com.model.domain.styles.constants.VertAlignment}
     */
    protected Geometry transformCenter;

    public static GeometryDetails create() {
        return new GeometryDetails();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("width", width)
            .add("height", height)
            .add("scaleX", scaleX)
            .add("scaleY", scaleY)
            .add("angle", angle)
            .add("transformCenter", transformCenter)
            .toString();
    }

    public Geometry getWidth() {
        return width;
    }

    public GeometryDetails setWidth(Geometry width) {
        this.width = width;
        return this;
    }

    public Geometry getHeight() {
        return height;
    }

    public GeometryDetails setHeight(Geometry height) {
        this.height = height;
        return this;
    }

    public Geometry getScaleX() {
        return scaleX;
    }

    public GeometryDetails setScaleX(Geometry scaleX) {
        this.scaleX = scaleX;
        return this;
    }

    public Geometry getScaleY() {
        return scaleY;
    }

    public GeometryDetails setScaleY(Geometry scaleY) {
        this.scaleY = scaleY;
        return this;
    }

    public Geometry getAngle() {
        return angle;
    }

    public GeometryDetails setAngle(Geometry angle) {
        this.angle = angle;
        return this;
    }

    public Geometry getTransformCenter() {
        return transformCenter;
    }

    /**
     *
     * @param transformCenter is a Map
     *            key = extension (String),
     *            value = center is a  AbstractMap.SimpleEntry with
     *                      key = {@link com.model.domain.styles.constants.HorAlignment},
     *                      value = {@link com.model.domain.styles.constants.VertAlignment}
     * @return GeometryDetails
     */
    public GeometryDetails setTransformCenter(Geometry transformCenter) {
        this.transformCenter = transformCenter;
        return this;
    }
}
