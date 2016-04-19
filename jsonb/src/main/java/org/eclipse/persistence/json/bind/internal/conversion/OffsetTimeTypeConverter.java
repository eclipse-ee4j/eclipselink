package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;

import javax.json.bind.JsonbException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * @author David Král
 */
public class OffsetTimeTypeConverter extends AbstractDateTimeConverter<OffsetTime> {

    private static final Logger log = Logger.getLogger(OffsetTimeTypeConverter.class.getName());

    public OffsetTimeTypeConverter() {
        super(OffsetTime.class);
    }

    @Override
    protected Instant toInstant(OffsetTime value) {
        throw new JsonbException(Messages.getMessage(MessageKeys.TIME_TO_EPOCH_MILLIS_ERROR, OffsetTime.class.getSimpleName()));
    }

    @Override
    protected OffsetTime fromInstant(Instant instant) {
        throw new JsonbException(Messages.getMessage(MessageKeys.TIME_TO_EPOCH_MILLIS_ERROR, OffsetTime.class.getSimpleName()));
    }

    @Override
    protected String formatDefault(OffsetTime value, Locale locale) {
        return DateTimeFormatter.ISO_OFFSET_TIME.withLocale(locale).format(value);
    }

    @Override
    protected OffsetTime parseDefault(String jsonValue, Locale locale) {
        return OffsetTime.parse(jsonValue, DateTimeFormatter.ISO_OFFSET_TIME.withLocale(locale));
    }

    @Override
    protected OffsetTime parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return OffsetTime.parse(jsonValue, formatter);
    }
}
