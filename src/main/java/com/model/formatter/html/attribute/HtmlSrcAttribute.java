package com.model.formatter.html.attribute;

import com.model.domain.style.constant.PictureFormat;
import com.model.utils.PictureUtils;

import java.util.Base64;
import java.util.Locale;

public class HtmlSrcAttribute extends HtmlAttribute {
    public static final String ATTR_NAME = "src";

    @Override
    public String getAttribute() {
        return ATTR_NAME;
    }

    // PICT is not supported
    public HtmlSrcAttribute setSrc(byte[] data) {
        final PictureFormat pictureFormat = PictureUtils.getFormat(data);
        if (pictureFormat != null) {
            final String src =
                String.format(
                    "data:image/%s;base64,%s",
                    pictureFormat.toString().toLowerCase(Locale.ENGLISH),
                    Base64.getEncoder().encodeToString(data)
                );
            setAttributeValue(src);
        }
        return this;
    }

    @Override
    public String getAssignmentPattern(Boolean isHtml4) {
        return ASSIGNMENT_PATTERN_HTML4;
    }
}