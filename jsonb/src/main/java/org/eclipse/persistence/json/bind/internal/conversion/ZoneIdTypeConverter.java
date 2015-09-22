package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;
import java.time.Period;
import java.time.ZoneId;

/**
 * @author David Král
 */
public class ZoneIdTypeConverter extends AbstractTypeConverter<ZoneId> {

    public ZoneIdTypeConverter() {
        super(ZoneId.class);
    }

    @Override
    public ZoneId fromJson(String jsonValue, Type type) {
        return ZoneId.of(jsonValue);
    }

    @Override
    public String toJson(ZoneId object) {
        return quoteString(object.getId());
    }

}
