package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author David Král
 */
public class ZonedDateTimeTypeConverter extends AbstractTypeConverter<ZonedDateTime> {

    public ZonedDateTimeTypeConverter() {
        super(ZonedDateTime.class);
    }

    @Override
    public ZonedDateTime fromJson(String jsonValue, Type type) {
        return ZonedDateTime.parse(jsonValue, DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    @Override
    public String toJson(ZonedDateTime object) {
        return object.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

}
