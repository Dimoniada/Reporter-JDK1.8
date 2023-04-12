package com.model.formatter.word;

import com.model.formatter.FormatterContext;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.springframework.stereotype.Component;

/**
 * Class for writing to .docx format
 */
@Component
public class DocxFormatter extends WordFormatter implements DocxDetails {

    public DocxFormatter() {
        super(FontCharset.DEFAULT);
    }

    public DocxFormatter(FormatterContext context) {
        super(context);
    }

    public static DocxFormatter create() {
        return new DocxFormatter();
    }

    public static DocxFormatter create(FormatterContext context) {
        return new DocxFormatter(context);
    }
}
