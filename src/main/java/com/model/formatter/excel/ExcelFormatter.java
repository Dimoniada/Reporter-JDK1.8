package com.model.formatter.excel;

import com.google.common.base.MoreObjects;
import com.model.domain.Document;
import com.model.formatter.FormatterContext;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.springframework.core.io.WritableResource;

/**
 * Custom class for saving {@link Document} document in XLS/XLSX format.
 * {@link ExcelFormatter#handle handle(doc)} stores the result in
 * resource {@link ExcelFormatter#resource} that is created
 * from the given filename, generated automatically or
 * set "manually"
 * <p>
 * P.S.: if the resource is set "manually", then
 * {@link ExcelFormatter#handle handle(doc)} must be called
 * after {@link ExcelFormatter#setResource(WritableResource)}
 */
public abstract class ExcelFormatter extends ExcelFormatterVisitor {

    public ExcelFormatter(FontCharset fontCharset) {
        this.fontCharset = fontCharset;
    }

    public ExcelFormatter(FormatterContext context) {
        this.fontCharset = FontCharset.DEFAULT;
        this.decimalFormat = context.getDecimalFormat();
    }

    public ExcelFormatter() {
        this(FontCharset.DEFAULT);
    }

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("resource", resource)
                .add("fileName", fileName)
                .toString();
    }
}
