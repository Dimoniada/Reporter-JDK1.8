package com.model.formatter.word;

import com.google.common.base.MoreObjects;
import com.model.domain.Document;
import com.model.domain.DocumentCase;
import com.model.domain.Footer;
import com.model.domain.Heading;
import com.model.domain.Paragraph;
import com.model.domain.Picture;
import com.model.domain.Separator;
import com.model.domain.Table;
import com.model.domain.TableCell;
import com.model.domain.TableHeaderCell;
import com.model.domain.TableHeaderRow;
import com.model.domain.TableRow;
import com.model.domain.Title;
import com.model.domain.core.DataItem;
import com.model.domain.style.Style;
import com.model.domain.style.StyleService;
import com.model.formatter.BaseDetails;
import com.model.formatter.Formatter;
import com.model.formatter.word.style.WordStyleService;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Optional;

public abstract class WordFormatterVisitor extends Formatter implements BaseDetails {
    /**
     * docx native document {@link org.apache.poi.xwpf.usermodel.XWPFDocument}
     */
    protected XWPFDocument wordDocument;

    /**
     * docx native table {@link org.apache.poi.xwpf.usermodel.XWPFTable}
     */
    protected XWPFTable docxTable;

    protected FontCharset fontCharset;
    protected DecimalFormat decimalFormat;
    protected StyleService styleService;

    private final Logger log = LoggerFactory.getLogger(WordFormatterVisitor.class);

    @Override
    public void initializeResource() throws IOException {
        wordDocument = getWordDocument();
        outputStream = getOutputStream();
    }

    @Override
    public void cleanupResource() throws IOException {
        wordDocument.write(outputStream);
        wordDocument.close();
    }

    @Override
    public void visitDocument(Document documentObj) throws Throwable {
        styleService = getStyleService();
        styleService.writeStyles(wordDocument);
        this.visitComposition(documentObj);
    }

    /**
     * Creates page and writes text on it, holding blank space before next page
     *
     * @param documentCase paper which text will be written on
     * @throws Throwable on visit composition
     */
    @Override
    public void visitDocumentCase(DocumentCase documentCase) throws Throwable {
        this.visitComposition(documentCase);
        wordDocument.createParagraph().createRun().addBreak(BreakType.PAGE);
    }

    @Override
    public void visitTitle(Title titleObj) throws Exception {
        handleCustomItem(titleObj, wordDocument.createParagraph());
    }

    @Override
    public void visitHeading(Heading headingObj) {
        final String heading = headingObj.getText();
        final int depth = headingObj.getDepth();
        final XWPFParagraph paragraph = wordDocument.createParagraph();
        final String headingStyleId = "Heading " + depth;
        addHeading(depth, headingStyleId);
        paragraph.setStyle(headingStyleId);
        paragraph.createRun().setText(heading);
    }

    protected XWPFStyles addHeading(int depth, String headingStyleId) {
        final XWPFStyles styles = wordDocument.createStyles();

        if (styles.getStyle(headingStyleId) == null) {
            final CTStyle ctStyle = CTStyle.Factory.newInstance();
            ctStyle.setStyleId(headingStyleId);

            final CTString styleName = CTString.Factory.newInstance();
            styleName.setVal(headingStyleId);
            ctStyle.setName(styleName);

            final CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
            indentNumber.setVal(BigInteger.valueOf(depth));

            // lower number > style is more prominent in the formats bar
            ctStyle.setUiPriority(indentNumber);

            final CTOnOff ctOnOff = CTOnOff.Factory.newInstance();
            ctStyle.setUnhideWhenUsed(ctOnOff);

            // style shows up in the formats bar
            ctStyle.setQFormat(ctOnOff);

            // style defines a heading of the given level
            final CTPPrGeneral ppr = CTPPrGeneral.Factory.newInstance();
            ppr.setOutlineLvl(indentNumber);
            ctStyle.setPPr(ppr);

            final XWPFStyle style = new XWPFStyle(ctStyle);

            final CTHpsMeasure size = CTHpsMeasure.Factory.newInstance();
            size.setVal(new BigInteger(String.valueOf(depth)));

            // is a null op if already defined
            style.setType(STStyleType.PARAGRAPH);
            styles.addStyle(style);
        }
        return styles;
    }

    @Override
    public void visitParagraph(Paragraph paragraphObj) throws Exception {
        handleCustomItem(paragraphObj, wordDocument.createParagraph());
    }

