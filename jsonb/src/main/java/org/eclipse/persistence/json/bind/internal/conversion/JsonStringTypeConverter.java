package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.internal.ProcessingContext;
import org.eclipse.persistence.json.bind.model.Customization;

import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonString;
import java.lang.reflect.Type;

/**
 * @author David Král
 */
public class JsonStringTypeConverter extends AbstractTypeConverter<JsonString> {

    public JsonStringTypeConverter() {
        super(JsonString.class);
    }

    @Override
    public JsonString fromJson(String jsonValue, Type type, Customization customization) {
        final JsonBuilderFactory factory = ProcessingContext.getJsonbContext().getJsonProvider().createBuilderFactory(null);
        final JsonObject jsonObject = factory.createObjectBuilder()
                .add("json", jsonValue)
                .build();
        return jsonObject.getJsonString("json");
    }

    @Override
    public String toJson(JsonString object, Customization customization) {
        return object.toString();
    }

}
