package com.model.formatter.excel;

import com.model.formatter.FormatterContext;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Class for writing to .xls format
 */
@Component
public class XlsFormatter extends ExcelFormatter implements XlsDetails {

    public XlsFormatter() {
        super(FontCharset.DEFAULT);
    }

    public XlsFormatter(FormatterContext context) {
        super(context);
    }

    public static XlsFormatter create() {
        return new XlsFormatter();
    }

    public static XlsFormatter create(FormatterContext context) {
        return new XlsFormatter(context);
    }

    @Override
    public Workbook getWorkbook() {
        return Optional.ofNullable(workbook).orElse(new HSSFWorkbook());
    }
}
