package com.model.formatter.html;

import com.google.common.base.MoreObjects;
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
import com.model.domain.core.CompositionPart;
import com.model.domain.core.DataItem;
import com.model.domain.core.PictureItem;
import com.model.domain.core.TextItem;
import com.model.domain.style.BorderStyle;
import com.model.domain.style.LayoutStyle;
import com.model.domain.style.Style;
import com.model.domain.style.StyleService;
import com.model.domain.style.constant.BorderWeight;
import com.model.domain.style.constant.Color;
import com.model.domain.style.constant.PictureFormat;
import com.model.formatter.BaseDetails;
import com.model.formatter.Formatter;
import com.model.formatter.html.style.HtmlLayoutTextStyle;
import com.model.formatter.html.style.HtmlStyleService;
import com.model.formatter.html.tag.Html;
import com.model.formatter.html.tag.HtmlBody;
import com.model.formatter.html.tag.HtmlCol;
import com.model.formatter.html.tag.HtmlColgroup;
import com.model.formatter.html.tag.HtmlDiv;
import com.model.formatter.html.tag.HtmlFooter;
import com.model.formatter.html.tag.HtmlH1;
import com.model.formatter.html.tag.HtmlHead;
import com.model.formatter.html.tag.HtmlHeading;
import com.model.formatter.html.tag.HtmlLineSeparator;
import com.model.formatter.html.tag.HtmlParagraph;
import com.model.formatter.html.tag.HtmlPicture;
import com.model.formatter.html.tag.HtmlTable;
import com.model.formatter.html.tag.HtmlTableCell;
import com.model.formatter.html.tag.HtmlTableHeaderCell;
import com.model.formatter.html.tag.HtmlTableRow;
import com.model.formatter.html.tag.HtmlTag;
import com.model.formatter.html.tag.HtmlTitle;
import com.model.utils.PictureUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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
        tagCreator = getTagCreator();
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
        final Style documentStyle = styleService.prepareStyleFrom(documentObj);
        final HtmlStyleService htmlStyleService = (HtmlStyleService) styleService;
        tagCreator.writeTag(
            htmlBody,
            null,
            documentStyle,
            htmlStyleService.isUseHtml4Tags(),
            htmlStyleService.contains(documentStyle) && !htmlStyleService.isWriteStyleInTag(),
            false,
            false
        );
        this.visitComposition(documentObj);
        tagCreator.write(htmlBody.close());
        tagCreator.write(html.close());
        outputStreamWriter.close();
    }

    @Override
    public void visitDocumentCase(DocumentCase documentCase) throws Throwable {
        this.visitComposition(documentCase);
        final Style style = styleService.prepareStyleFrom(documentCase);
        if (style == null) {
            return;
        }
        final HtmlStyleService htmlStyleService = (HtmlStyleService) styleService;
        getTagCreator().writeTag(
            new HtmlDiv(),
            documentCase.getName(),
            style,
            htmlStyleService.isUseHtml4Tags(),
            !htmlStyleService.isWriteStyleInTag(),
            false,
            true
        );
    }

    @Override
    public void visitTitle(Title titleObj) throws Exception {
        final HtmlH1 htmlTitle = new HtmlH1();
        final Style style = styleService.extractStyleFor(titleObj).orElse(titleObj.getStyle());
        handleTag(htmlTitle, titleObj.getText(), style, true);
    }

    @Override
    public void visitHeading(Heading headingObj) throws Exception {
        final HtmlHeading htmlHeading = new HtmlHeading(headingObj.getDepth());
        final Style style = styleService.extractStyleFor(headingObj).orElse(headingObj.getStyle());
        handleTag(htmlHeading, headingObj.getText(), style, true);
    }

    @Override
    public void visitParagraph(Paragraph paragraphObj) throws Exception {
        final HtmlParagraph htmlParagraph = new HtmlParagraph();
        final Style style = styleService.extractStyleFor(paragraphObj).orElse(paragraphObj.getStyle());
        handleTag(htmlParagraph, paragraphObj.getText(), style, true);
    }

    @Override
    public void visitPicture(Picture pictureObj) throws Exception {
        final HtmlPicture htmlPicture = new HtmlPicture();
        htmlPicture.setSrc(pictureObj.getData());
        final Style style = styleService.extractStyleFor(pictureObj).orElse(pictureObj.getStyle());
        handleTag(htmlPicture, null, style, true);
    }

    @Override
    public void visitTable(Table tableObj) throws Throwable {
        final HtmlTable htmlTable = new HtmlTable();
        Style style = styleService.extractStyleFor(tableObj).orElse(tableObj.getStyle());
        final HtmlStyleService htmlStyleService = (HtmlStyleService) styleService;
        final boolean isUseHtml4Tags = htmlStyleService.isUseHtml4Tags();
        if (isUseHtml4Tags && style == null) {
            style = LayoutStyle.create().setBorderBottom(BorderStyle.create(Color.BLACK, BorderWeight.THIN));
        }
        handleTag(htmlTable, tableObj.getLabel(), style, false);

        if (tableObj.getTableHeaderRow().isPresent()) {
            visitTableHeaderRow(tableObj.getTableHeaderRow().get());
        }

        this.visitComposition(tableObj);
        outputStreamWriter.write(htmlTable.close());
    }

    @Override
    public void visitTableHeaderRow(TableHeaderRow tableHeaderRowObj) throws Throwable {
        final List<HtmlLayoutTextStyle> htmlStyles = new ArrayList<>();
        tableHeaderRowObj
            .getParts()
            .forEach(thc -> {
                final Style style = styleService.extractStyleFor(thc).orElse(thc.getStyle());
                if (style instanceof HtmlLayoutTextStyle) {
                    final HtmlLayoutTextStyle htmlStyle = (HtmlLayoutTextStyle) style;
                    if (htmlStyle.isColGroupStyle()) {
                        htmlStyles.add(htmlStyle);
                    }
                }
            });
        final boolean isNeedColGroupTag = !htmlStyles.isEmpty();
        if (isNeedColGroupTag) {
            final HtmlColgroup htmlColgroup = new HtmlColgroup();
            outputStreamWriter.write(htmlColgroup.open());
            for (final HtmlLayoutTextStyle htmlStyle : htmlStyles) {
                handleTag(new HtmlCol(), "", htmlStyle, true);
            }
            outputStreamWriter.write(htmlColgroup.close());
        }
        visitRow(tableHeaderRowObj);
    }

    @Override
    public void visitTableRow(TableRow tableRowObj) throws Throwable {
        styleService.extractStyleFor(tableRowObj);
        visitRow(tableRowObj);
    }

    @Override
    public void visitTableHeaderCell(TableHeaderCell tableHeaderCellObj) throws Throwable {
        final HtmlTableHeaderCell htmlTableHeaderCell = new HtmlTableHeaderCell();
        handleCustomCell(tableHeaderCellObj, htmlTableHeaderCell);
    }

    @Override
    public void visitTableCell(TableCell tableCellObj) throws Exception {
        final HtmlTableCell htmlTableCell = new HtmlTableCell();
        handleCustomCell(tableCellObj, htmlTableCell);
    }

    @Override
    public void visitLineSeparator(LineSeparator lineSeparatorObj) throws Exception {
        final HtmlLineSeparator htmlLineSeparator = new HtmlLineSeparator();
        final Style style = LayoutStyle.create().setBorderBottom(lineSeparatorObj.getBorderStyle());
        handleTag(htmlLineSeparator, null, style, false);
    }

    @Override
    public void visitFooter(Footer footerObj) throws Exception {
        final HtmlFooter htmlFooter = new HtmlFooter();
        if (footerObj.isDataInheritedFrom(TextItem.class)) {
            final Style style = styleService.extractStyleFor(footerObj).orElse(footerObj.getStyle());
            handleTag(htmlFooter, footerObj.getText(), style, true);
        }
        if (footerObj.isDataInheritedFrom(PictureItem.class)) {
            final Style pictureStyle = footerObj.getStyle();
            final Style footerStyle = styleService.extractStyleFor(footerObj).orElse(null);
            handlePictureInTag(footerObj, htmlFooter, footerStyle, pictureStyle);
        }
    }

    protected void handleCustomCell(DataItem cellObj, HtmlTag htmlCell) throws Exception {
        final HtmlDiv htmlDiv = new HtmlDiv();
        if (htmlCell instanceof HtmlTableHeaderCell) {
            htmlDiv.setRealDomainClazz(TableHeaderCell.class);
        }
        if (htmlCell instanceof HtmlTableCell) {
            htmlDiv.setRealDomainClazz(TableCell.class);
        }
        final HtmlStyleService htmlStyleService = (HtmlStyleService) styleService;
        if (cellObj.isDataInheritedFrom(TextItem.class)) {
            final Style cellStyle = htmlStyleService.getCustomTableCellStyle(cellObj);
            final Style cellDivStyle = htmlStyleService.getCustomTableCellDivStyle(htmlDiv);
            if (cellDivStyle != null) {
                handleTag(htmlCell, null, cellStyle, false);
                handleTag(htmlDiv, cellObj.getText(), cellDivStyle, true);
                outputStreamWriter.write(htmlCell.close());
            } else {
                handleTag(htmlCell, cellObj.getText(), cellStyle, true);
            }
        }
        if (cellObj.isDataInheritedFrom(PictureItem.class)) {
            final Style pictureStyle = cellObj.getStyle();
            final Style cellStyle = htmlStyleService.extractStyleFor(cellObj).orElse(null);
            handlePictureInTag(cellObj, htmlCell, cellStyle, pictureStyle);
        }
    }

    protected void handlePictureInTag(DataItem item, HtmlTag htmlTag, Style tagStyle, Style pictureStyle)
        throws Exception {
        final byte[] data = item.getData();
        final PictureFormat pictureFormat = PictureUtils.getFormat(data);
        final Picture picture = Picture.create(data, pictureFormat).setStyle(pictureStyle);
        final Style preparedPictureStyle = styleService.prepareStyleFrom(picture);
        final HtmlPicture htmlPicture = new HtmlPicture();
        htmlPicture.setSrc(picture.getData());
        handleTag(htmlTag, null, tagStyle, false);
        handleTag(htmlPicture, null, preparedPictureStyle, true);
        outputStreamWriter.write(htmlTag.close());
    }

    protected void handleTag(HtmlTag tag, String text, Style style, Boolean needCloseTag) throws Exception {
        final HtmlStyleService htmlStyleService = (HtmlStyleService) styleService;
        final boolean isBordersCollapse =
            style instanceof HtmlLayoutTextStyle
                && ((HtmlLayoutTextStyle) style).isBordersCollapse();
        getTagCreator()
            .writeTag(
                tag,
                text,
                style,
                htmlStyleService.isUseHtml4Tags(),
                htmlStyleService.contains(style) && !htmlStyleService.isWriteStyleInTag(),
                isBordersCollapse,
                needCloseTag
            );
    }

    protected void visitRow(CompositionPart<?, ?> row) throws Throwable {
        final HtmlTableRow htmlTableRow = new HtmlTableRow();
        outputStreamWriter.write(htmlTableRow.open());
        this.visitComposition(row);
        outputStreamWriter.write(htmlTableRow.close());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("outputStreamWriter", outputStreamWriter)
            .add("encoding", encoding)
            .add("locale", locale)
            .add("decimalFormat", decimalFormat)
            .add("styleService", styleService)
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

    @Override
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

    public TagCreator getTagCreator() {
        if (tagCreator == null) {
            tagCreator = new TagCreator(outputStreamWriter, decimalFormat);
        }
        return tagCreator;
    }
}
