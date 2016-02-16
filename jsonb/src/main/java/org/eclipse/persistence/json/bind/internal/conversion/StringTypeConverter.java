package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;

/**
 * @author Roman Grigoriadi
 */
public class StringTypeConverter extends AbstractTypeConverter<String> {

    public StringTypeConverter() {
        super(String.class);
    }

    @Override
    public String fromJson(String jsonValue, Type type) {
        return jsonValue;
    }

    @Override
    public String toJson(String object) {
        return object.toString();
    }

    @Override
    public boolean supportsToJson(Class type) {
        return type.isAssignableFrom(CharSequence.class);
    }

    @Override
    public boolean supportsFromJson(Class type) {
        return type.isAssignableFrom(CharSequence.class);
    }


}
