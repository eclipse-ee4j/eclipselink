package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;

/**
 * @author David Král
 */
public class LongTypeConverter extends AbstractTypeConverter<Long> {

    public LongTypeConverter() {
        super(Long.class);
    }

    @Override
    public Long fromJson(String jsonValue, Type type) {
        return Long.parseLong(jsonValue);
    }

    @Override
    public String toJson(Long object) {
        return String.valueOf(object);
    }

    @Override
    public boolean supportsToJson(Class<?> type) {
        return super.supportsToJson(type)
                || type == long.class;
    }

    @Override
    public boolean supportsFromJson(Class<?> type) {
        return super.supportsFromJson(type)
                || type == long.class;
    }
}
