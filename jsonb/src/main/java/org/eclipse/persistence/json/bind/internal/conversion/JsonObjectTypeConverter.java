package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.internal.ProcessingContext;
import org.eclipse.persistence.json.bind.model.Customization;

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
    public JsonObject fromJson(String jsonValue, Type type, Customization customization) {
        throw new UnsupportedOperationException("Unexpected call");
    }

    @Override
    public String toJson(JsonObject object, Customization customization) {
        final StringWriter stringWriter = new StringWriter();
        final JsonWriter jsonWriter = ProcessingContext.getJsonbContext().getJsonProvider().createWriter(stringWriter);
        jsonWriter.writeObject(object);
        jsonWriter.close();

        return stringWriter.toString();
    }

}
