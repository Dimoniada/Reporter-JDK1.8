package com.model.formatter.excel;

import com.model.formatter.FormatterContext;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Class for writing to .xlsx format
 */
@Component
public class XlsxFormatter extends ExcelFormatter implements XlsxDetails {

    public XlsxFormatter() {
        super(FontCharset.DEFAULT);
    }

    public XlsxFormatter(FormatterContext context) {
        super(context);
    }

    public static XlsxFormatter create() {
        return new XlsxFormatter();
    }

    public static XlsxFormatter create(FormatterContext context) {
        return new XlsxFormatter(context);
    }

    @Override
    public Workbook getWorkbook() {
        return Optional.ofNullable(workbook).orElse(new XSSFWorkbook());
    }
}
