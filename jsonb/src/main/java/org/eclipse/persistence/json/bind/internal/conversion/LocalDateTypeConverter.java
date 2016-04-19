package org.eclipse.persistence.json.bind.internal.conversion;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author David Král
 */
public class LocalDateTypeConverter extends AbstractDateTimeConverter<LocalDate> {


    public LocalDateTypeConverter() {
        super(LocalDate.class);
    }

    @Override
    protected Instant toInstant(LocalDate value) {
        return Instant.from(value.atStartOfDay(ZoneId.systemDefault()));
    }

    @Override
    protected LocalDate fromInstant(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Override
    protected String formatDefault(LocalDate value, Locale locale) {
        return DateTimeFormatter.ISO_LOCAL_DATE.withLocale(locale).format(value);
    }

    @Override
    protected LocalDate parseDefault(String jsonValue, Locale locale) {
        return LocalDate.parse(jsonValue, DateTimeFormatter.ISO_LOCAL_DATE.withLocale(locale));
    }

    @Override
    protected LocalDate parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return LocalDate.parse(jsonValue, formatter);
    }
}
