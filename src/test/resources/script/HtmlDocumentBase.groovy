package script

import com.model.domain.*
import com.model.domain.style.*
import com.model.domain.style.constant.BorderWeight
import com.model.domain.style.constant.Color
import com.model.domain.style.geometry.Geometry
import com.model.domain.style.geometry.GeometryDetails
import com.model.formatter.html.style.HtmlLayoutTextStyle
import com.model.formatter.html.style.HtmlStyleService

Document doc;
TextStyle textStyle1;
TextStyle textStyleCell;
LayoutStyle layoutStyle1;
LayoutTextStyle layoutTextStyle;
LayoutStyle layoutStyleForCells;
HtmlLayoutTextStyle htmlTableStyle;
HtmlLayoutTextStyle htmlColGroupStyle;

final BorderStyle doubleBorder = BorderStyle.create(Color.BLACK, BorderWeight.DOUBLE);
final BorderStyle singleBorder = BorderStyle.create(Color.RED, BorderWeight.THICK);

layoutStyle1 =
    LayoutStyle
        .create()
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

layoutStyleForCells =
    LayoutStyle
        .create()
        .setBorderTop(singleBorder)
        .setBorderLeft(singleBorder)
        .setBorderRight(singleBorder)
        .setBorderBottom(singleBorder)
        .setAutoWidth(true)
        .setCondition(StyleCondition.create(TableCell.class));

textStyle1 =
    TextStyle
        .create()
        .setFontSize((short) 14)
        .setBold(true)
        .setFontFamilyStyle(FontFamilyStyle.SANS_SERIF)
        .setFontNameResource("arial");

layoutTextStyle = LayoutTextStyle.create(textStyle1, layoutStyle1)
    .setCondition(
        StyleCondition.create(
            TableHeaderCell.class,
            o -> {
                if (o instanceof TableHeaderCell) {
                    return o.getColumnIndex() == 1;
                }
                return false;
            }
        )
    );

textStyleCell =
    TextStyle
        .create()
        .setFontSize((short) 10)
        .setBold(true)
        .setFontFamilyStyle(FontFamilyStyle.MONOSPACED)
        .setFontNameResource("courierNew")
        .setCondition(StyleCondition.create(TableCell.class));

htmlTableStyle = HtmlLayoutTextStyle.create(true)
    .setCondition(
        StyleCondition.create(Table.class)
    );

htmlColGroupStyle = HtmlLayoutTextStyle.create(null, layoutStyleForCells)
    .setColGroupStyle(true)
    .setCondition(
        StyleCondition.create(
            TableHeaderCell.class,
            o -> {
                if (o instanceof TableHeaderCell) {
                    return o.getColumnIndex() == 0;
                }
                return false;
            }
        )
    );

final DocumentCase documentCase = DocumentCase.create().setName("Test sheet1")
    .addParts(
        Title.create().setText("Title 1"),
        Paragraph.create().setText("paragraph 1"),
        Table
            .create()
            .setTableHeaderRow(
                TableHeaderRow
                    .create().addParts(
                    TableHeaderCell.create("column1"),
                    TableHeaderCell.create("column2 (столбец2)")
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
    );

doc = Document
    .create()
    .setLabel("Test document")
    .setAuthor("A1 Systems")
    .setDescription("meta information")
    .addPart(documentCase);

final HtmlStyleService styleService = HtmlStyleService.create()
    .setWriteStyleInplace(false)
    .addStyles(htmlTableStyle, htmlColGroupStyle, layoutTextStyle);

final Map<String, Object> result = new HashMap<String, Object>();
result.put("document", doc);
result.put("styleService", styleService);
result.put("htmlTableStyle", htmlTableStyle);
result.put("htmlColGroupStyle", htmlColGroupStyle);
result.put("layoutTextStyle", layoutTextStyle);

return result;
