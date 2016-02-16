package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * @author David Král
 */
public class TimeZoneTypeConverter extends AbstractTypeConverter<TimeZone> {

    public TimeZoneTypeConverter() {
        super(TimeZone.class);
    }

    @Override
    public TimeZone fromJson(String jsonValue, Type type) {
        return TimeZone.getTimeZone(jsonValue);
    }

    @Override
    public String toJson(TimeZone object) {
        return (object).getID();
    }

}
