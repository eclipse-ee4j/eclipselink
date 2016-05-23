package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.internal.ProcessingContext;
import org.eclipse.persistence.json.bind.model.Customization;

import javax.json.JsonStructure;
import javax.json.JsonWriter;
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
    public JsonStructure fromJson(String jsonValue, Type type, Customization customization) {
        throw new UnsupportedOperationException("Unexpected call");
    }

    @Override
    public String toJson(JsonStructure object, Customization customization) {
        final StringWriter stringWriter = new StringWriter();
        final JsonWriter jsonWriter = ProcessingContext.getJsonbContext().getJsonProvider().createWriter(stringWriter);
        jsonWriter.write(object);
        jsonWriter.close();

        return stringWriter.toString();
    }

}
