package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.model.Customization;

import java.lang.reflect.Type;
import java.util.OptionalInt;

/**
 * @author David Král
 */
public class OptionalIntTypeConverter extends AbstractTypeConverter<OptionalInt> {

    public OptionalIntTypeConverter() {
        super(OptionalInt.class);
    }

    @Override
    public OptionalInt fromJson(String jsonValue, Type type, Customization customization) {
        return NULL.equals(jsonValue) ? OptionalInt.empty() : OptionalInt.of(Integer.parseInt(jsonValue));
    }

    @Override
    public String toJson(OptionalInt object, Customization customization) {
        return object.isPresent() ? String.valueOf(object.getAsInt()) : NULL;
    }

}
