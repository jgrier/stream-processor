package dev.streamprocessor.internal;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Wraps a record with its type name for serialization across Restate service boundaries.
 * This allows deserializing back to the correct Java type (including records) without
 * requiring compile-time type knowledge in the generic handler.
 */
public class RecordEnvelope {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public String typeName;
    public String valueJson;

    public RecordEnvelope() {}

    public RecordEnvelope(String typeName, String valueJson) {
        this.typeName = typeName;
        this.valueJson = valueJson;
    }

    public static String wrap(Object value) throws Exception {
        String typeName = value.getClass().getName();
        String valueJson = MAPPER.writeValueAsString(value);
        return MAPPER.writeValueAsString(new RecordEnvelope(typeName, valueJson));
    }

    public static Object unwrap(String json) throws Exception {
        RecordEnvelope envelope = MAPPER.readValue(json, RecordEnvelope.class);
        Class<?> clazz = Class.forName(envelope.typeName);
        return MAPPER.readValue(envelope.valueJson, clazz);
    }
}
