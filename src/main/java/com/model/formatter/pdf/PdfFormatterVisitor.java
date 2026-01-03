package com.model.formatter.pdf;

import com.google.common.base.MoreObjects;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.UnitValue;
import com.model.domain.Document;
import com.model.domain.DocumentCase;
import com.model.domain.Footer;
import com.model.domain.Heading;
import com.model.domain.LineSeparator;
import com.model.domain.Paragraph;
import com.model.domain.Picture;
import com.model.domain.Table;
import com.model.domain.TableCell;
import com.model.domain.TableHeaderCell;
import com.model.domain.TableHeaderRow;
import com.model.domain.TableRow;
import com.model.domain.Title;
import com.model.domain.core.PictureItem;
import com.model.domain.core.TextItem;
import com.model.domain.style.BorderStyle;
import com.model.domain.style.Style;
import com.model.domain.style.StyleService;
import com.model.domain.style.constant.BorderWeight;
import com.model.formatter.BaseDetails;
import com.model.formatter.Formatter;
import com.model.formatter.pdf.style.PdfPageEventHandler;
import com.model.formatter.pdf.style.PdfStyleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * The class generates a representation of a pdf document {@link Document}
 */
public abstract class PdfFormatterVisitor extends Formatter implements BaseDetails {
    private static final float DEFAULT_MARGIN = 20;

    protected PdfWriter writer;
    protected PdfDocument pdf;
    protected com.itextpdf.layout.element.Table table;
    protected com.itextpdf.layout.Document document;
    protected String encoding;
    protected DecimalFormat decimalFormat;
    protected StyleService styleService;

    private final Logger log = LoggerFactory.getLogger(PdfFormatterVisitor.class);

    @Override
    public void initializeResource() throws IOException {
        outputStream = getOutputStream();
    }

    @Override
    public void visitDocument(Document documentObj) throws Throwable {
        styleService = getStyleService();
        writer = new PdfWriter(outputStream);
        pdf = new PdfDocument(writer);

        /*Add MetaInfo*/
        final PdfDocumentInfo pdfInfo = pdf.getDocumentInfo();
        if (StringUtils.hasText(documentObj.getAuthor())) {
            pdfInfo.setAuthor(documentObj.getAuthor());
        }
        if (StringUtils.hasText(documentObj.getLabel())) {
            pdfInfo.setTitle(documentObj.getLabel());
        }
        if (StringUtils.hasText(documentObj.getDescription())) {
            pdfInfo.setSubject(documentObj.getDescription());
        }
        pdfInfo.setCreator("PdfFormatter");

        document = new com.itextpdf.layout.Document(pdf);
        document.setMargins(DEFAULT_MARGIN, DEFAULT_MARGIN, DEFAULT_MARGIN, DEFAULT_MARGIN);
        visitComposition(documentObj);
        document.close();
    }

    /**
     * For a PDF document - {@link DocumentCase} is a new page.
     * While a {@link DocumentCase} added to a {@link Document} structure,
     * the last page will be ended and a new page will be created with a writer on it.
     */
    @Override
    public void visitDocumentCase(DocumentCase documentCase) throws Throwable {
        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        visitComposition(documentCase);
    }

    @Override
    public void visitTitle(Title titleObj) throws Exception {
        ((PdfStyleService) styleService).handleSimpleElement(titleObj, document);
    }

    @Override
    public void visitParagraph(Paragraph paragraphObj) throws Exception {
        ((PdfStyleService) styleService).handleSimpleElement(paragraphObj, document);
    }

    @Override
    public void visitHeading(Heading headingObj) throws Exception {
        ((PdfStyleService) styleService).handleSimpleElement(headingObj, document);
    }

    @Override
    public void visitTable(Table tableObj) throws Throwable {
//        final var watch = new StopWatch();
//        watch.start();
        final Style style =
            styleService
                .extractStyleFor(tableObj)
                .orElse(tableObj.getStyle());
        if (StringUtils.hasText(tableObj.getLabel())) {
            final Text text = new Text(tableObj.getLabel());
            ((PdfStyleService) styleService).convertStyleToElement(style, text, text);
            document.add(new com.itextpdf.layout.element.Paragraph(text));
        }
        if (!tableObj.getTableHeaderRow().isPresent()) {
            throw new IllegalArgumentException(
                String.format("There is no header row in table %s", tableObj)
            );
        }
        final TableHeaderRow tableHeaderRow = tableObj.getTableHeaderRow().get();
        final int colCount = (int) tableHeaderRow.getCellCount();
        if (colCount > 0) {
            final float[] columns = new float[colCount];
            Arrays.fill(columns, 1);
            table = new com.itextpdf.layout.element.Table(UnitValue.createPercentArray(columns));
            table.setWidth(UnitValue.createPercentValue(100));
            visitTableHeaderRow(tableHeaderRow);
            visitComposition(tableObj);
            document.add(table);
        }
//        watch.stop();
//        log.info("Table visited in {} ms", watch.getTotalTimeMillis());
        log.info("Visited table {}", tableObj);
    }

