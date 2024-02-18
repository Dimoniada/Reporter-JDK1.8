package com.model.formatter;

import com.model.domain.CompositionPart;
import com.model.domain.Document;
import com.model.domain.DocumentCase;
import com.model.domain.DocumentItem;
import com.model.domain.Footer;
import com.model.domain.Heading;
import com.model.domain.Paragraph;
import com.model.domain.Picture;
import com.model.domain.Separator;
import com.model.domain.Table;
import com.model.domain.TableHeaderCell;
import com.model.domain.TableHeaderRow;
import com.model.domain.TableRow;
import com.model.domain.Title;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class writes to the log calls of non-overridden Visitor methods,
 * warning about a possible error in the implementation.
 */
public class FormatterVisitor implements Visitor {

    private static final Logger log = LoggerFactory.getLogger(FormatterVisitor.class);
    private final String superClassName = getClass().getGenericSuperclass().getTypeName();

    @Override
    public void visitComposition(CompositionPart<?, ?> compositionPart) throws Throwable {
        for (final DocumentItem item : compositionPart.getParts()) {
            item.accept(this);
        }
    }

    @Override
    public void visitDocument(Document documentObj) throws Throwable {
        log.debug("No overriding for visitDocument: {}", documentObj);
        throw new Throwable("No overriding for visitDocument in " + superClassName);
    }

    @Override
    public void visitDocumentCase(DocumentCase documentCase) throws Throwable {
        log.debug("No overriding for visitDocumentCase: {}", documentCase);
        throw new Throwable("No overriding for visitDocumentCase in " + superClassName);
    }

    @Override
    public void visitTitle(Title titleObj) throws Throwable {
        log.debug("No overriding for visitTitle: {}", titleObj);
        throw new Throwable("No overriding for visitTitle " + superClassName);
    }

    @Override
    public void visitHeading(Heading headingObj) throws Throwable {
        log.debug("No overriding for visitHeading: {}", headingObj);
        throw new Throwable("No overriding for visitHeading " + superClassName);
    }

    @Override
    public void visitParagraph(Paragraph paragraphObj) throws Throwable {
        log.debug("No overriding for visitParagraph: {}", paragraphObj);
        throw new Throwable("No overriding for visitParagraph " + superClassName);
    }

    @Override
    public void visitTable(Table tableObj) throws Throwable {
        log.debug("No overriding for visitTable: {}", tableObj);
        throw new Throwable("No overriding for visitTable " + superClassName);
    }

    @Override
    public void visitTableHeaderRow(TableHeaderRow tableHeaderRowObj) throws Throwable {
        log.debug("No overriding for visitTableHeaderRow: {}", tableHeaderRowObj);
        throw new Throwable("No overriding for visitTableHeaderRow " + superClassName);
    }

    @Override
    public void visitTableHeaderCell(TableHeaderCell tableHeaderCellObj) throws Throwable {
        log.debug("No overriding for visitTableHeaderCell: {}", tableHeaderCellObj);
        throw new Throwable("No overriding for visitTableHeaderCell " + superClassName);
    }

    @Override
    public void visitTableRow(TableRow tableRowObj) throws Throwable {
        log.debug("No overriding for visitTableRow: {}", tableRowObj);
        throw new Throwable("No overriding for visitTableHeaderCell " + superClassName);
    }

    @Override
    public void visitTableCell(DocumentItem tableCellObj) throws Throwable {
        log.debug("No overriding for visitTableCell: {}", tableCellObj);
        throw new Throwable("No overriding for visitTableCell " + superClassName);
    }

    @Override
    public void visitSeparator(Separator separatorObj) throws Throwable {
        log.debug("No overriding for visitSeparator: {}", separatorObj);
        throw new Throwable("No overriding for visitSeparator " + superClassName);
    }

    @Override
    public void visitFooter(Footer footerObj) throws Throwable {
        log.debug("No overriding for visitFooter: {}", footerObj);
        throw new Throwable("No overriding for visitFooter " + superClassName);
    }

    @Override
    public void visitPicture(Picture pictureObj) throws Throwable {
        log.debug("No overriding for visitPicture: {}", pictureObj);
        throw new Throwable("No overriding for pictureObj " + superClassName);
    }
}
