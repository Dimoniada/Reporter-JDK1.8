package com.model.utils;

public abstract class ConverterUtils {
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object value) {
        if (value != null) {
            return (T) value;
        }
        return null;
    }
}
