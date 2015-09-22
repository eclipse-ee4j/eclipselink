package org.eclipse.persistence.json.bind.internal.unmarshaller;

import javax.json.bind.JsonbException;
import javax.json.stream.JsonParser;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Default types for JSON values when type cannot be inferred by reflection.
 * Fields defined as Object.class types, or raw generic fields are such cases.
 *
 * @author Roman Grigoriadi
 */
public enum JsonValueType {
    BOOLEAN(Boolean.class),
    NUMBER(BigDecimal.class),
    STRING(String.class),
    ARRAY(ArrayList.class),
    OBJECT(HashMap.class),
    NULL(null);

    private JsonValueType(Class<?> supportedByType) {
        this.supportedByType = supportedByType;
    }

    private Class<?> supportedByType;

    public Class<?> getConversionType() {
        return supportedByType;
    }

    public static JsonValueType of(JsonParser.Event event) {
        switch (event) {
            case VALUE_FALSE:
            case VALUE_TRUE:
                return BOOLEAN;
            case VALUE_STRING:
                return STRING;
            case VALUE_NUMBER:
                return NUMBER;
            case VALUE_NULL:
                return NULL;
            case START_ARRAY:
                return ARRAY;
            case START_OBJECT:
                return OBJECT;
            default:
                throw new JsonbException("Not a value type: " + event.name());
        }
    }
}
