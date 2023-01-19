package com.model.formatter;

import com.model.domain.*;

/**
 * Basic visitor interface
 */
public interface Visitor {
    void visitComposition(CompositionPart<?, ?> compositionPart) throws Throwable;

    void visitDocument(Document documentObj) throws Throwable;

    void visitDocumentCase(DocumentCase documentCase) throws Throwable;

    void visitTitle(Title titleObj) throws Exception;

    void visitHeading(Heading headingObj) throws Exception;

    void visitParagraph(Paragraph paragraphObj) throws Exception;

    void visitTable(Table tableObj) throws Throwable;

    void visitTableHeaderRow(TableHeaderRow tableHeaderRowObj) throws Throwable;

    void visitTableHeaderCell(TableHeaderCell tableHeaderCell) throws Throwable;

    void visitTableRow(TableRow tableRowObj) throws Throwable;

    void visitTableCell(TableCell tableCellObj) throws Exception;

    void visitSeparator(Separator separatorObj) throws Exception;

    void visitFooter(Footer footerObj) throws Exception;
}
