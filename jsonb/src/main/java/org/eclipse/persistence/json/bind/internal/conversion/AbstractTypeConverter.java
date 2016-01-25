package org.eclipse.persistence.json.bind.internal.conversion;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;

/**
 * @author David Král
 */
public abstract class AbstractTypeConverter<T> implements SupportedTypeConverter<T> {

    protected static final String NULL = "null";
    private final Class<T> clazzType;

    public AbstractTypeConverter(Class<T> clazzType) {
        this.clazzType = clazzType;
    }

    protected String quoteString(String string) {
        return String.join("", "\"", string, "\"");
    }

    @Override
    public boolean supportsFromJson(Class<?> type) {
        return clazzType == type;
    }

    @Override
    public boolean supportsToJson(Class<?> type) {
        return clazzType.isAssignableFrom(type);
    }

    protected JsonObject getJsonObject(String jsonValue) {
        StringReader stringReader = new StringReader(jsonValue);
        JsonReader jsonReader = Json.createReader(stringReader);
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();
        return jsonObject;
    }
}
