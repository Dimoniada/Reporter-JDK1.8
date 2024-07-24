package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.domain.core.CompositionPart;
import com.model.domain.core.DocumentItem;
import com.model.formatter.FormatterVisitor;

/**
 * This class is used to structure components within a document.
 * Can be used to separate independent document blocks: pages in multipart documents,
 * sheets in spreadsheet books, etc.
 */
public class DocumentCase extends CompositionPart<DocumentCase, DocumentItem> {

    protected String name;

    public static DocumentCase create(String name) {
        final DocumentCase sheet = new DocumentCase();
        sheet.setName(name);
        return sheet;
    }

    public static DocumentCase create() {
        return DocumentCase.create("");
    }

    @Override
    public DocumentCase accept(FormatterVisitor visitor) throws Throwable {
        visitor.visitDocumentCase(this);
        return this;
    }

    public String getName() {
        return name;
    }

    public DocumentCase setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("name", name)
                .toString();
    }
}
