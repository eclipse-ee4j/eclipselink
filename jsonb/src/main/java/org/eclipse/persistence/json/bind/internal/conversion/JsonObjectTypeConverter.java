package org.eclipse.persistence.json.bind.internal.conversion;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;

/**
 * @author David Král
 */
public class JsonObjectTypeConverter extends AbstractTypeConverter<JsonObject> {

    public JsonObjectTypeConverter() {
        super(JsonObject.class);
    }

    @Override
    public JsonObject fromJson(String jsonValue, Type type) {
        return getJsonObject(jsonValue);
    }

    @Override
    public String toJson(JsonObject object) {
        final StringWriter stringWriter = new StringWriter();
        final JsonWriter jsonWriter = Json.createWriter(stringWriter);
        jsonWriter.writeObject(object);
        jsonWriter.close();

        return stringWriter.toString();
    }

}
