package com.model.formatter.excel;

import com.google.common.base.MoreObjects;
import com.model.domain.Table;
import com.model.domain.*;
import com.model.domain.styles.*;
import com.model.formatter.Formatter;
import com.model.formatter.excel.styles.ExcelStyleService;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Optional;

/**
 * The class accepts an excel workbook {@link ExcelFormatterVisitor#workbook} and
 * generates a xls/xlsx representation of the document {@link Document}
 */
public class ExcelFormatterVisitor extends Formatter {
    protected Workbook workbook;
    protected FontCharset fontCharset;
    protected DecimalFormat decimalFormat;
    protected StyleService styleService;
    private final Logger log = LoggerFactory.getLogger(ExcelFormatterVisitor.class);

    @Override
    public String getExtension() {
        return null;
    }

    @Override
    public MediaType getContentMediaType() {
        return null;
    }

    @Override
    public void initializeResource() {
        workbook = getWorkbook();
    }

    @Override
    public void cleanupResource() throws IOException {
        workbook.write(getOutputStream());
        workbook.close();
    }

    @Override
    public void visitDocument(Document documentObj) throws Throwable {
        styleService = getStyleService();
        styleService.writeStyles(workbook);
        this.visitComposition(documentObj);
    }

    @Override
    public void visitDocumentCase(DocumentCase documentCase) throws Throwable {
        workbook.createSheet(WorkbookUtil.createSafeSheetName(documentCase.getName())); // SheetName must be unique
        this.visitComposition(documentCase);
    }

    @Override
    public void visitTitle(Title titleObj) throws Exception {
        final Sheet sheet = getLastSheet(workbook);
        final Row row = createRow(sheet, 1);
        ((ExcelStyleService) styleService)
            .fillCellFromItem(
                createCell(row, 1, CellType.STRING),
                titleObj
            );
    }

    @Override
    public void visitParagraph(Paragraph paragraphObj) throws Exception {
        final Sheet sheet = getLastSheet(workbook);
        final Row row = createRow(sheet, 1);
        ((ExcelStyleService) styleService)
            .fillCellFromItem(
                createCell(row, 1, CellType.STRING),
                paragraphObj
            );
    }

    @Override
    public void visitHeading(Heading headingObj) throws Exception {
        final int depth = headingObj.getDepth();
        final Sheet sheet = getLastSheet(workbook);
        final Row row = createRow(sheet, 1);
        ((ExcelStyleService) styleService)
            .fillCellFromItem(
                createCell(row, depth + 1, CellType.STRING),
                headingObj
            );
    }

    @Override
    public void visitTable(Table tableObj) throws Throwable {
//        final var watch = new StopWatch();
//        watch.start();
        final Sheet sheet = getLastSheet(workbook);
        org.apache.poi.ss.usermodel.Cell cell;
        final Style style =
            styleService
                .extractStyleFor(tableObj)
                .orElse(tableObj.getStyle());
        if (StringUtils.hasText(tableObj.getLabel())) {
            final Row row = createRow(sheet, 1);
            cell = createCell(row, 1, CellType.STRING);
            cell.setCellValue(tableObj.getLabel());
            if (style instanceof TextStyle) {
                ((ExcelStyleService) styleService).convertTextStyleToCell(cell, (TextStyle) style);
            } else if (style instanceof LayoutStyle) {
                ((ExcelStyleService) styleService).convertLayoutStyleToCell(cell, (LayoutStyle) style);
            } else if (style instanceof LayoutTextStyle) {
                ((ExcelStyleService) styleService).convertLayoutTextStyleToCell(cell, (LayoutTextStyle) style);
            }
        }
        if (tableObj.getTableHeaderRow().isPresent()) {
            this.visitTableHeaderRow(tableObj.getTableHeaderRow().get());
        }
        this.visitComposition(tableObj);
        ((ExcelStyleService) styleService).adjustHeaderCells();
//        watch.stop();
//        log.info("Table visited in {} ms", watch.getTotalTimeMillis());
    }

    @Override
    public void visitTableHeaderRow(TableHeaderRow tableHeaderRowObj) throws Throwable {
        createRow(getLastSheet(workbook), 1);
        this.visitComposition(tableHeaderRowObj);
    }

