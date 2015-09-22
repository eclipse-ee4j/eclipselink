package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * @author David Král
 */
public class ZoneOffsetTypeConverter extends AbstractTypeConverter<ZoneOffset> {

    public ZoneOffsetTypeConverter() {
        super(ZoneOffset.class);
    }

    @Override
    public ZoneOffset fromJson(String jsonValue, Type type) {
        return ZoneOffset.of(jsonValue);
    }

    @Override
    public String toJson(ZoneOffset object) {
        return quoteString(object.getId());
    }

}
