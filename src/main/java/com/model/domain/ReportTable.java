package com.model.domain;

import com.google.common.base.MoreObjects;
import com.model.formatter.FormatterVisitor;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Class for mapping custom items {@link ReportTable#dataItems} to an intermediate representation of a table,
 * <p>
 * if {@link ReportTable#isTableHeaderRowFromData} is set:
 * TableHeaderRow is formed from {@link ReportTable#dataItems} data
 * <p>
 */

public class ReportTable extends Table {

    protected boolean isTableHeaderRowFromData;
    /**
     * List(T) T or class instance with public fields
     * or interface @Repository methods (proxy class)
     */
    private List<?> dataItems;

    public static ReportTable create() {
        return new ReportTable();
    }

    public static ReportTable create(TableHeaderRow tableHeaderRow) {
        return new ReportTable().setTableHeaderRow(tableHeaderRow);
    }

    @Override
    public ReportTable accept(FormatterVisitor visitor) throws Throwable {
        if (dataItems != null) {
            final List<String> tableHeaderAliasNames = new ArrayList<>();
            final Optional<?> optItem = dataItems.stream().filter(Objects::nonNull).findFirst();
            if (isTableHeaderRowFromData) {
                tableHeaderRow = generateTableHeaderRow(optItem);
            }
            getTableHeaderRow().ifPresent(
                thr -> thr
                    .getParts()
                    .forEach(
                        hc -> tableHeaderAliasNames.add(hc.getAliasName())
                    )
            );
            if (optItem.isPresent()) {
                final Class<?> actualClass = optItem.get().getClass();
                for (final Object docItem : dataItems.stream().filter(Objects::nonNull).collect(Collectors.toList())) {
                    final TableRow tr = TableRow.create();
                    if (Proxy.isProxyClass(actualClass) && actualClass.getInterfaces().length > 0) {
                        final Class<?> baseInterface = actualClass.getInterfaces()[0];
                        final BeanInfo info = Introspector.getBeanInfo(baseInterface);
                        final Map<String, Method> namesMethods =
                            Arrays.stream(info.getPropertyDescriptors())
                                .collect(
                                    Collectors.toMap(
                                        PropertyDescriptor::getName,
                                        PropertyDescriptor::getReadMethod
                                    )
                                );
                        for (final String name : tableHeaderAliasNames) {
                            if (namesMethods.containsKey(name) || isTableHeaderRowFromData) {
                                final Object value = namesMethods.get(name).invoke(docItem);
                                //TODO: case when Object is a picture
                                tr.addPart(TableCell.create(value != null ? value.toString() : ""));
                            } else {
                                tr.addPart(TableCell.create(""));
                            }
                        }
                    } else {
                        final ConfigurablePropertyAccessor propAcc =
                            PropertyAccessorFactory.forDirectFieldAccess(docItem);
                        final List<String> fieldNames =
                            Arrays.stream(actualClass.getDeclaredFields())
                                .map(Field::getName)
                                .collect(Collectors.toList());
                        for (final String name : tableHeaderAliasNames) {
                            if (fieldNames.contains(name) || isTableHeaderRowFromData) {
                                final Object value = propAcc.getPropertyValue(name);
                                tr.addPart(TableCell.create(value != null ? value.toString() : ""));
                            } else {
                                tr.addPart(TableCell.create(""));
                            }
                        }
                    }
                    addPart(tr);
                }
            }
        }
        super.accept(visitor);
        return this;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private TableHeaderRow generateTableHeaderRow(Optional<?> optItem) throws IntrospectionException {
        if (optItem.isPresent()) {
            final TableHeaderRow thr = TableHeaderRow.create();
            final Consumer<String> addHeaderCell =
                name -> thr.addPart(
                    TableHeaderCell
                        .create(name)
                        .setAliasName(name)
                );
            final Class<?> actualClass = optItem.get().getClass();
            if (Proxy.isProxyClass(actualClass) && actualClass.getInterfaces().length > 0) {
                final Class<?> baseInterface = actualClass.getInterfaces()[0];
                final BeanInfo info = Introspector.getBeanInfo(baseInterface);
                for (final PropertyDescriptor pd : info.getPropertyDescriptors()) {
                    addHeaderCell.accept(pd.getName());
                }
            } else {
                for (final Field df : actualClass.getDeclaredFields()) {
                    addHeaderCell.accept(df.getName());
                }
            }
            return thr;
        }
        return null;
    }

    @Override
    public ReportTable setTableHeaderRow(TableHeaderRow tableHeaderRow) {
        super.setTableHeaderRow(tableHeaderRow);
        return this;
    }

    public <T> ReportTable addDataList(List<T> docItems) {
        this.dataItems = docItems;
        return this;
    }

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)
                .add("isTableHeaderRowFromData", isTableHeaderRowFromData)
                .add("dataItems", dataItems)
                .toString();
    }

    public List<?> getDataItems() {
        return dataItems;
    }

    public boolean isTableHeaderRowFromData() {
        return isTableHeaderRowFromData;
    }

    public ReportTable setTableHeaderRowFromData(boolean isTableHeaderRowFromData) {
        this.isTableHeaderRowFromData = isTableHeaderRowFromData;
        return this;
    }
}
