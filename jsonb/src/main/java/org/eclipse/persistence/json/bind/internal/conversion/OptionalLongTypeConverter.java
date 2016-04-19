package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.model.Customization;

import java.lang.reflect.Type;
import java.util.OptionalLong;

/**
 * @author David Král
 */
public class OptionalLongTypeConverter extends AbstractTypeConverter<OptionalLong> {

    public OptionalLongTypeConverter() {
        super(OptionalLong.class);
    }

    @Override
    public OptionalLong fromJson(String jsonValue, Type type, Customization customization) {
        return NULL.equals(jsonValue) ? OptionalLong.empty() : OptionalLong.of(Long.parseLong(jsonValue));
    }

    @Override
    public String toJson(OptionalLong object, Customization customization) {
        return object.isPresent() ? String.valueOf(object.getAsLong()) : NULL;
    }

}
