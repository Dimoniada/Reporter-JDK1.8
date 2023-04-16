package com.model.formatter.html;

import com.google.common.base.MoreObjects;
import com.model.domain.CompositionPart;
import com.model.domain.Document;
import com.model.domain.DocumentCase;
import com.model.domain.DocumentItem;
import com.model.domain.Footer;
import com.model.domain.Heading;
import com.model.domain.Paragraph;
import com.model.domain.Separator;
import com.model.domain.Table;
import com.model.domain.TableCell;
import com.model.domain.TableHeaderCell;
import com.model.domain.TableHeaderRow;
import com.model.domain.TableRow;
import com.model.domain.Title;
import com.model.domain.styles.BorderStyle;
import com.model.domain.styles.LayoutStyle;
import com.model.domain.styles.Style;
import com.model.domain.styles.StyleService;
import com.model.domain.styles.constants.BorderWeight;
import com.model.domain.styles.constants.Color;
import com.model.formatter.BaseDetails;
import com.model.formatter.Formatter;
import com.model.formatter.html.styles.HtmlStyleService;
import com.model.formatter.html.tag.Html;
import com.model.formatter.html.tag.HtmlBody;
import com.model.formatter.html.tag.HtmlCol;
import com.model.formatter.html.tag.HtmlColgroup;
import com.model.formatter.html.tag.HtmlFooter;
import com.model.formatter.html.tag.HtmlH1;
import com.model.formatter.html.tag.HtmlHead;
import com.model.formatter.html.tag.HtmlHeading;
import com.model.formatter.html.tag.HtmlLineSeparator;
import com.model.formatter.html.tag.HtmlParagraph;
import com.model.formatter.html.tag.HtmlTable;
import com.model.formatter.html.tag.HtmlTableCell;
import com.model.formatter.html.tag.HtmlTableHeaderCell;
import com.model.formatter.html.tag.HtmlTableRow;
import com.model.formatter.html.tag.HtmlTag;
import com.model.formatter.html.tag.HtmlTitle;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * The class generates a representation of the html document {@link Document}
 */
public abstract class HtmlFormatterVisitor extends Formatter implements BaseDetails {

    protected OutputStreamWriter outputStreamWriter;

    protected String encoding;
    protected Locale locale;
    protected DecimalFormat decimalFormat;

    protected StyleService styleService;
    protected TagCreator tagCreator;

    @Override
    public void initializeResource() throws IOException {
        outputStream = getOutputStream();
    }

    @Override
    public void visitDocument(Document documentObj) throws Throwable {
        styleService = getStyleService();
        outputStreamWriter = new OutputStreamWriter(outputStream, encoding);
        tagCreator = new TagCreator(outputStreamWriter, decimalFormat);
        tagCreator.write("<!doctype html>");
        final Html html = new Html();
        final HtmlHead htmlHead = new HtmlHead();
        final HtmlBody htmlBody = new HtmlBody();

        tagCreator.write(html.open());
        tagCreator.write(htmlHead.open());
        tagCreator.write("<meta charset=\"" + encoding + "\">");
        styleService.writeStyles(outputStreamWriter);
        if (StringUtils.hasText(documentObj.getLabel())) {
            final HtmlTitle htmlTitle = new HtmlTitle();
            tagCreator.write(htmlTitle.open());
            tagCreator.write(HtmlUtils.htmlEscape(documentObj.getLabel()));
            tagCreator.write(htmlTitle.close());
        }
        tagCreator.write(htmlHead.close());
        tagCreator.write(htmlBody.open());
        this.visitComposition(documentObj);
        tagCreator.write(htmlBody.close());
        tagCreator.write(html.close());
        outputStreamWriter.close();
    }

    @Override
    public void visitDocumentCase(DocumentCase documentCase) throws Throwable {
        this.visitComposition(documentCase);
    }

    @Override
    public void visitTitle(Title titleObj) throws Exception {
        final HtmlH1 htmlTitle = new HtmlH1();
        final Style style = styleService.extractStyleFor(titleObj).orElse(titleObj.getStyle());
        handleTag(htmlTitle, titleObj, style, true);
    }

    @Override
    public void visitHeading(Heading headingObj) throws Exception {
        final HtmlHeading htmlHeading = new HtmlHeading(headingObj.getDepth());
        final Style style = styleService.extractStyleFor(headingObj).orElse(headingObj.getStyle());
        handleTag(htmlHeading, headingObj, style, true);
    }

    @Override
    public void visitParagraph(Paragraph paragraphObj) throws Exception {
        final HtmlParagraph htmlParagraph = new HtmlParagraph();
        final Style style = styleService.extractStyleFor(paragraphObj).orElse(paragraphObj.getStyle());
        handleTag(htmlParagraph, paragraphObj, style, true);
    }

