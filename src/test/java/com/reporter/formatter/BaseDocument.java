package com.reporter.formatter;

import com.model.domain.Document;
import com.model.domain.DocumentCase;
import com.model.domain.FontService;
import com.model.domain.Heading;
import com.model.domain.LineSeparator;
import com.model.domain.Paragraph;
import com.model.domain.Table;
import com.model.domain.TableCell;
import com.model.domain.TableHeaderCell;
import com.model.domain.TableHeaderRow;
import com.model.domain.TableRow;
import com.model.domain.Title;
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class BaseDocument {

    public Document doc;
    public TextStyle textStyle1;
    public TextStyle textStyle2;
    public TextStyle textStyleCell;
    public LayoutStyle layoutStyle1;
    public LayoutStyle layoutStyle2;
    public LayoutTextStyle layoutTextStyle1;
    public LayoutTextStyle layoutTextStyle2;
    public LayoutTextStyle styleForHeading;
    public LayoutTextStyle styleForParagraph;
    public LayoutTextStyle styleForSeparator;

    public final ByteArrayOutputStream os = new ByteArrayOutputStream();
    public final OutputStreamWriter writer = new OutputStreamWriter(os);

    public FontService fontService;

    public void initDoc() throws Exception {

        final BorderStyle doubleBorder = BorderStyle.create(Color.BLACK, BorderWeight.DOUBLE);
        final BorderStyle singleBorder = BorderStyle.create(Color.RED, BorderWeight.THICK);

        layoutStyle1 =
            LayoutStyle.create()
                .setBorderTop(doubleBorder)
                .setBorderLeft(doubleBorder)
                .setBorderRight(doubleBorder)
                .setBorderBottom(doubleBorder)
                .setAutoWidth(true)
                .setGeometryDetails(
                    GeometryDetails.create()
                        .setWidth(Geometry.create().add("html", "20px"))
                        .setHeight(Geometry.create().add("html", "15px"))
                        .setAngle(Geometry.create().add("html", "10deg"))
                );

        layoutStyle2 =
            LayoutStyle.create()
                .setBorderTop(singleBorder)
                .setBorderLeft(singleBorder)
                .setBorderRight(singleBorder)
                .setBorderBottom(singleBorder)
                .setAutoWidth(true);

        textStyle1 =
            TextStyle.create()
                .setFontSize((short) 14)
                .setBold(true)
                .setFontFamilyStyle(FontFamilyStyle.SANS_SERIF)
                .setFontNameResource("arial");

        final DecimalFormat df = new DecimalFormat("0.000");
        final DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(symbols);

        textStyle2 =
            textStyle1
                .clone()
                .setBold(false)
                .setDecimalFormat(df);

        layoutTextStyle1 = LayoutTextStyle.create(textStyle1, layoutStyle1);
        layoutTextStyle2 = LayoutTextStyle.create(textStyle2, layoutStyle2);

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
                .setStyleCondition(StyleCondition.create(TableCell.class));

        styleForHeading =
            LayoutTextStyle
                .create(textStyleCell, layoutStyle2)
                .setStyleCondition(StyleCondition.create(Heading.class));

        styleForParagraph =
            LayoutTextStyle
                .create(textStyleCell, layoutStyle2)
                .setStyleCondition(StyleCondition.create(Paragraph.class));

        styleForSeparator =
            LayoutTextStyle
                .create(textStyleCell, layoutStyle2)
                .setStyleCondition(StyleCondition.create(LineSeparator.class));

        fontService = FontService.
            create()
            .initializeFonts();

        final DocumentCase documentCase = DocumentCase.create()
            .addParts(
                Title.create().setText("Title 1"),
                Paragraph.create().setText("paragraph 1"),
                Table
                    .create()
                    .setTableHeaderRow(
                        TableHeaderRow
                            .create().addParts(
                                TableHeaderCell.create("column1").setStyle(layoutTextStyle1),
                                TableHeaderCell.create("column2 (столбец2)").setStyle(layoutTextStyle1)
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
                    .spreadStyleToParts(layoutTextStyle2)
            );

        doc = Document
            .create()
            .setLabel("Test document")
            .setAuthor("A1 Systems")
            .setDescription("meta information")
            .addPart(documentCase)
            .addParts(
                Title.create().setText("Test document v.1").setStyle(TextStyle.create().setFontSize((short) 20)),
                LineSeparator.create(BorderStyle.create(Color.TEAL, BorderWeight.THIN)),
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
                            .spreadStyleToParts(layoutTextStyle1)
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
                            .spreadStyleToParts(layoutTextStyle1)
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
