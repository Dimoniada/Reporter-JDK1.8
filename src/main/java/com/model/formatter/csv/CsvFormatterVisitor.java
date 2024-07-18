package com.model.formatter.csv;

import com.google.common.base.MoreObjects;
import com.model.domain.Document;
import com.model.domain.DocumentCase;
import com.model.domain.Footer;
import com.model.domain.Heading;
import com.model.domain.Paragraph;
import com.model.domain.Picture;
import com.model.domain.Separator;
import com.model.domain.Table;
import com.model.domain.TableHeaderRow;
import com.model.domain.TableRow;
import com.model.domain.Title;
import com.model.domain.core.DataItem;
import com.model.domain.core.TextItem;
import com.model.formatter.BaseDetails;
import com.model.formatter.Formatter;
import com.model.utils.LocalizedNumberUtils;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * The class generates a csv representation of the document {@link Document}
 */
public abstract class CsvFormatterVisitor extends Formatter implements BaseDetails {
    protected OutputStreamWriter writer;
    protected CsvListWriter csvWriter;

    protected String encoding;
    protected DecimalFormat decimalFormat;

    protected CsvPreference csvPreference = CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE;

    @Override
    public void initializeResource() throws IOException {
        outputStream = getOutputStream();
    }

    @Override
    public void visitDocument(Document documentObj) throws Throwable {

        if (!Charset.isSupported(encoding)) {
            throw new IllegalArgumentException(
                String.format(
                    "Unsupported character set %s in the current Java virtual machine",
                    encoding
                )
            );
        }
        writer = new OutputStreamWriter(outputStream, encoding);
        csvWriter = new CsvListWriter(writer, csvPreference);
        this.visitComposition(documentObj);
        csvWriter.close();
    }

    @Override
    public void visitDocumentCase(DocumentCase documentCase) throws Throwable {
        csvWriter.write("");
    }

    @Override
    public void visitTitle(Title titleObj) throws Exception {
        csvWriter.write(LocalizedNumberUtils.applyDecimalFormat(titleObj.getText(), titleObj.getStyle(), decimalFormat));
    }

    @Override
    public void visitHeading(Heading headingObj) throws Exception {
        csvWriter.write(LocalizedNumberUtils.applyDecimalFormat(headingObj.getText(), headingObj.getStyle(), decimalFormat));
    }

    @Override
    public void visitParagraph(Paragraph paragraphObj) throws Exception {
        csvWriter.write(LocalizedNumberUtils.applyDecimalFormat(paragraphObj.getText(), paragraphObj.getStyle(), decimalFormat));
    }

    @Override
    public void visitTable(Table tableObj) throws Throwable {
        if (tableObj.getTableHeaderRow().isPresent()) {
            this.visitTableHeaderRow(tableObj.getTableHeaderRow().get());
        }
        this.visitComposition(tableObj);
    }

    @Override
    public void visitTableHeaderRow(TableHeaderRow tableHeaderRowObj) throws Throwable {
        writeRow(
            StreamSupport
                .stream(
                    tableHeaderRowObj.getParts().spliterator(),
                    false
                )
                .collect(Collectors.toList())
        );
    }

    @Override
    public void visitTableRow(TableRow tableRowObj) throws Throwable {
        writeRow(
            StreamSupport
                .stream(
                    tableRowObj.getParts().spliterator(),
                    false
                )
                .collect(Collectors.toList()));
    }

    protected void writeRow(Iterable<DataItem<?>> tableCells) throws Throwable {
        final List<String> tableRow = new ArrayList<>();
        for (final DataItem<?> o : tableCells) {
            final String text = o.isDataInheritedFrom(TextItem.class) ? o.getText() : "{picture}";
            tableRow.add(LocalizedNumberUtils.applyDecimalFormat(text, o.getStyle(), decimalFormat));
        }
        csvWriter.write(tableRow);
    }

    @Override
    public void visitSeparator(Separator separatorObj) throws Exception {
        csvWriter.write("");
    }

    @Override
    public void visitFooter(Footer footerObj) throws Exception {
        csvWriter.write("");
    }

    @Override
    public void visitPicture(Picture picture) throws Throwable {
        csvWriter.write("pic");
    }

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("writer", writer)
                .add("csvWriter", csvWriter)
                .add("os", outputStream)
                .add("encoding", encoding)
                .add("decimalFormat", decimalFormat)
                .add("csvPreference", csvPreference)
                .toString();
    }

    public OutputStreamWriter getWriter() {
        return writer;
    }

    public OutputStream getOs() {
        return outputStream;
    }

    public CsvListWriter getCsvWriter() {
        return csvWriter;
    }

    public String getEncoding() {
        return encoding;
    }

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public CsvPreference getCsvPreference() {
        return csvPreference;
    }

    public CsvFormatterVisitor setWriter(OutputStreamWriter writer) {
        this.writer = writer;
        return this;
    }

    public CsvFormatterVisitor setOs(OutputStream os) {
        this.outputStream = os;
        return this;
    }

    public CsvFormatterVisitor setCsvWriter(CsvListWriter csvWriter) {
        this.csvWriter = csvWriter;
        return this;
    }

    public CsvFormatterVisitor setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public CsvFormatterVisitor setDecimalFormat(DecimalFormat decimalFormat) {
        this.decimalFormat = decimalFormat;
        return this;
    }

    public CsvFormatterVisitor setCsvPreference(CsvPreference csvPreference) {
        this.csvPreference = csvPreference;
        return this;
    }
}
