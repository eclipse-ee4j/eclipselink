package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author David Král
 */
public class DurationTypeConverter extends AbstractTypeConverter<Duration> {

    public DurationTypeConverter() {
        super(Duration.class);
    }

    @Override
    public Duration fromJson(String jsonValue, Type type) {
        return Duration.parse(jsonValue);
    }

    @Override
    public String toJson(Duration object) {
        return quoteString(object.toString());
    }

}
