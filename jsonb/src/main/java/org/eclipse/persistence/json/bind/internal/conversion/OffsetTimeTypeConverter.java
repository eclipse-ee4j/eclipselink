package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;

/**
 * @author David Král
 */
public class OffsetTimeTypeConverter extends AbstractTypeConverter<OffsetTime> {

    public OffsetTimeTypeConverter() {
        super(OffsetTime.class);
    }

    @Override
    public OffsetTime fromJson(String jsonValue, Type type) {
        return OffsetTime.parse(jsonValue, DateTimeFormatter.ISO_OFFSET_TIME);
    }

    @Override
    public String toJson(OffsetTime object) {
        return object.format(DateTimeFormatter.ISO_OFFSET_TIME);
    }

}
