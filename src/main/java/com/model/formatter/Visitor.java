package com.model.formatter;


import com.model.domain.CompositionPart;
import com.model.domain.Document;
import com.model.domain.DocumentCase;
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
