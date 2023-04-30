package com.reporter.formatter;

import com.model.domain.*;
import com.model.domain.styles.*;
import com.model.domain.styles.constants.BorderWeight;
import com.model.domain.styles.constants.Color;
import com.model.domain.styles.geometry.Geometry;
import com.model.domain.styles.geometry.MeasurableValues;

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

    public ByteArrayOutputStream os = new ByteArrayOutputStream();
    public OutputStreamWriter writer = new OutputStreamWriter(os);

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
                .setMeasurable(
                    MeasurableValues.create(
                        Geometry.create().add("html", "1.2"),
                        Geometry.create().add("html", "1.7"),
                        Geometry.create().add("html", "359")
                    )
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
                                TableHeaderCell.create().setText("column1").setStyle(layoutTextStyle),
                                TableHeaderCell.create().setText("column2 (столбец2)").setStyle(layoutTextStyle)
                            )
                    )
                    .addParts(
                        TableRow
                            .create().addParts(
                                TableCell.create().setText("1"),
                                TableCell.create().setText("2")
                            ),
                        TableRow
                            .create().addParts(
                                TableCell.create().setText("3"),
                                TableCell.create().setText("4")
                            ),
                        TableRow
                            .create().addParts(
                                TableCell.create().setText("5"),
                                TableCell.create().setText("6")
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
                                TableHeaderCell.create().setText("Column 1"),
                                TableHeaderCell.create().setText("Column 2")
                            )
                            .spreadStyleToParts(layoutTextStyle)
                    )
                    .addParts(
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create().setText("Cell 1.1"),
                                TableCell.create().setText("Cell 1.2")
                            ),
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create().setText("Cell 2.1"),
                                TableCell.create().setText("Cell 2.2")
                            ),
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create().setText("Cell 3.1"),
                                TableCell.create().setText("Cell 3.2")
                            ),
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create().setText("Cell 4.1"),
                                TableCell.create().setText("Cell 4.2")
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
                                TableHeaderCell.create().setText("Column 1"),
                                TableHeaderCell.create().setText("Column 2")
                            )
                            .spreadStyleToParts(layoutTextStyle)
                    )
                    .addParts(
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create().setText("Cell 1.1"),
                                TableCell.create().setText("Cell 1.2")
                            ),
                        TableRow
                            .create()
                            .addParts(
                                TableCell.create().setText("Cell 2.1"),
                                TableCell.create().setText("Cell 2.2")
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
                                TableHeaderCell.create().setText("столбец1"),
                                TableHeaderCell.create().setText("column2")
                            )
                    )
                    .addParts(
                        TableRow
                            .create().addParts(
                                TableCell.create().setText("1"),
                                TableCell.create().setText("2")),
                        TableRow
                            .create().addParts(
                                TableCell.create().setText("3"),
                                TableCell.create().setText("4 and some escape characters (символы) %;;;;;\\/")),
                        TableRow
                            .create().addParts(
                                TableCell.create().setText("5"),
                                TableCell.create().setText("6"))
                    )
                    .spreadStyleToParts(textStyleCell)
            );
    }

}
