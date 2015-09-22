package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;

/**
 * @author David Král
 */
public class ShortTypeConverter extends AbstractTypeConverter<Short> {

    public ShortTypeConverter() {
        super(Short.class);
    }

    @Override
    public Short fromJson(String jsonValue, Type type) {
        return Short.parseShort(jsonValue);
    }

    @Override
    public String toJson(Short object) {
        return String.valueOf(object);
    }

    @Override
    public boolean supportsToJson(Class<?> type) {
        return super.supportsToJson(type)
                || type == short.class;
    }

    @Override
    public boolean supportsFromJson(Class<?> type) {
        return super.supportsFromJson(type)
                || type == short.class;
    }
}
