package com.reporter.formatter;

import com.model.domain.Document;
import com.model.domain.FontService;
import com.model.domain.Heading;
import com.model.domain.Paragraph;
import com.model.domain.Separator;
import com.model.domain.Table;
import com.model.domain.TableCell;
import com.model.domain.TableHeaderCell;
import com.model.domain.TableHeaderRow;
import com.model.domain.TableRow;
import com.model.domain.Title;
import com.model.domain.DocumentCase;
import com.model.domain.style.BorderStyle;
import com.model.domain.style.FontFamilyStyle;
import com.model.domain.style.LayoutStyle;
import com.model.domain.style.LayoutTextStyle;
import com.model.domain.style.StyleCondition;
import com.model.domain.style.TextStyle;
import com.model.domain.style.constant.BorderWeight;
import com.model.domain.style.constant.Color;
import com.model.domain.style.geometry.Geometry;
import com.model.domain.style.geometry.GeometryDetails;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

public class BaseDocument {

    public Document doc;
    public TextStyle textStyle1;
    public TextStyle textStyleCell;
    public LayoutStyle layoutStyle1;
    public LayoutStyle layoutStyle2;
    public LayoutTextStyle layoutTextStyle;
    public LayoutTextStyle styleForHeading;
    public LayoutTextStyle styleForParagraph;
    public LayoutTextStyle styleForSeparator;

    public final ByteArrayOutputStream os = new ByteArrayOutputStream();
    public final OutputStreamWriter writer = new OutputStreamWriter(os);

    public FontService fontService;

    public void initDoc() throws Exception {

        final BorderStyle border = BorderStyle.create(Color.BLACK, BorderWeight.DOUBLE);

        layoutStyle1 =
            LayoutStyle
                .create()
                .setBorderTop(border)
                .setBorderLeft(border)
                .setBorderRight(border)
                .setBorderBottom(border)
                .setAutoWidth(true)
                .setGeometryDetails(
                    GeometryDetails.create()
                        .setWidth(Geometry.create().add("html", "20px"))
                        .setHeight(Geometry.create().add("html", "15px"))
                        .setAngle(Geometry.create().add("html", "10deg"))
                );

        textStyle1 =
            TextStyle
                .create()
                .setFontSize((short) 14)
                .setBold(true)
                .setFontFamilyStyle(FontFamilyStyle.SANS_SERIF)
                .setFontNameResource("arial");

        layoutTextStyle = LayoutTextStyle.create(textStyle1, layoutStyle1);

        layoutStyle2 =
            LayoutStyle
                .create()
                .setAutoWidth(true);

        textStyleCell =
            TextStyle
                .create()
                .setFontSize((short) 10)
                .setBold(true)
                .setFontFamilyStyle(FontFamilyStyle.MONOSPACED)
                .setFontNameResource("courierNew")
                .setCondition(StyleCondition.create(TableCell.class));

        styleForHeading =
            LayoutTextStyle
                .create(textStyleCell, layoutStyle2)
                .setCondition(StyleCondition.create(Heading.class));

        styleForParagraph =
            LayoutTextStyle
                .create(textStyleCell, layoutStyle2)
                .setCondition(StyleCondition.create(Paragraph.class));

        styleForSeparator =
            LayoutTextStyle
                .create(textStyleCell, layoutStyle2)
                .setCondition(StyleCondition.create(Separator.class));

        fontService = FontService.
            create()
            .initializeFonts();

        final DocumentCase documentCase = DocumentCase.create().setName("Test sheet1")
            .addParts(
                Title.create().setText("Title 1"),
                Paragraph.create().setText("paragraph 1"),
                Table
                    .create()
                    .setTableHeaderRow(
                        TableHeaderRow
                            .create().addParts(
                                TableHeaderCell.create("column1").setStyle(layoutTextStyle),
                                TableHeaderCell.create("column2 (столбец2)").setStyle(layoutTextStyle)
                            )
                    )
                    .addParts(
                        TableRow
                            .create().addParts(
                                TableCell.create("1"),
                                TableCell.create("2")
                            ),
                        TableRow
                            .create().addParts(
                                TableCell.create("3"),
                                TableCell.create("4")
                            ),
                        TableRow
                            .create().addParts(
                                TableCell.create("5"),
                                TableCell.create("6")
                            )
                    )
                    .spreadStyleToParts(textStyle1)
            );

        doc = Document
            .create()
            .setLabel("Test document")
            .setAuthor("A1 Systems")
            .setDescription("meta information")
            .addPart(documentCase)
            .addParts(
                Title.create().setText("Test document v.1").setStyle(TextStyle.create().setFontSize((short) 20)),
                Separator.create(BorderStyle.create(Color.TEAL, BorderWeight.THIN)),
                Heading.create(1).setText("Chapter 1"),
                Heading.create(2).setText("Chapter 1.1"),
                Heading.create(3).setText("Chapter 1.1.1"),
                Paragraph.create().setText("This is an example of text in paragraph"),
                Table
                    .create()
                    .setTableHeaderRow(
                        TableHeaderRow
                            .create()
                            .addParts(
                                TableHeaderCell.create("Column 1"),
                                TableHeaderCell.create("Column 2")
                            )
                            .spreadStyleToParts(layoutTextStyle)
                    )
                    .addParts(
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create("Cell 1.1"),
                                TableCell.create("Cell 1.2")
                            ),
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create("Cell 2.1"),
                                TableCell.create("Cell 2.2")
                            ),
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create("Cell 3.1"),
                                TableCell.create("Cell 3.2")
                            ),
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create("Cell 4.1"),
                                TableCell.create("Cell 4.2")
                            )
                    )
                    .spreadStyleToParts(textStyleCell),
                Heading.create(1).setText("Chapter 2"),
                Heading.create(2).setText("Chapter 2.1"),
                Heading.create(3).setText("Chapter 2.1.1"),
                Paragraph.create().setText("This is an example of text in paragraph 2"),
                Table
                    .create()
                    .setTableHeaderRow(
                        TableHeaderRow
                            .create()
                            .addParts(
                                TableHeaderCell.create("Column 1"),
                                TableHeaderCell.create("Column 2")
                            )
                            .spreadStyleToParts(layoutTextStyle)
                    )
                    .addParts(
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create("Cell 1.1"),
                                TableCell.create("Cell 1.2")
                            ),
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create("Cell 2.1"),
                                TableCell.create("Cell 2.2")
                            )
                    )
                    .spreadStyleToParts(textStyleCell)
            )
            .spreadStyleToParts(styleForHeading)
            .spreadStyleToParts(styleForParagraph)
            .spreadStyleToParts(styleForSeparator)
            .addParts(
                Title.create().setText("Title 1"),
                Paragraph.create().setText("paragraph 1"),
                Heading.create(2).setText("shifted heading"),
                Table
                    .create()
                    .setTableHeaderRow(
                        TableHeaderRow
                            .create().addParts(
                                TableHeaderCell.create("столбец1"),
                                TableHeaderCell.create("column2")
                            )
                    )
                    .addParts(
                        TableRow
                            .create().addParts(
                                TableCell.create("1"),
                                TableCell.create("2")
                            ),
                        TableRow
                            .create().addParts(
                                TableCell.create("3"),
                                TableCell.create("4 and some escape characters (символы) %;;;;;\\/")
                            ),
                        TableRow
                            .create().addParts(
                                TableCell.create("5"),
                                TableCell.create("6")
                            )
                    )
                    .spreadStyleToParts(textStyleCell)
            );
    }

}
