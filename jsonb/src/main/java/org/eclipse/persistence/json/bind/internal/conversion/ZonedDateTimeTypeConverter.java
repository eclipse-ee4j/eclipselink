package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * @author David Král
 */
public class ZonedDateTimeTypeConverter extends AbstractDateTimeConverter<ZonedDateTime> {

    private static final Logger log = Logger.getLogger(ZonedDateTimeTypeConverter.class.getName());


    public ZonedDateTimeTypeConverter() {
        super(ZonedDateTime.class);
    }

    @Override
    protected Instant toInstant(ZonedDateTime value) {
        return value.toInstant();
    }

    /**
     * fromInstant is called only in case {@link javax.json.bind.annotation.JsonbDateFormat} is TIME_IN_MILLIS,
     * which doesn't make much sense for usage with ZonedDateTime.
     */
    @Override
    protected ZonedDateTime fromInstant(Instant instant) {
        final ZoneId zone = ZoneId.systemDefault();
        log.warning(Messages.getMessage(MessageKeys.OFFSET_DATE_TIME_FROM_MILLIS, ZonedDateTime.class.getSimpleName(), zone));
        return ZonedDateTime.ofInstant(instant, zone);
    }

    @Override
    protected String formatDefault(ZonedDateTime value, Locale locale) {
        return DateTimeFormatter.ISO_ZONED_DATE_TIME.withLocale(locale).format(value);
    }

    @Override
    protected ZonedDateTime parseDefault(String jsonValue, Locale locale) {
        return ZonedDateTime.parse(jsonValue, DateTimeFormatter.ISO_ZONED_DATE_TIME.withLocale(locale));
    }

    @Override
    protected ZonedDateTime parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return ZonedDateTime.parse(jsonValue, formatter);
    }
}
