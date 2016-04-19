package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.model.Customization;

import java.lang.reflect.Type;

/**
 * @author David Král
 */
public class EnumTypeConverter extends AbstractTypeConverter<Enum> {

    public EnumTypeConverter() {
        super(Enum.class);
    }

    @Override
    public Enum fromJson(String jsonValue, Type type, Customization customization) {
        Class<? extends Enum> en = (Class<? extends Enum>) type;
        for (Enum c : en.getEnumConstants()) {
            if (c.name().equals(jsonValue)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public boolean supportsFromJson(Class<?> type) {
        return type.isEnum();
    }

    @Override
    public String toJson(Enum object, Customization customization) {
        return object.toString();
    }

    @Override
    public boolean supportsToJson(Class<?> type) {
        return type.isEnum();
    }
}
