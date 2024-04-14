package com.model.domain.style.constant;

import java.util.function.Function;

public enum PictureFormat {
    JPG("JPG", null),
    PNG("PNG", null),
    BMP("BMP", null),
    GIF("GIF", null),
    WMF("WMF", data -> checkBeginSignature(data, "D7CDC69A", 0)),
    EMF("EMF", data -> checkBeginSignature(data, "01000000", 0)),
    DIB("DIB", data -> checkBeginSignature(data, "424D", 0)
    || checkBeginSignature(data, "2800", 0)),
    PICT("PICT", data -> {
        final boolean pict1Format = checkEndSignature(data, "FF", 0)
            && (checkBeginSignature(data, "1101", 522)
                || checkBeginSignature(data, "1101", 10));
        final boolean pict2Format = checkEndSignature(data, "00FF", 0)
            && (checkBeginSignature(data, "001102FF0C00", 522)
                || checkBeginSignature(data, "001102FF0C00", 10));
        return pict1Format || pict2Format;
    }
    );

    private final String pictFormat;
    private final Function<Byte[], Boolean> formatChecker;

    PictureFormat(String pictFormat, Function<Byte[], Boolean> formatChecker) {
        this.pictFormat = pictFormat;
        this.formatChecker = formatChecker;
    }

    public Function<Byte[], Boolean> getFormatChecker() {
        return formatChecker;
    }

    private static boolean checkBeginSignature(Byte[] data, String startSignature, int startOffset) {
        final int dataLength = data.length;
        final int bytesToRead = startSignature.length() / 2;
        if (dataLength < startOffset + bytesToRead) {
            return false;
        }
        final StringBuilder hexBuilder = new StringBuilder();
        for (int i = startOffset; i < startOffset + bytesToRead; i++) {
            final String hex = String.format("%02X", data[i]);
            hexBuilder.append(hex);
        }
        return startSignature.contentEquals(hexBuilder);
    }

    private static boolean checkEndSignature(Byte[] data, String endSignature, int endOffset) {
        final int dataLength = data.length;
        final int bytesToRead = endSignature.length() / 2;
        if (dataLength < endOffset + bytesToRead) {
            return false;
        }
        final StringBuilder hexBuilder = new StringBuilder();
        for (int i = dataLength - bytesToRead - endOffset; i < dataLength - endOffset; i++) {
            final String hex = String.format("%02X", data[i]);
            hexBuilder.append(hex);
        }
        return endSignature.contentEquals(hexBuilder);
    }

    @Override
    public String toString() {
        return pictFormat;
    }
}
