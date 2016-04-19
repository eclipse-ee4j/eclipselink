package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;

import javax.json.bind.JsonbException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author David Král
 */
public class LocalTimeTypeConverter extends AbstractDateTimeConverter<LocalTime> {


    public LocalTimeTypeConverter() {
        super(LocalTime.class);
    }

    @Override
    protected Instant toInstant(LocalTime value) {
        throw new JsonbException(Messages.getMessage(MessageKeys.TIME_TO_EPOCH_MILLIS_ERROR, LocalTime.class.getSimpleName()));
    }

    @Override
    protected LocalTime fromInstant(Instant instant) {
        throw new JsonbException(Messages.getMessage(MessageKeys.TIME_TO_EPOCH_MILLIS_ERROR, LocalTime.class.getSimpleName()));
    }

    @Override
    protected String formatDefault(LocalTime value, Locale locale) {
        return DateTimeFormatter.ISO_LOCAL_TIME.withLocale(locale).format(value);
    }

    @Override
    protected LocalTime parseDefault(String jsonValue, Locale locale) {
        return LocalTime.parse(jsonValue, DateTimeFormatter.ISO_LOCAL_TIME.withLocale(locale));
    }

    @Override
    protected LocalTime parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return LocalTime.parse(jsonValue, formatter);
    }
}
