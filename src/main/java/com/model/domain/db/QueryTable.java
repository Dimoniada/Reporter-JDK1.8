package com.model.domain.db;

import com.google.common.base.MoreObjects;
import com.model.domain.Table;
import com.model.domain.TableCell;
import com.model.domain.TableHeaderCell;
import com.model.domain.TableHeaderRow;
import com.model.domain.TableRow;
import com.model.formatter.FormatterVisitor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class for mapping a data from DB to an intermediate representation of a table
 * <p>
 * if {@link QueryTable#isTableHeaderRowFromData} is set:
 * TableHeaderRow is formed from database data
 * <p>
 */
public class QueryTable extends Table {

    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    protected Integer fetchSize = 50;
    protected Integer rsTypeScroll = ResultSet.TYPE_FORWARD_ONLY;
    protected Integer rsTypeConcurrency = ResultSet.CONCUR_READ_ONLY;

    protected MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

    protected boolean isTableHeaderRowFromData;

    protected String query;

    /**
     * Class stores names and labels of columns in ResultSetMetaData as HashMap
     */
    public static class ColumnMetaDataMap extends HashMap<String, String> {
        public static ColumnMetaDataMap create(ResultSetMetaData resultSetMetaData, String query) throws SQLException {
            if (resultSetMetaData == null) {
                throw new IllegalStateException(String.format("There is no metadata for query: \"%s\"", query));
            }
            final ColumnMetaDataMap metaData = new ColumnMetaDataMap();
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                metaData.put(
                    resultSetMetaData.getColumnName(i),
                    resultSetMetaData.getColumnLabel(i)
                );
            }
            return metaData;
        }
    }

    public static QueryTable create() {
        return new QueryTable();
    }

    public static QueryTable create(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new QueryTable().setNamedParameterJdbcTemplate(namedParameterJdbcTemplate);
    }

    @Override
    public QueryTable accept(FormatterVisitor visitor) throws Throwable {
        if (!StringUtils.hasText(query)) {
            throw new IllegalArgumentException("Query is empty");
        }
        if (namedParameterJdbcTemplate == null) {
            throw new IllegalArgumentException("namedParameterJdbcTemplate is null");
        }
        if (isTableHeaderRowFromData) {
            final AtomicReference<ColumnMetaDataMap> metaData = new AtomicReference<>(new ColumnMetaDataMap());
            namedParameterJdbcTemplate.getJdbcTemplate().setMaxRows(1);
            final RowCallbackHandler rsLambdaMeta =
                (ResultSet rs) -> metaData.set(
                    ColumnMetaDataMap.create(
                        rs.getMetaData(),
                        query
                    )
                );
            namedParameterJdbcTemplate.query(query, mapSqlParameterSource, rsLambdaMeta);
            tableHeaderRow = generateTableHeaderRow(metaData.get());
        }

        namedParameterJdbcTemplate.getJdbcTemplate().setMaxRows(0);
        namedParameterJdbcTemplate.getJdbcTemplate().setFetchSize(fetchSize);
        final RowCallbackHandler rsLambdaWork =
            rs ->
                addPart(
                    resultSetToTableRow(
                        rs,
                        getTableHeaderRow().orElse(null)
                    )
                );
        namedParameterJdbcTemplate.query(query, mapSqlParameterSource, rsLambdaWork);

        super.accept(visitor);
        return this;
    }

    private TableHeaderRow generateTableHeaderRow(ColumnMetaDataMap columnMetaDataMap) {
        final TableHeaderRow thr = TableHeaderRow.create();
        columnMetaDataMap
            .forEach((k, v) -> thr.addPart(
                TableHeaderCell
                    .create(v)
                    .setAliasName(k)
            ));
        return thr;
    }

    private TableRow resultSetToTableRow(ResultSet rs, TableHeaderRow thr) throws SQLException {
        final TableRow tableRow = TableRow.create();
        if (thr == null) {
            return tableRow;
        }
        int i = 0;
        for (final TableHeaderCell hc : thr.getParts()) {
            i++;
            final TableCell tableCell;
            if (hc.getAliasName().isEmpty()) {
                //TODO case when rs.getObject() is a picture
                tableCell = TableCell.create(rs.getObject(i, String.class));
            } else {
                final String columnName =
                    isTableHeaderRowFromData
                        ? hc.getText()
                        : hc.getAliasName();
                //TODO case when rs.getObject() is a picture
                tableCell = TableCell.create(rs.getObject(columnName, String.class));
            }
            tableRow.addPart(tableCell);
        }
        return tableRow;
    }

    @Override
    public QueryTable setTableHeaderRow(TableHeaderRow tableHeaderRow) {
        super.setTableHeaderRow(tableHeaderRow);
        return this;
    }

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("namedParameterJdbcTemplate", namedParameterJdbcTemplate)
                .add("fetchSize", fetchSize)
                .add("rsTypeScroll", rsTypeScroll)
                .add("rsTypeConcurrency", rsTypeConcurrency)
                .add("mapSqlParameterSource", mapSqlParameterSource)
                .add("isTableHeaderRowFromData", isTableHeaderRowFromData)
                .add("query", query)
                .toString();
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return namedParameterJdbcTemplate;
    }

    public QueryTable setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        return this;
    }

    public Integer getFetchSize() {
        return fetchSize;
    }

    public QueryTable setFetchSize(Integer fetchSize) {
        this.fetchSize = fetchSize;
        return this;
    }

    public Integer getRsTypeScroll() {
        return rsTypeScroll;
    }

    public QueryTable setRsTypeScroll(Integer rsTypeScroll) {
        this.rsTypeScroll = rsTypeScroll;
        return this;
    }

    public Integer getRsTypeConcurrency() {
        return rsTypeConcurrency;
    }

    public QueryTable setRsTypeConcurrency(Integer rsTypeConcurrency) {
        this.rsTypeConcurrency = rsTypeConcurrency;
        return this;
    }

    public MapSqlParameterSource getMapSqlParameterSource() {
        return mapSqlParameterSource;
    }

    public QueryTable setMapSqlParameterSource(MapSqlParameterSource mapSqlParameterSource) {
        this.mapSqlParameterSource = mapSqlParameterSource;
        return this;
    }

    public boolean isTableHeaderRowFromData() {
        return isTableHeaderRowFromData;
    }

    public QueryTable setTableHeaderRowFromData(boolean isTableHeaderRowFromData) {
        this.isTableHeaderRowFromData = isTableHeaderRowFromData;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public QueryTable setQuery(String query) {
        this.query = query;
        return this;
    }
}
