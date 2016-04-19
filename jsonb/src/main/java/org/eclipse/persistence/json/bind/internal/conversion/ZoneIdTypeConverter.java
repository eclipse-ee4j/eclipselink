package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.model.Customization;

import java.lang.reflect.Type;
import java.time.ZoneId;

/**
 * @author David Král
 */
public class ZoneIdTypeConverter extends AbstractTypeConverter<ZoneId> {

    public ZoneIdTypeConverter() {
        super(ZoneId.class);
    }

    @Override
    public ZoneId fromJson(String jsonValue, Type type, Customization customization) {
        return ZoneId.of(jsonValue);
    }

    @Override
    public String toJson(ZoneId object, Customization customization) {
        return object.getId();
    }

}
