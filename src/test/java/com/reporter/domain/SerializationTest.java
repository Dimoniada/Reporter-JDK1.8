package com.reporter.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.model.domain.Document;
import com.reporter.formatter.BaseDocument;

public class SerializationTest extends BaseDocument {

    //@Test
    public void testSerializeDeserialize() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        final ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
        final String json = ow.writeValueAsString(doc);
        final Document document = objectMapper.readValue(json, Document.class);
    }
}
