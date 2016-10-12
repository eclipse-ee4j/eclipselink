/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal.serializer;

import org.eclipse.persistence.json.bind.internal.JsonbContext;
import org.eclipse.persistence.json.bind.internal.ProcessingContext;
import org.eclipse.persistence.json.bind.internal.Unmarshaller;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;

import javax.json.bind.annotation.JsonbDateFormat;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author David Král
 */
public class CalendarTypeDeserializer extends AbstractValueTypeDeserializer<Calendar> {

    private final Calendar calendarTemplate;


    public CalendarTypeDeserializer(JsonBindingModel model) {
        super(Calendar.class, model);
        calendarTemplate = Calendar.getInstance();
        calendarTemplate.clear();
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
    protected Calendar deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        final JsonbContext jsonbContext = unmarshaller.getJsonbContext();
        final JsonbDateFormatter formatter = jsonbContext.getComponentMatcher().getDateFormatter(model);
        Calendar result = (Calendar) calendarTemplate.clone();
        final String format = formatter.getFormat();
        if (JsonbDateFormat.TIME_IN_MILLIS.equals(format)) {
            result.setTimeInMillis(Long.parseLong(jsonValue));
            return result;
        }

        Locale locale = jsonbContext.getLocale(formatter.getLocale());
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
}
