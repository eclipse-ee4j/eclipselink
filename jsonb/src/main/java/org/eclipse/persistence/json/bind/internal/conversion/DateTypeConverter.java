package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author David Král
 */
public class DateTypeConverter extends AbstractTypeConverter<Date> {

    public DateTypeConverter() {
        super(Date.class);
    }

    @Override
    public Date fromJson(String jsonValue, Type type) {
        Date date = new Date();
        date.setTime(LocalDateTime.parse(jsonValue, DateTimeFormatter.ISO_DATE_TIME).atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli());
        return date;
    }

    @Override
    public String toJson(Date object) {
        Calendar calendar = new Calendar.Builder().setInstant(object).build();
        final LocalDateTime localDate = LocalDateTime.ofInstant(calendar.toInstant(), ZoneOffset.systemDefault());
        return localDate.format(DateTimeFormatter.ISO_DATE_TIME);
    }

}
