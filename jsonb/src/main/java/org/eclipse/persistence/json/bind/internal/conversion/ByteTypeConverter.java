package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;

/**
 * @author David Král
 */
public class ByteTypeConverter extends AbstractTypeConverter<Byte> {

    public ByteTypeConverter() {
        super(Byte.class);
    }

    @Override
    public Byte fromJson(String jsonValue, Type type) {
        return Byte.parseByte(jsonValue);
    }

    @Override
    public String toJson(Byte object) {
        return String.valueOf(object);
    }

    @Override
    public boolean supportsToJson(Class<?> type) {
        return  super.supportsToJson(type)
                || type == short.class;
    }

    @Override
    public boolean supportsFromJson(Class<?> type) {
        return super.supportsFromJson(type)
                || type == short.class;
    }
}
