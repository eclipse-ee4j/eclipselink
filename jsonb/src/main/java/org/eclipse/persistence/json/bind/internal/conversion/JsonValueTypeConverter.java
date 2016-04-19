package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.model.Customization;

import javax.json.JsonValue;
import java.lang.reflect.Type;

/**
 * @author David Král
 */
public class JsonValueTypeConverter extends AbstractTypeConverter<JsonValue> {

    private final static String TRUE = "true";
    private final static String FALSE = "false";

    public JsonValueTypeConverter() {
        super(JsonValue.class);
    }

    @Override
    public JsonValue fromJson(String jsonValue, Type type, Customization customization) {
        switch (jsonValue) {
            case TRUE:
                return JsonValue.TRUE;
            case FALSE:
                return JsonValue.FALSE;
            case NULL:
                return JsonValue.NULL;
        }
        return null;
    }

    @Override
    public String toJson(JsonValue object, Customization customization) {
        return object.toString();
    }

    @Override
    public boolean supportsToJson(Class<?> type) {
        return JsonValue.class.isAssignableFrom(type)
                && type.isAnonymousClass();
    }

}
