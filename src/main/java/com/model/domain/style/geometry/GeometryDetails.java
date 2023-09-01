package com.model.domain.style.geometry;

import com.google.common.base.MoreObjects;
import com.model.domain.style.constant.HorAlignment;
import com.model.domain.style.constant.VertAlignment;

import java.util.Map;

/**
 * Represents measurable details of an element
 */
public class GeometryDetails {
    /**
     * Element width, default is null when not provided
     */
    protected Geometry<Object> width;
    /**
     * Element height, default is null when not provided
     */
    protected Geometry<Object> height;
    /**
     * Scale X of element, default is null when not provided
     */
    protected Geometry<Object> scaleX;
    /**
     * Scale Y of element, default is null when not provided
     */
    protected Geometry<Object> scaleY;
    /**
     * Element angle, default is null when not provided
     */
    protected Geometry<Object> angle;
    /**
     * Scaling and rotation center for element, default is null when not provided
     * Geometry here is a Map
     *      key = extension (String)
     *      value = center position, relative itself (AbstractMap.SimpleEntry(HorAlignment, VertAlignment))
     * See
     *      {@link com.model.domain.style.constant.HorAlignment},
     *      {@link com.model.domain.style.constant.VertAlignment}
     */
    protected Geometry<Map.Entry<HorAlignment, VertAlignment>> transformCenter;

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

    public Geometry<Object> getWidth() {
        return width;
    }

    public GeometryDetails setWidth(Geometry<Object> width) {
        this.width = width;
        return this;
    }

    public Geometry<Object> getHeight() {
        return height;
    }

    public GeometryDetails setHeight(Geometry<Object> height) {
        this.height = height;
        return this;
    }

    public Geometry<Object> getScaleX() {
        return scaleX;
    }

    public GeometryDetails setScaleX(Geometry<Object> scaleX) {
        this.scaleX = scaleX;
        return this;
    }

    public Geometry<Object> getScaleY() {
        return scaleY;
    }

    public GeometryDetails setScaleY(Geometry<Object> scaleY) {
        this.scaleY = scaleY;
        return this;
    }

    public Geometry<Object> getAngle() {
        return angle;
    }

    public GeometryDetails setAngle(Geometry<Object> angle) {
        this.angle = angle;
        return this;
    }

    public Geometry<Map.Entry<HorAlignment, VertAlignment>> getTransformCenter() {
        return transformCenter;
    }

    public GeometryDetails setTransformCenter(Geometry<Map.Entry<HorAlignment, VertAlignment>> transformCenter) {
        this.transformCenter = transformCenter;
        return this;
    }
}
