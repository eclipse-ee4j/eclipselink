package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;

/**
 * @author David Král
 */
public class FloatTypeConverter extends AbstractTypeConverter<Float> {

    public FloatTypeConverter() {
        super(Float.class);
    }

    @Override
    public Float fromJson(String jsonValue, Type type) {
        return Float.parseFloat(jsonValue);
    }

    @Override
    public String toJson(Float object) {
        return String.valueOf(object);
    }

    @Override
    public boolean supportsToJson(Class<?> type) {
        return super.supportsToJson(type)
                || type == float.class;
    }

    @Override
    public boolean supportsFromJson(Class<?> type) {
        return super.supportsFromJson(type)
                || type == float.class;
    }
}