    @Override
    public void visitTableHeaderRow(TableHeaderRow tableHeaderRowObj) throws Throwable {
        visitComposition(tableHeaderRowObj);
    }

    @Override
    public void visitTableHeaderCell(TableHeaderCell tableHeaderCellObj) throws Exception {
        final Cell cell = ((PdfStyleService) styleService).handleTableCustomCell(tableHeaderCellObj);
        table.addHeaderCell(cell);
    }

    @Override
    public void visitTableRow(TableRow tableRowObj) throws Throwable {
        styleService.extractStyleFor(tableRowObj);
        visitComposition(tableRowObj);
    }

    @Override
    public void visitTableCell(TableCell tableCellObj) throws Exception {
        final Cell cell = ((PdfStyleService) styleService).handleTableCustomCell(tableCellObj);
        table.addCell(cell);
    }

    @Override
    public void visitPicture(Picture pictureObj) throws Exception {
        ((PdfStyleService) styleService).handlePicture(pictureObj, document);
    }

    @Override
    public void visitLineSeparator(LineSeparator lineSeparatorObj) {
        final BorderStyle border = lineSeparatorObj.getBorderStyle();
        if (border != null && border.getWeight() != BorderWeight.NONE) {
            final ILineDrawer lineDrawer = PdfStyleService.toPdfILineDrawer(border.getWeight());
            lineDrawer.setColor(PdfStyleService.toPdfColor(border.getColor()));
            document.add(new com.itextpdf.layout.element.LineSeparator(lineDrawer));
        }
    }

    @Override
    public void visitFooter(Footer footerObj) throws Exception {
        final Style style =
            styleService
                .extractStyleFor(footerObj)
                .orElse(footerObj.getStyle());
        final com.itextpdf.layout.element.Paragraph elParagraph = new com.itextpdf.layout.element.Paragraph();
        if (footerObj.isDataInheritedFrom(TextItem.class)) {
            final Text text = new Text(footerObj.getText());
            elParagraph.add(text);
            ((PdfStyleService) styleService).convertStyleToElement(style, text, elParagraph);
        }
        if (footerObj.isDataInheritedFrom(PictureItem.class)) {
            final byte[] data = footerObj.getData();
            final ImageData imageData = ImageDataFactory.create(data);
            final Image image = new Image(imageData);
            elParagraph.add(image);
            ((PdfStyleService) styleService).convertStyleToElement(style, null, image);
        }

        final AbstractPdfDocumentEventHandler handler = PdfPageEventHandler.create(elParagraph, document, style);
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, handler);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("writer", writer)
            .add("pdf", pdf)
            .add("table", table)
            .add("os", outputStream)
            .add("document", document)
            .add("encoding", encoding)
            .add("decimalFormat", decimalFormat)
            .add("styleService", styleService)
            .toString();
    }

    public PdfWriter getWriter() {
        return writer;
    }

    public PdfFormatterVisitor setWriter(PdfWriter writer) {
        this.writer = writer;
        return this;
    }

    public PdfDocument getPdf() {
        return pdf;
    }

    public PdfFormatterVisitor setPdf(PdfDocument pdf) {
        this.pdf = pdf;
        return this;
    }

    public com.itextpdf.layout.element.Table getTable() {
        return table;
    }

    public PdfFormatterVisitor setTable(com.itextpdf.layout.element.Table table) {
        this.table = table;
        return this;
    }

    public com.itextpdf.layout.Document getDocument() {
        return document;
    }

    public PdfFormatterVisitor setDocument(com.itextpdf.layout.Document document) {
        this.document = document;
        return this;
    }

    public String getEncoding() {
        return encoding;
    }

    public PdfFormatterVisitor setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public PdfFormatterVisitor setDecimalFormat(DecimalFormat decimalFormat) {
        this.decimalFormat = decimalFormat;
        return this;
    }

    @Override
    public StyleService getStyleService() {
        if (styleService == null) {
            styleService = PdfStyleService.create(encoding, null, decimalFormat);
        }
        return styleService;
    }

    @SuppressWarnings("unchecked")
    public <T extends PdfFormatterVisitor> T setStyleService(StyleService styleService) {
        this.styleService = styleService;
        return (T) this;
    }
}
