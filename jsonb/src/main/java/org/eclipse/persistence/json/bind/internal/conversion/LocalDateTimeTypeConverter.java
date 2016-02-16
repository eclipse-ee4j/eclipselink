package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author David Král
 */
public class LocalDateTimeTypeConverter extends AbstractTypeConverter<LocalDateTime> {

    public LocalDateTimeTypeConverter() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime fromJson(String jsonValue, Type type) {
        return LocalDateTime.parse(jsonValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public String toJson(LocalDateTime object) {
        return object.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

}
