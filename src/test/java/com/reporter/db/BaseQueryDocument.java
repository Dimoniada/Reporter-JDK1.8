package com.reporter.db;

import com.model.domain.Document;
import com.model.domain.FontService;
import com.model.domain.TableCell;
import com.model.domain.TableHeaderCell;
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
import com.model.domain.style.constant.FillPattern;
import com.model.domain.style.constant.HorAlignment;
import com.model.domain.style.constant.VertAlignment;
import com.model.utils.LocalizedNumberUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class BaseQueryDocument {
    public Document queryDoc;
    public FontService fontService;
    public TextStyle textTitleStyle;
    public LayoutTextStyle headerCellStyle;
    public LayoutTextStyle cellStyle;
    public LayoutTextStyle rowStyleNormal;
    public LayoutTextStyle rowStyleAlert;
    public LayoutTextStyle rowStyleInterlinear;

    public void initDoc() throws Exception {
        System.setProperty("spring.profiles.active", "test");

        fontService = FontService.create()
            .initializeFonts();

        final TextStyle textStyle = TextStyle.create()
            .setFontFamilyStyle(FontFamilyStyle.SERIF)
            .setFontSize((short) 10);

        textTitleStyle = textStyle
            .clone()
            .setFontFamilyStyle(FontFamilyStyle.SANS_SERIF)
            .setFontSize((short) 16)
            .setBold(true)
            .setStyleCondition(StyleCondition.create(Title.class, o -> o instanceof Title));

        final BorderStyle border = BorderStyle.create(Color.GREY_50_PERCENT, BorderWeight.DOUBLE);

        final LayoutStyle layoutStyleNormal = LayoutStyle.create()
            .setBorderTop(border)
            .setBorderLeft(border)
            .setBorderRight(border)
            .setBorderBottom(border)
            .setAutoWidth(true)
            .setFillPattern(FillPattern.SOLID_FOREGROUND)
            .setHorAlignment(HorAlignment.LEFT);

        final LayoutStyle layoutStyleInterlinear = layoutStyleNormal
            .clone()
            .setFillBackgroundColor(Color.GREEN_LIGHT)
            .setFillForegroundColor(Color.GREEN_LIGHT);

        final LayoutStyle layoutStyleAlert = layoutStyleNormal
            .clone()
            .setFillBackgroundColor(Color.RED_LIGHT)
            .setFillForegroundColor(Color.RED_LIGHT);

        headerCellStyle = LayoutTextStyle
            .create(
                textStyle
                    .clone()
                    .setFontSize((short) 11).setBold(true),
                layoutStyleNormal
                    .clone()
                    .setHorAlignment(HorAlignment.CENTER)
                    .setVertAlignment(VertAlignment.CENTER)
            )
            .setStyleCondition(StyleCondition.create(TableHeaderCell.class, o -> o instanceof TableHeaderCell));


        rowStyleNormal = LayoutTextStyle
            .create(textStyle, layoutStyleNormal)
            .setStyleCondition(
                StyleCondition.create(
                    TableRow.class, o -> o instanceof TableRow
                        && ((TableRow) o).getRowIndex() % 2 == 0
                )
            );

        rowStyleInterlinear = LayoutTextStyle
            .create(textStyle, layoutStyleInterlinear)
            .setStyleCondition(
                StyleCondition.create(
                    TableRow.class, o -> o instanceof TableRow
                        && ((TableRow) o).getRowIndex() % 2 != 0
                )
            );

        rowStyleAlert = LayoutTextStyle
            .create(textStyle, layoutStyleAlert)
            .setStyleCondition(StyleCondition.create(TableRow.class, o -> {
                if (o instanceof TableRow) {
                    final List<TableCell> cells = (ArrayList<TableCell>) ((TableRow) o).getParts();
                    if (cells.size() > 4) {
                        final TableCell cell1 = cells.get(2);
                        final TableCell cell2 = cells.get(3);
                        return StringUtils.hasText(cell1.getText())
                            && StringUtils.hasText(cell2.getText())
                            && LocalizedNumberUtils.isNumber(cell1.getText())
                            && LocalizedNumberUtils.isNumber(cell2.getText())
                            && Integer.parseInt(cell1.getText()) > Integer.parseInt(cell2.getText());
                    }
                }
                return false;
            }));

        cellStyle = LayoutTextStyle
            .create(textStyle, layoutStyleNormal.clone().setHorAlignment(HorAlignment.RIGHT))
            .setStyleCondition(StyleCondition.create(TableCell.class, o -> {
                if (o instanceof TableCell) {
                    final TableCell cell = (TableCell) o;
                    if (StringUtils.hasText(cell.getText())) {
                        final String text = cell.getText();
                        return LocalizedNumberUtils.isNumber(text) && text.length() != 11 || text.endsWith("%");
                    }
                }
                return false;
            }));

        queryDoc = Document.create()
            .setLabel("doc2")
            .addPart(Title
                .create("Мониторинг трафика 2021-05-26")
                .setStyle(textTitleStyle)
            );
    }
}