    @Override
    public void visitTableHeaderCell(TableHeaderCell tableHeaderCellObj) throws Exception {
        final Row row = getLastRow(getLastSheet(workbook));
        final Cell cell = createCell(row, 1, CellType.STRING);
        ((ExcelStyleService) styleService).handleTableCustomCell(tableHeaderCellObj, cell);

        final Optional<Style> optStyle = styleService.extractStyleFor(tableHeaderCellObj);
        Style tableHeaderStyle = tableHeaderCellObj.getStyle();

        if (optStyle.isPresent()) {
            if (tableHeaderStyle == null) {
                tableHeaderStyle = optStyle.get();
            } else {
                StyleUtils.joinWith(optStyle.get(), tableHeaderStyle);
            }
        }
        LayoutStyle lhsStyle = null;
        if (tableHeaderStyle instanceof LayoutTextStyle) {
            lhsStyle = ((LayoutTextStyle) tableHeaderStyle).getLayoutStyle();
        } else if (tableHeaderStyle instanceof LayoutStyle) {
            lhsStyle = (LayoutStyle) tableHeaderStyle;
        }
        if (lhsStyle != null
            && (lhsStyle.isAutoWidth() || lhsStyle.getWidth() > 0)) {
            ((ExcelStyleService) styleService).getNeedAdjustHeaderCells().put(cell, lhsStyle);
        }
    }

    @Override
    public void visitTableRow(TableRow tableRowObj) throws Throwable {
        createRow(getLastSheet(workbook), 1);
        styleService.extractStyleFor(tableRowObj);
        this.visitComposition(tableRowObj);
    }

    @Override
    public void visitTableCell(TableCell tableCellObj) throws Exception {
        final Row row = getLastRow(getLastSheet(workbook));
        final Cell cell = createCell(row, 1, CellType.STRING);
        ((ExcelStyleService) styleService).handleTableCustomCell(tableCellObj, cell);
    }

    @Override
    public void visitSeparator(Separator separatorObj) {
        final Sheet sheet = getLastSheet(workbook);
        final Row row = createRow(sheet, 1);
        createCell(row, 1, CellType.STRING).setCellValue(""); // A new cell needs for a value
    }

    @Override
    public void visitFooter(com.model.domain.Footer footerObj) throws Exception {
        final Sheet sheet = getLastSheet(workbook);
        final Row row = createRow(sheet, 1);
        createCell(row, 1, CellType.STRING).setCellValue("");
        ((ExcelStyleService) styleService)
            .fillCellFromItem(
                createCell(row, 1, CellType.STRING),
                footerObj
            );
    }

    /**
     * Returns the last sheet in an Excel workbook,
     * if there are no sheets, then creates it
     *
     * @param workbook excel workbook
     * @return last page of the book
     */
    private Sheet getLastSheet(Workbook workbook) {
        if (workbook.getNumberOfSheets() == 0) {
            workbook.createSheet();
        }
        return workbook.getSheetAt(workbook.getNumberOfSheets() - 1);
    }

    /**
     * Returns the last row of the sheet
     * or null if the sheet is empty
     *
     * @param sheet excel sheet
     * @return last line of the sheet
     */
    private Row getLastRow(Sheet sheet) {
        final int count = sheet.getPhysicalNumberOfRows();
        return count > 0 ? sheet.getRow(count - 1) : null;
    }

    /**
     * Returns the last cell in a row
     * or null if row is empty
     *
     * @param row excel row
     * @return last cell of the row
     */
    private Cell getLastCell(Row row) {
        final int count = row.getPhysicalNumberOfCells();
        return count > 0 ? row.getCell(count - 1) : null;
    }

    /**
     * Create excel row
     *
     * @param sheet    sheet on which the row is created
     * @param rowCount offset down from the last row
     * @return created string
     */
    private Row createRow(Sheet sheet, int rowCount) {
        Row row = getLastRow(sheet);
        final int bias = sheet.getPhysicalNumberOfRows();
        for (int i = 0; i < rowCount; i++) {
            row = sheet.createRow(bias + i);
        }
        return row;
    }

    /**
     * Create an Excel cell
     *
     * @param row       the row in which the cell is created
     * @param cellCount offset to the right of the last cell in the row
     * @param type      cell type (usually STRING)
     * @return - created cell
     */
    private Cell createCell(Row row, int cellCount, CellType type) {
        Cell cell = getLastCell(row);
        final int bias = row.getPhysicalNumberOfCells();
        for (int i = 0; i < cellCount; i++) {
            cell = row.createCell(bias + i, type);
        }
        return cell;
    }

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("workbook", workbook)
                .add("os", outputStream)
                .add("fontCharset", fontCharset)
                .add("decimalFormat", decimalFormat)
                .add("styleService", styleService)
                .add("parent", super.toString())
                .toString();
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public ExcelFormatterVisitor setWorkbook(Workbook workbook) {
        this.workbook = workbook;
        return this;
    }

    public FontCharset getFontCharset() {
        return fontCharset;
    }

    public ExcelFormatterVisitor setFontCharset(FontCharset fontCharset) {
        this.fontCharset = fontCharset;
        return this;
    }

    public StyleService getStyleService() {
        if (styleService == null) {
            styleService = ExcelStyleService.create(fontCharset, decimalFormat);
        }
        return styleService;
    }

    @SuppressWarnings("unchecked")
    public <T extends ExcelFormatterVisitor> T setStyleService(StyleService styleService) {
        this.styleService = styleService;
        return (T) this;
    }
}