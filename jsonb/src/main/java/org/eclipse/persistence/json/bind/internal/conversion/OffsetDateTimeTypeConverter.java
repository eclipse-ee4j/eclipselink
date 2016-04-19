package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * @author David Král
 */
public class OffsetDateTimeTypeConverter extends AbstractDateTimeConverter<OffsetDateTime> {

    private static final Logger log = Logger.getLogger(OffsetDateTimeTypeConverter.class.getName());


    public OffsetDateTimeTypeConverter() {
        super(OffsetDateTime.class);
    }

    @Override
    protected Instant toInstant(OffsetDateTime value) {
        return value.toInstant();
    }

    /**
     * fromInstant is called only in case {@link javax.json.bind.annotation.JsonbDateFormat} is TIME_IN_MILLIS,
     * which doesn't make much sense for usage with OffsetDateTime.
     */
    @Override
    protected OffsetDateTime fromInstant(Instant instant) {
        final ZoneId zone = ZoneId.systemDefault();
        log.warning(Messages.getMessage(MessageKeys.OFFSET_DATE_TIME_FROM_MILLIS, OffsetDateTime.class.getSimpleName(), zone));
        return OffsetDateTime.ofInstant(instant, zone);
    }

    @Override
    protected String formatDefault(OffsetDateTime value, Locale locale) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.withLocale(locale).format(value);
    }

    @Override
    protected OffsetDateTime parseDefault(String jsonValue, Locale locale) {
        return OffsetDateTime.parse(jsonValue, DateTimeFormatter.ISO_OFFSET_DATE_TIME.withLocale(locale));
    }

    @Override
    protected OffsetDateTime parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return OffsetDateTime.parse(jsonValue, formatter);
    }
}
