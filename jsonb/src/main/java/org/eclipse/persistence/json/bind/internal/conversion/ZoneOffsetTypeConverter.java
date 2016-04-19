package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.model.Customization;

import java.lang.reflect.Type;
import java.time.ZoneOffset;

/**
 * @author David Král
 */
public class ZoneOffsetTypeConverter extends AbstractTypeConverter<ZoneOffset> {

    public ZoneOffsetTypeConverter() {
        super(ZoneOffset.class);
    }

    @Override
    public ZoneOffset fromJson(String jsonValue, Type type, Customization customization) {
        return ZoneOffset.of(jsonValue);
    }

    @Override
    public String toJson(ZoneOffset object, Customization customization) {
        return object.getId();
    }

}