    @Override
    public void visitTable(Table tableObj) throws Throwable {
        final HtmlTable htmlTable = new HtmlTable();
        Style style = styleService.extractStyleFor(tableObj).orElse(tableObj.getStyle());
        final HtmlStyleService htmlStyleService = (HtmlStyleService) styleService;
        final boolean isUseColgroupTag = htmlStyleService.getHtmlColgroupTag().isEnabled();
        final boolean isUseHtml4Tags = htmlStyleService.isUseHtml4Tags();
        if (isUseHtml4Tags) {
            if (style == null) {
                style = LayoutStyle.create().setBorderBottom(BorderStyle.create(Color.BLACK, BorderWeight.THIN));
            }
            if (isUseColgroupTag) {
                handleColgroupTag(tableObj, style);
            } else {
                handleTag(htmlTable, tableObj, style, false);
            }
        } else {
            if (isUseColgroupTag) {
                handleColgroupTag(tableObj, style);
            } else {
                outputStreamWriter.write(htmlTable.open());
            }
        }

        if (tableObj.getTableHeaderRow().isPresent()) {
            visitTableHeaderRow(tableObj.getTableHeaderRow().get());
        }

        this.visitComposition(tableObj);
        outputStreamWriter.write(htmlTable.close());
    }

    @Override
    public void visitTableHeaderRow(TableHeaderRow tableHeaderRowObj) throws Throwable {
        visitRow(tableHeaderRowObj);
    }

    @Override
    public void visitTableHeaderCell(TableHeaderCell tableHeaderCellObj) throws Throwable {
        final HtmlTableHeaderCell htmlTableHeaderCell = new HtmlTableHeaderCell();
        final Style style = ((HtmlStyleService) styleService).handleTableCustomCell(tableHeaderCellObj);
        handleTag(htmlTableHeaderCell, tableHeaderCellObj, style, true);
    }

    @Override
    public void visitTableRow(TableRow tableRowObj) throws Throwable {
        styleService.extractStyleFor(tableRowObj);
        visitRow(tableRowObj);
    }

    @Override
    public void visitTableCell(TableCell tableCellObj) throws Exception {
        final HtmlTableCell htmlTableCell = new HtmlTableCell();
        final Style style = ((HtmlStyleService) styleService).handleTableCustomCell(tableCellObj);
        handleTag(htmlTableCell, tableCellObj, style, true);
    }

    @Override
    public void visitSeparator(Separator separatorObj) throws Exception {
        final HtmlLineSeparator htmlLineSeparator = new HtmlLineSeparator();
        final Style style = LayoutStyle.create().setBorderBottom(separatorObj.getBorderStyle());
        handleTag(htmlLineSeparator, separatorObj, style, false);
    }

    @Override
    public void visitFooter(Footer footerObj) throws Exception {
        final HtmlFooter htmlFooter = new HtmlFooter();
        final Style style = styleService.extractStyleFor(footerObj).orElse(footerObj.getStyle());
        handleTag(htmlFooter, footerObj, style, true);
    }

    protected void handleTag(HtmlTag tag, DocumentItem item, Style style, Boolean needCloseTag) throws Exception {
        final HtmlStyleService htmlStyleService = (HtmlStyleService) styleService;
        new TagCreator(outputStreamWriter, decimalFormat)
            .setItem(item)
            .writeTag(
                tag,
                style,
                htmlStyleService.isUseHtml4Tags(),
                htmlStyleService.contains(style) && !htmlStyleService.isWriteStyleInplace(),
                htmlStyleService.getHtmlColgroupTag(),
                needCloseTag
            );
    }

    protected void visitRow(CompositionPart<?, ?> row) throws Throwable {
        final HtmlTableRow htmlTableRow = new HtmlTableRow();
        outputStreamWriter.write(htmlTableRow.open());
        this.visitComposition(row);
        outputStreamWriter.write(htmlTableRow.close());
    }

    private void handleColgroupTag(Table tableObj, Style style) throws Exception {
        final HtmlTable htmlTable = new HtmlTable();
        if (tableObj.getTableHeaderRow().isPresent()) {
            handleTag(htmlTable, tableObj, style, false);
            if (styleService.extractStyleFor(TableCell.create()).isPresent()) {
                final Style cellStyle = styleService.extractStyleFor(TableCell.create()).get();
                final TableHeaderRow thr = tableObj.getTableHeaderRow().get();
                final HtmlColgroup htmlColgroup = new HtmlColgroup();
                outputStreamWriter.write(htmlColgroup.open());
                for (final TableHeaderCell ignored : thr.getParts()) {
                    handleTag(new HtmlCol(), TableCell.create(), cellStyle, true);
                }
                outputStreamWriter.write(htmlColgroup.close());
            }
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("outputStreamWriter", outputStreamWriter)
            .add("encoding", encoding)
            .add("locale", locale)
            .add("decimalFormat", decimalFormat)
            .add("styleService", styleService)
            .add("parent", super.toString())
            .toString();
    }

    public OutputStreamWriter getOutputStreamWriter() {
        return outputStreamWriter;
    }

    public HtmlFormatterVisitor setOutputStreamWriter(OutputStreamWriter outputStreamWriter) {
        this.outputStreamWriter = outputStreamWriter;
        return this;
    }

    public String getEncoding() {
        return encoding;
    }

    public HtmlFormatterVisitor setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public StyleService getStyleService() {
        if (styleService == null) {
            styleService = HtmlStyleService.create(false, decimalFormat);
        }
        return styleService;
    }

    @SuppressWarnings("unchecked")
    public <T extends HtmlFormatterVisitor> T setStyleService(StyleService styleService) {
        this.styleService = styleService;
        return (T) this;
    }

    public Locale getLocale() {
        return locale;
    }

    public HtmlFormatterVisitor setLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public HtmlFormatterVisitor setDecimalFormat(DecimalFormat decimalFormat) {
        this.decimalFormat = decimalFormat;
        return this;
    }
}
