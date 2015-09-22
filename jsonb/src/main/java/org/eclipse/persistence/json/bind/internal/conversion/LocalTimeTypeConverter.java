package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author David Král
 */
public class LocalTimeTypeConverter extends AbstractTypeConverter<LocalTime> {

    public LocalTimeTypeConverter() {
        super(LocalTime.class);
    }

    @Override
    public LocalTime fromJson(String jsonValue, Type type) {
        return LocalTime.parse(jsonValue, DateTimeFormatter.ISO_LOCAL_TIME);
    }

    @Override
    public String toJson(LocalTime object) {
        return quoteString(object.format(DateTimeFormatter.ISO_LOCAL_TIME));
    }

}
