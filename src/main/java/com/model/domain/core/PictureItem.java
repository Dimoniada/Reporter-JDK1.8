package com.model.domain.core;

import com.google.common.base.MoreObjects;

public class PictureItem extends DataItem {
    /**
     * Picture raw data
     */
    protected byte[] data;

    @Override
    public String getText() {
        return null;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DataItem> T setData(byte[] data) {
        this.data = data;
        return (T) this;
    }

    @Override
    public boolean isDataInheritedFrom(Class<?> type) {
        return type.isAssignableFrom(getClass());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("data", data)
            .add("super", super.toString())
            .toString();
    }
}
