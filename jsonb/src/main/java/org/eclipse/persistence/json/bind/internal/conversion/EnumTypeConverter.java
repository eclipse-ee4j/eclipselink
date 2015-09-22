package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;

/**
 * @author David Král
 */
public class EnumTypeConverter extends AbstractTypeConverter<Enum> {

    public EnumTypeConverter() {
        super(Enum.class);
    }

    @Override
    public Enum fromJson(String jsonValue, Type type) {
        Class<? extends Enum> en = (Class<? extends Enum>) type;
        for (Enum c : en.getEnumConstants()) {
            if (c.name().equals(jsonValue)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public String toJson(Enum object) {
        return quoteString(object.toString());
    }

    @Override
    public boolean supportsToJson(Class<?> type) {
        return type.isEnum();
    }
}
