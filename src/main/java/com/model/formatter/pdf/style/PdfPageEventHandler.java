package com.model.formatter.pdf.style;

import com.google.common.base.MoreObjects;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.renderer.IRenderer;
import com.model.domain.style.LayoutStyle;
import com.model.domain.style.LayoutTextStyle;
import com.model.domain.style.Style;
import com.model.domain.style.constant.HorAlignment;

// Handling header and footer additions on pages
//how-to-add-header-and-footer-to-a-pdf-with-itext-7
public class PdfPageEventHandler implements IEventHandler {
    private static final int TOP_MARGIN = 10;
    private static final float DEFAULT_MARGIN = 20;

    protected Paragraph elParagraph;
    protected Document document;

    private final Style style;

    /**
     * @param elParagraph decorated paragraph
     * @param document    base pdf document
     */
    PdfPageEventHandler(
        Paragraph elParagraph,
        Document document,
        Style style
    ) {
        this.elParagraph = elParagraph;
        this.document = document;
        this.style = style;
    }

    public static PdfPageEventHandler create(
        Paragraph elParagraph,
        Document document,
        Style style
    ) {
        return new PdfPageEventHandler(elParagraph, document, style);
    }

    /**
     * Header and footer in itextpdf are drawn automatically when creating pages.
     * Passage events are registered for this
     * the corresponding sections of the pdf page when filling it out.
     * <p>
     * When events occur, a canvas is created that accepts elParagraph formatted text
     * and located at the desired coordinates.
     *
     * @param currentEvent - event type for rendering header or footer:
     *                     visiting the beginning of the page or its end
     */
    @Override
    public void handleEvent(Event currentEvent) {
        if (elParagraph == null) {
            return;
        }
        final PdfDocumentEvent docEvent = (PdfDocumentEvent) currentEvent;
        final Rectangle pageSize = docEvent.getPage().getPageSize();

        final float posX = getXPositionOnPageByMiddlePoint(pageSize);
        final float posY = getYPositionOnPageByEvent(pageSize, docEvent);

        final Canvas canvas = new Canvas(docEvent.getPage(), pageSize);
        canvas
            .showTextAligned(elParagraph, posX, posY, TextAlignment.LEFT)
            .close();
    }

    /**
     * Returns the horizontal position of the middle of the sheet,
     * counts as the width of the visible page divided by 2
     *
     * @param pageSize - pdf sheet rectangle
     * @return - x coordinate of the middle of the sheet
     */
    private float getXPositionOnPageByMiddlePoint(Rectangle pageSize) {
        HorAlignment horAlignment = null;
        if (style instanceof LayoutStyle) {
            final LayoutStyle layoutStyle = (LayoutStyle) style;
            horAlignment = layoutStyle.getHorAlignment();
        }
        if (style instanceof LayoutTextStyle) {
            final LayoutStyle layoutStyle = ((LayoutTextStyle) style).getLayoutStyle();
            horAlignment = layoutStyle.getHorAlignment();
        }
        if (HorAlignment.LEFT == horAlignment) {
            return pageSize.getLeft() + document.getLeftMargin();
        }
        if (HorAlignment.RIGHT == horAlignment) {
            return pageSize.getRight() - document.getRightMargin();
        }
        return (pageSize.getLeft() + document.getLeftMargin()
            + (pageSize.getRight() - document.getRightMargin())) / 2;
    }

    /**
     * Returns the vertical position of the header or footer of the sheet
     *
     * @param pageSize - pdf sheet rectangle
     * @param docEvent - header or footer view event
     * @return - y coordinate of the header or footer of the sheet
     */
    private float getYPositionOnPageByEvent(Rectangle pageSize, PdfDocumentEvent docEvent) {
        //TODO: correct render Footer height
        float posY = 0;
        final IRenderer paragraphRenderer = elParagraph.createRendererSubTree();
        final LayoutResult result = paragraphRenderer
            .setParent(document.getRenderer())
            .layout(
                new LayoutContext(
                    new LayoutArea(
                        1,
                        new Rectangle(100000f, 100000f)
                    )
                )
            );
        final float height = result.getOccupiedArea().getBBox().getHeight();
        if (PdfDocumentEvent.START_PAGE.equals(docEvent.getType())) {
            document.setMargins(height, DEFAULT_MARGIN, DEFAULT_MARGIN, DEFAULT_MARGIN);
            posY = pageSize.getTop() - document.getTopMargin() - height + TOP_MARGIN;
        } else if (PdfDocumentEvent.END_PAGE.equals(docEvent.getType())) {
            document.setMargins(DEFAULT_MARGIN, DEFAULT_MARGIN, height, DEFAULT_MARGIN);
            posY = document.getBottomMargin() - height + TOP_MARGIN;
        }
        return posY;
    }

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("elParagraph", elParagraph)
                .add("document", document)
                .toString();
    }

    public Paragraph getElParagraph() {
        return elParagraph;
    }

    public void setElParagraph(Paragraph elParagraph) {
        this.elParagraph = elParagraph;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
