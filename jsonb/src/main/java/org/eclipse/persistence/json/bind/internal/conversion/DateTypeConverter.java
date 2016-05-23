package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.model.Customization;

import javax.json.bind.JsonbException;
import javax.json.bind.annotation.JsonbDateFormat;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author David Král
 */
public class DateTypeConverter extends AbstractTypeConverter<Date> {

    public DateTypeConverter() {
        super(Date.class);
    }

    @Override
    public Date fromJson(String jsonValue, Type type, Customization customization) {
        final JsonbDateFormatter dateFormatter = getDateFormatter(customization);
        if(JsonbDateFormat.TIME_IN_MILLIS.equals(dateFormatter.getFormat())) {
            return new Date(Long.parseLong(jsonValue));
        }
        final DateFormat dateFormat = getDateFormat(dateFormatter);
        try {
            return dateFormat.parse(jsonValue);
        } catch (ParseException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.DATE_PARSE_ERROR, jsonValue, dateFormat));
        }
    }

    @Override
    public String toJson(Date object, Customization customization) {
        final JsonbDateFormatter formatter = getDateFormatter(customization);
        if (JsonbDateFormat.TIME_IN_MILLIS.equals(formatter.getFormat())) {
            return String.valueOf(object.getTime());
        }
        return getDateFormat(formatter).format(object);
    }

    private DateFormat getDateFormat(JsonbDateFormatter formatter) {
        if (JsonbDateFormat.DEFAULT_FORMAT.equals(formatter.getFormat())) {
            return new SimpleDateFormat(JsonbDateFormatter.ISO_8601_DATE_TIME_FORMAT, formatter.getLocale());
        }
        return new SimpleDateFormat(formatter.getFormat(), formatter.getLocale());
    }

    private JsonbDateFormatter getDateFormatter(Customization customization) {
        return customization != null ? customization.getDateTimeFormatter() : JsonbDateFormatter.getDefault();
    }

}
