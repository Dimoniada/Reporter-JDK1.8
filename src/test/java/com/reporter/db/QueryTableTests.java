package com.reporter.db;

import com.ReporterApplication;
import com.model.domain.Document;
import com.model.domain.DocumentCase;
import com.model.domain.Heading;
import com.model.domain.Paragraph;
import com.model.domain.ReportTable;
import com.model.domain.Table;
import com.model.domain.TableCell;
import com.model.domain.TableHeaderCell;
import com.model.domain.TableHeaderRow;
import com.model.domain.TableRow;
import com.model.domain.Title;
import com.model.domain.db.QueryTable;
import com.model.domain.style.BorderStyle;
import com.model.domain.style.LayoutStyle;
import com.model.domain.style.StyleCondition;
import com.model.domain.style.constant.BorderWeight;
import com.model.domain.style.constant.Color;
import com.model.formatter.DocumentHolder;
import com.model.formatter.csv.CsvFormatter;
import com.model.formatter.excel.XlsxFormatter;
import com.model.formatter.excel.styles.ExcelStyleService;
import org.apache.commons.io.FileUtils;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.nio.charset.Charset;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Sql("classpath:db/h2/schema_query_table_test.sql")
@SpringBootTest(classes = {ReporterApplication.class})
public class QueryTableTests extends BaseQueryDocument {
    @Autowired
    public NamedParameterJdbcTemplate jdbcTemplateH2;

    private static class ReportClass {
        protected String login = "qwe";
        protected String client_name = "qweqwe";
        protected LocalDate date1_amount = LocalDate.now(Clock.systemDefaultZone()).minusDays(1);
        protected LocalDate date2_amount = LocalDate.now(Clock.systemDefaultZone());
        protected Integer abs_diff = 123;
        protected String rel_diff_percent = "12%";
        protected String username = "my_name";
        protected String password = "my_password";
    }

    @BeforeEach
    public void initDoc() throws Exception {
        super.initDoc();
    }

    @Test
    public void testComplicatedDocument() throws Throwable {
        final BorderStyle doubleBorder = BorderStyle.create(Color.GREY_50_PERCENT, BorderWeight.DOUBLE);
        final LayoutStyle headerCellStyle = LayoutStyle.create()
            .setBorderTop(doubleBorder)
            .setBorderLeft(doubleBorder)
            .setBorderRight(doubleBorder)
            .setBorderBottom(doubleBorder)
            .setAutoWidth(true);

        final BorderStyle singleBorder = BorderStyle.create(Color.RED_DARK, BorderWeight.DOTTED);
        final LayoutStyle cellStyle = LayoutStyle.create()
            .setBorderTop(singleBorder)
            .setBorderLeft(singleBorder)
            .setBorderRight(singleBorder)
            .setBorderBottom(singleBorder)
            .setCondition(StyleCondition.create(TableCell.class, null));

        final QueryTable queryTable = QueryTable.create()
            .setQuery(
                " select login as _login_," +
                    " client_name as _client_name_," +
                    " date1_amount as _date1_amount_," +
                    " date2_amount as _date2_amount_," +
                    " abs_diff as _abs_diff_," +
                    " rel_diff_percent as _rel_diff_percent_," +
                    " username as _username_," +
                    " password as _password_" +
                    " from traffic_mon left join site_users on site_users.id = user_id;"
            )
            .setNamedParameterJdbcTemplate(jdbcTemplateH2)
            .setTableHeaderRow(
                TableHeaderRow.create().addParts(
                        TableHeaderCell.create().setText("Login")
                            .setAliasName("_login_"),
                        TableHeaderCell.create().setText("Conventional name of the client")
                            .setAliasName("_client_name_"),
                        TableHeaderCell.create().setText("14.03.2021")
                            .setAliasName("_date1_amount_"),
                        TableHeaderCell.create().setText("15.03.2021")
                            .setAliasName("_date2_amount_"),
                        TableHeaderCell.create().setText("Абсолютное изменение")
                            .setAliasName("_abs_diff_"),
                        TableHeaderCell.create().setText("Relative change")
                            .setAliasName("_rel_diff_percent_"),
                        TableHeaderCell.create().setText("User name")
                            .setAliasName("_username_"),
                        TableHeaderCell.create().setText("User password")
                            .setAliasName("_password_")
                    )
                    .spreadStyleToParts(headerCellStyle)
            );
        final ReportTable reportTable = ReportTable.create()
            .addDataList(Arrays.asList(new ReportClass(), new ReportClass()))
            .setTableHeaderRowFromData(true);

        final Table simpleTable = Table.create()
            .addPart(TableRow.create(
                TableCell.create("qwe2"),
                TableCell.create("qweqwe2"),
                TableCell.create(LocalDate.now().minusDays(2).toString()),
                TableCell.create(LocalDate.now().minusDays(1).toString()),
                TableCell.create("124"),
                TableCell.create("13%"),
                TableCell.create("my_name2"),
                TableCell.create("my_password2")
            ));

        final ExcelStyleService styleService = ExcelStyleService.create(FontCharset.DEFAULT, null);
        styleService.addStyles(cellStyle);

        final Document doc = Document
            .create()
            .setLabel("doc2")
            .addPart(
                DocumentCase
                    .create()
                    .setName("Sheet with table")
                    .addParts(queryTable, reportTable, simpleTable)
            );

        final XlsxFormatter xlsxFormatter = XlsxFormatter.create().setStyleService(styleService);
        try (DocumentHolder documentHolder = xlsxFormatter.handle(doc)) {
            final Workbook wb = WorkbookFactory.create(documentHolder.getResource().getFile());
            final Sheet sheet = wb.getSheetAt(0);
            final String actual = sheet.getRow(4).getCell(4).getStringCellValue();
            wb.close();
            Assertions.assertEquals("124", actual);
        }
    }

    @Test
    public void testSaveQueryTableResultToFile() throws Throwable {

        final QueryTable queryTable = QueryTable.create()
            .setNamedParameterJdbcTemplate(jdbcTemplateH2)
            .setTableHeaderRowFromData(true)
            .setQuery(
                "    select \"login\",\n" +
                    "    \"client_name\",\n" +
                    "    \"date1_amount\",\n" +
                    "    \"date2_amount\",\n" +
                    "    \"abs_diff\",\n" +
                    "    \"rel_diff_percent\",\n" +
                    "    \"user_id\" from \"traffic_mon\";"
            );

        final Document doc = Document
            .create()
            .setLabel("doc1")
            .addParts(
                Title.create().setText("Title 1"),
                Paragraph.create().setText("paragraph 1"),
                Heading.create(2).setText("shifted heading"),
                queryTable
            );
        final Set<String> expected =
            new HashSet<String>() {{
                add("\"\"\"ООО \"\"\"\"А + А Эксист-Инфо\"\"\"\"\"\"\"");
                add("ООО Стэй ИН Тач");
                add("client5");
                add("0");
                add("-2");
                add("-100%");
                add("79960174254");
                add("1");
            }};
        final CsvFormatter csvFormatter = new CsvFormatter();

        csvFormatter.setEncoding("windows-1251");
        try (DocumentHolder documentHolder = csvFormatter.handle(doc)) {
            final String text =
                FileUtils.readFileToString(documentHolder.getResource().getFile(), Charset.forName("windows-1251"));
            Assertions.assertTrue(expected.stream().allMatch(text::contains));
        }
    }
}
