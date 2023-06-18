package com.model.formatter.word;

import com.google.common.base.MoreObjects;
import com.model.domain.Document;
import com.model.formatter.FormatterContext;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.springframework.core.io.WritableResource;

/**
 * Base class for saving {@link Document} document in DOC/DOCX format.
 * {@link WordFormatter#handle handle(doc)} stores the result in
 * resource {@link WordFormatter#resource} that is created
 * from the given filename, generated automatically or
 * set "manually"
 * <p>
 * P.S.: if the resource is set "manually", then
 * {@link WordFormatter#handle handle(doc)} must be called
 * after {@link WordFormatter#setResource(WritableResource)}
 */
public abstract class WordFormatter extends WordFormatterVisitor {

    public WordFormatter(FontCharset fontCharset) {
        this.fontCharset = fontCharset;
    }

    public WordFormatter(FormatterContext context) {
        this.fontCharset = FontCharset.DEFAULT;
        this.decimalFormat = context.getDecimalFormat();
    }

    public WordFormatter() {
        this(FontCharset.DEFAULT);
    }

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("resource", resource)
                .add("fileName", fileName)
                .add("parent", super.toString())
                .toString();
    }
}
