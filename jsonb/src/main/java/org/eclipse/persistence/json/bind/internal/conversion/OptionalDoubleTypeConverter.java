package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;
import java.util.OptionalDouble;

/**
 * @author David Král
 */
public class OptionalDoubleTypeConverter extends AbstractTypeConverter<OptionalDouble> {

    private final DoubleTypeConverter doubleTypeConverter = new DoubleTypeConverter();

    public OptionalDoubleTypeConverter() {
        super(OptionalDouble.class);
    }

    @Override
    public OptionalDouble fromJson(String jsonValue, Type type) {
        return NULL.equals(jsonValue) ? OptionalDouble.empty() : OptionalDouble.of(doubleTypeConverter.fromJson(jsonValue, type));
    }

    @Override
    public String toJson(OptionalDouble object) {
        return object.isPresent() ? String.valueOf(doubleTypeConverter.toJson(object.getAsDouble())) : NULL;
    }

}
