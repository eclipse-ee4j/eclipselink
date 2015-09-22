package org.eclipse.persistence.json.bind.internal.conversion;

import javax.json.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;

/**
 * @author David Král
 */
public class JsonStructureTypeConverter extends AbstractTypeConverter<JsonStructure> {

    public JsonStructureTypeConverter() {
        super(JsonStructure.class);
    }

    @Override
    public JsonStructure fromJson(String jsonValue, Type type) {
        StringReader stringReader = new StringReader(jsonValue);
        JsonReader jsonReader = Json.createReader(stringReader);
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();

        return jsonObject;
    }

    @Override
    public String toJson(JsonStructure object) {
        final StringWriter stringWriter = new StringWriter();
        final JsonWriter jsonWriter = Json.createWriter(stringWriter);
        jsonWriter.write(object);
        jsonWriter.close();

        return stringWriter.toString();
    }

}