    @Override
    public void visitTable(Table tableObj) throws Throwable {
//        final var watch = new StopWatch();
//        watch.start();
        final Style style =
            styleService
                .extractStyleFor(tableObj)
                .orElse(tableObj.getStyle());
        final String label = tableObj.getLabel();
        if (StringUtils.hasText(label)) {
            final Paragraph tableLabel = Paragraph.create(label).setStyle(style);
            ((WordStyleService) styleService)
                .handleCustomItem(
                    tableLabel,
                    wordDocument.createParagraph()
                );
        }
        createEmptyTable();
        ((WordStyleService) styleService).handleTable(style, docxTable);
        if (tableObj.getTableHeaderRow().isPresent()) {
            this.visitTableHeaderRow(tableObj.getTableHeaderRow().get());
        }
        this.visitComposition(tableObj);
//        watch.stop();
//        log.info("Table visited in {} ms", watch.getTotalTimeMillis());
        log.info("Visited table {}", tableObj);
    }

    @Override
    public void visitTableHeaderRow(TableHeaderRow tableHeaderRowObj) throws Throwable {
        docxTable.insertNewTableRow(0);
        this.visitComposition(tableHeaderRowObj);
    }

    @Override
    public void visitTableHeaderCell(TableHeaderCell tableHeaderCellObj) throws Exception {
        final XWPFTableRow headerRow = docxTable.getRow(0);
        final XWPFTableCell cell = headerRow.createCell();
        handleCustomItem(tableHeaderCellObj, cell);
    }

    @Override
    public void visitTableRow(TableRow tableRowObj) throws Throwable {
        final XWPFTableRow row = docxTable.createRow();
        row.getCtRow().setTcArray(new CTTc[0]);
        row.getTableCells().clear();
        styleService.extractStyleFor(tableRowObj);
        visitComposition(tableRowObj);
    }

    @Override
    public void visitTableCell(TableCell tableCellObj) throws Exception {
        final XWPFTableRow row = docxTable.getRow(docxTable.getNumberOfRows() - 1);
        final XWPFTableCell cell = row.createCell();
        ((WordStyleService) styleService).handleCustomItem(tableCellObj, cell);
    }

    @Override
    public void visitSeparator(Separator separatorObj) {
        wordDocument
            .createParagraph()
            .createRun()
            .addBreak(BreakType.TEXT_WRAPPING);
    }

    @Override
    public void visitFooter(Footer footerObj) throws Exception {
        final CTSectPr sectPr = wordDocument.getDocument().getBody().addNewSectPr();
        final XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy(wordDocument, sectPr);
        final CTP ctp = CTP.Factory.newInstance();
        final XWPFParagraph paragraph = new XWPFParagraph(ctp, wordDocument);
        handleCustomItem(footerObj, paragraph);
        headerFooterPolicy
            .createFooter(STHdrFtr.DEFAULT, new XWPFParagraph[]{paragraph});
    }

    @Override
    public void visitPicture(Picture pictureObj) throws Exception {
        ((WordStyleService) styleService).handleCustomItem(pictureObj, wordDocument.createParagraph());
    }

    public void handleCustomItem(DataItem item, Object element) throws Exception {
        ((WordStyleService) styleService).handleCustomItem(item, element);
    }

    public void createEmptyTable() {
        docxTable = wordDocument.createTable();
        docxTable.setTopBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "auto");
        docxTable.setLeftBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "auto");
        docxTable.setRightBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "auto");
        docxTable.setBottomBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "auto");
        docxTable.setInsideHBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "auto");
        docxTable.setInsideVBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "auto");
        docxTable.removeRow(0);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("wordDocument", wordDocument)
            .add("docxTable", docxTable)
            .add("fontCharset", fontCharset)
            .add("decimalFormat", decimalFormat)
            .add("styleService", styleService)
            .toString();
    }

    public XWPFDocument getWordDocument() {
        return Optional.ofNullable(wordDocument).orElse(new XWPFDocument());
    }

    public WordFormatterVisitor setWordDocument(XWPFDocument wordDocument) {
        this.wordDocument = wordDocument;
        return this;
    }

    public FontCharset getFontCharset() {
        return fontCharset;
    }

    public WordFormatterVisitor setFontCharset(FontCharset fontCharset) {
        this.fontCharset = fontCharset;
        return this;
    }

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public WordFormatterVisitor setDecimalFormat(DecimalFormat decimalFormat) {
        this.decimalFormat = decimalFormat;
        return this;
    }

    @Override
    public StyleService getStyleService() {
        if (styleService == null) {
            styleService = WordStyleService.create(fontCharset, decimalFormat);
        }
        return styleService;
    }

    public WordFormatterVisitor setStyleService(StyleService styleService) {
        this.styleService = styleService;
        return this;
    }
}
