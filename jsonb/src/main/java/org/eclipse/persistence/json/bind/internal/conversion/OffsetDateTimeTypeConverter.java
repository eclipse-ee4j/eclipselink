package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author David Král
 */
public class OffsetDateTimeTypeConverter extends AbstractTypeConverter<OffsetDateTime> {

    public OffsetDateTimeTypeConverter() {
        super(OffsetDateTime.class);
    }

    @Override
    public OffsetDateTime fromJson(String jsonValue, Type type) {
        return OffsetDateTime.parse(jsonValue, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @Override
    public String toJson(OffsetDateTime object) {
        return quoteString(object.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

}
