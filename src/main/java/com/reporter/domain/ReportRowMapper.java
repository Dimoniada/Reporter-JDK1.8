package com.reporter.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ReportRowMapper<T> implements RowMapper<T> {

    private static final Logger log = LoggerFactory.getLogger(ReportRowMapper.class);
    private final Class<T> clazz;

    public ReportRowMapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        final T mappedObject = BeanUtils.instantiateClass(this.clazz);
        final BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);
        beanWrapper.setAutoGrowNestedPaths(true);
        final ResultSetMetaData meta_data = rs.getMetaData();
        final int columnCount = meta_data.getColumnCount();
        for (int index = 1; index <= columnCount; index++) {
            try {
                final String column = JdbcUtils.lookupColumnName(meta_data, index);
                final Object value = JdbcUtils.getResultSetValue(
                        rs,
                        index,
                        Class.forName(meta_data.getColumnClassName(index))
                );
                beanWrapper.setPropertyValue(column, value);
            } catch (TypeMismatchException | NotWritablePropertyException | ClassNotFoundException e) {
                log.error("Error on map row", e);
            }
        }
        return mappedObject;
    }
}
