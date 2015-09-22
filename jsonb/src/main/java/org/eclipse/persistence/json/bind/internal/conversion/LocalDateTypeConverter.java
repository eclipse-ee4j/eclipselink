package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author David Král
 */
public class LocalDateTypeConverter extends AbstractTypeConverter<LocalDate> {

    public LocalDateTypeConverter() {
        super(LocalDate.class);
    }

    @Override
    public LocalDate fromJson(String jsonValue, Type type) {
        return LocalDate.parse(jsonValue, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Override
    public String toJson(LocalDate object) {
        return quoteString(object.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

}
