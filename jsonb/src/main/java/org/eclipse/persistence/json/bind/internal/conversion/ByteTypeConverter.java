package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.model.Customization;

import java.lang.reflect.Type;

/**
 * @author David Král
 */
public class ByteTypeConverter extends AbstractTypeConverter<Byte> {

    public ByteTypeConverter() {
        super(Byte.class);
    }

    @Override
    public Byte fromJson(String jsonValue, Type type, Customization customization) {
        return Byte.parseByte(jsonValue);
    }

    @Override
    public String toJson(Byte object, Customization customization) {
        return String.valueOf(object);
    }

    @Override
    public boolean supportsToJson(Class<?> type) {
        return  super.supportsToJson(type)
                || type == byte.class;
    }

    @Override
    public boolean supportsFromJson(Class<?> type) {
        return super.supportsFromJson(type)
                || type == byte.class;
    }
}
