package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author David Král
 */
public class CalendarTypeConverter extends AbstractTypeConverter<Calendar> {

    private final Calendar calendarTemplate;

    public CalendarTypeConverter() {
        super(Calendar.class);
        calendarTemplate = Calendar.getInstance();
    }

    @Override
    public Calendar fromJson(String jsonValue, Type type) {
        if (jsonValue.contains("T")) {
            return new Calendar.Builder()
                    .setLocale(Locale.getDefault())
                    .setTimeZone(TimeZone.getDefault())
                    .setInstant(LocalDateTime.parse(jsonValue, DateTimeFormatter.ISO_DATE_TIME).atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli())
                    .build();
        } else {
            Calendar cal = (Calendar) calendarTemplate.clone();
            cal.setTimeInMillis(LocalDate.parse(jsonValue, DateTimeFormatter.ISO_DATE).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            return cal;
        }
    }

    @Override
    public String toJson(Calendar object) {
        final LocalDateTime localDate = LocalDateTime.ofInstant(object.toInstant(), ZoneOffset.systemDefault());

        DateTimeFormatter formatter;
        if (object.isSet(Calendar.HOUR)) {
            formatter = DateTimeFormatter.ISO_DATE_TIME;
        } else {
            formatter = DateTimeFormatter.ISO_DATE;
        }

        return quoteString(localDate.format(formatter));
    }

}
