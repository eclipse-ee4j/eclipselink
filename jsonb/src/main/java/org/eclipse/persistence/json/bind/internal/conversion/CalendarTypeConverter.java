package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.internal.JsonbContext;
import org.eclipse.persistence.json.bind.model.Customization;

import javax.json.bind.JsonbConfig;
import javax.json.bind.annotation.JsonbDateFormat;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

/**
 * @author David Král
 */
public class CalendarTypeConverter extends AbstractTypeConverter<Calendar> {

    private final Calendar calendarTemplate;

    public CalendarTypeConverter() {
        super(Calendar.class);
        calendarTemplate = Calendar.getInstance();
        calendarTemplate.clear();
    }

    @Override
    public Calendar fromJson(String jsonValue, Type type, Customization customization) {
        final JsonbDateFormatter formatter = customization.getDateTimeFormatter();
        Calendar result = (Calendar) calendarTemplate.clone();
        final String format = formatter.getFormat();
        if (JsonbDateFormat.TIME_IN_MILLIS.equals(format)) {
            result.setTimeInMillis(Long.parseLong(jsonValue));
            return result;
        }

        Locale locale = formatter.getLocale();
        if (JsonbDateFormat.DEFAULT_FORMAT.equals(format)) {
            final boolean timed = jsonValue.contains("T");
            if (timed) {
                result.setTimeInMillis(parseDefaultDateTime(jsonValue, locale));
                return result;
            } else {
                result.setTime(parseDefaultDate(jsonValue, locale));
                return result;
            }

        }

        DateTimeFormatter customFormat = DateTimeFormatter.ofPattern(format, locale);
        final TemporalAccessor parsed = customFormat.parse(jsonValue);
        result.setTime(new Date(Instant.from(parsed).toEpochMilli()));
        return result;
    }

    /**
     * Parses with ISO_DATE_TIME format and converts to util.Calendar thereafter.
     * TODO PERF subject to reconsider if conversion between java.time and java.util outweights threadsafe java.time formatter.
     * @param jsonValue value to parse
     * @param locale locale
     * @return epoch millisecond
     */
    private Long parseDefaultDateTime(String jsonValue, Locale locale) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME.withLocale(locale);
        final TemporalAccessor temporal = dateTimeFormatter.parse(jsonValue);
        //With timezone
        if (temporal.isSupported(ChronoField.OFFSET_SECONDS)) {
            final ZonedDateTime zdt = ZonedDateTime.from(temporal);
            return zdt.toInstant().toEpochMilli();
        }
        //No timezone
        LocalDateTime dateTime = LocalDateTime.from(temporal);
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private Date parseDefaultDate(String jsonValue, Locale locale) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE.withLocale(locale);
        LocalDate localDate = LocalDate.parse(jsonValue, dateTimeFormatter);
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public String toJson(Calendar object, Customization customization) {
        final JsonbDateFormatter formatter = customization.getDateTimeFormatter();
        if (JsonbDateFormat.TIME_IN_MILLIS.equals(formatter.getFormat())) {
            return String.valueOf(object.getTime().getTime());
        }

        Locale locale = formatter.getLocale();
        if (JsonbDateFormat.DEFAULT_FORMAT.equals(formatter.getFormat())) {
            final Optional<Object> strictJson =
                    JsonbContext.getInstance().getConfig().getProperty(JsonbConfig.STRICT_IJSON);

            //Use ISO_DATE_TIME, convert to java.time first
            //TODO PERF subject to reconsider if conversion between java.time and java.util outweights threadsafe java.time formatter.
            if (strictJson.isPresent() && (Boolean) strictJson.get()
                    || object.isSet(Calendar.HOUR) || object.isSet(Calendar.HOUR_OF_DAY)) {

                ZonedDateTime zdt = ZonedDateTime.ofInstant(object.toInstant(), object.getTimeZone().toZoneId());
                return DateTimeFormatter.ISO_DATE_TIME.withLocale(locale).format(zdt);
            }
            final DateFormat defaultFormat = new SimpleDateFormat(JsonbDateFormatter.ISO_8601_DATE_FORMAT, locale);
            defaultFormat.setTimeZone(object.getTimeZone());
            return defaultFormat.format(object.getTime());
        }

        DateFormat custom = new SimpleDateFormat(formatter.getFormat(), locale);
        custom.setTimeZone(object.getTimeZone());
        return custom.format(object.getTime());
    }

}
