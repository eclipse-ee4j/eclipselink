package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.model.Customization;

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
    public OptionalDouble fromJson(String jsonValue, Type type, Customization customization) {
        return NULL.equals(jsonValue) ? OptionalDouble.empty() : OptionalDouble.of(doubleTypeConverter.fromJson(jsonValue, type, null));
    }

    @Override
    public String toJson(OptionalDouble object, Customization customization) {
        return object.isPresent() ? String.valueOf(doubleTypeConverter.toJson(object.getAsDouble(), null)) : NULL;
    }

}
