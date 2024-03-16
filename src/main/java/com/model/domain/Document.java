package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.domain.core.CompositionPart;
import com.model.domain.core.DocumentItem;
import com.model.formatter.FormatterVisitor;

import java.time.Instant;
import java.util.Arrays;

/**
 * Class {@link Document Document} contains meta-information about the document
 */
public class Document extends CompositionPart<Document, DocumentItem> {
    /**
     * Document's name
     */
    protected String label;
    /**
     * Author of the document
     */
    protected String author;
    /**
     * Document subject
     */
    protected String description;
    /**
     * Document creation date
     */
    protected Instant creationDateTime = Instant.now();

    public static Document create() {
        return new Document();
    }

    public static Document create(DocumentItem... parts) {
        return new Document().setParts(Arrays.asList(parts));
    }

    @Override
    public Document accept(FormatterVisitor visitor) throws Throwable {
        visitor.visitDocument(this);
        return this;
    }

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("label", label)
                .add("author", author)
                .add("description", description)
                .add("creationDateTime", creationDateTime)
                .toString();
    }

    public String getLabel() {
        return label;
    }

    public Document setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public Document setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Document setDescription(String description) {
        this.description = description;
        return this;
    }

    public Instant getCreationDateTime() {
        return creationDateTime;
    }

    public Document setCreationDateTime(Instant creationDateTime) {
        this.creationDateTime = creationDateTime;
        return this;
    }
}

