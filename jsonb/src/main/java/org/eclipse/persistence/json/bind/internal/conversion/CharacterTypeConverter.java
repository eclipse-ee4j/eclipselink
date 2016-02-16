package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;
import java.util.OptionalDouble;

/**
 * @author David Král
 */
public class CharacterTypeConverter extends AbstractTypeConverter<Character> {

    public CharacterTypeConverter() {
        super(Character.class);
    }

    @Override
    public Character fromJson(String jsonValue, Type type) {
        return jsonValue.charAt(0);
    }

    @Override
    public String toJson(Character object) {
        return object.toString();
    }

    @Override
    public boolean supportsToJson(Class<?> type) {
        return super.supportsToJson(type)
                || type == char.class;
    }

    @Override
    public boolean supportsFromJson(Class<?> type) {
        return super.supportsFromJson(type)
                || type == char.class;
    }
}
