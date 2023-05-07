package com.model.formatter.html.tag;

import com.model.formatter.html.attribute.HtmlAttribute;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public interface HtmlTagAttributesWriter {
    Map<String, HtmlAttribute> getAvailableAttributes();

    default String attributesToHtmlString(Boolean isHtml4) {
        final Function<Map.Entry<String, HtmlAttribute>, String> itemMapping = em -> {
            final String key = em.getKey();
            final HtmlAttribute attribute = em.getValue();
            final String processedValue = attribute.produceDefaultStringAttribute(attribute.getAttributeValue());
            return
                String.format(
                    attribute.getAssignmentPattern(isHtml4),
                    key,
                    processedValue
                );
        };

        final Collector<CharSequence, ?, String> htmlAttrCollector = Collectors
            .joining(
                isHtml4
                    ? HtmlAttribute.DELIMITER_PATTERN_HTML4
                    : HtmlAttribute.DELIMITER_PATTERN_HTML5,
                " ",
                ""
            );

        final String res = getAvailableAttributes()
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .filter(s -> s.getValue().getAttributeValue() != null)
            .map(itemMapping)
            .collect(htmlAttrCollector);

        return
            res.length() > 1
                ? res
                : "";
    }
}
