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

package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.model.Customization;

import javax.json.bind.annotation.JsonbDateFormat;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * Abstract class for converting date objects from {@link java.time}.
 *
 * @author Roman Grigoriadi
 */

public abstract class AbstractDateTimeConverter<T extends TemporalAccessor> extends AbstractTypeConverter<T> {

    public AbstractDateTimeConverter(Class<T> clazzType) {
        super(clazzType);
    }

    @Override
    public T fromJson(String jsonValue, Type type, Customization customization) {
        JsonbDateFormatter formatter = getDateFormatter(customization);
        if (JsonbDateFormat.TIME_IN_MILLIS.equals(formatter.getFormat())) {
            return fromInstant(Instant.ofEpochMilli(Long.parseLong(jsonValue)));
        } else if (formatter.getDateTimeFormatter() != null) {
            return parseWithFormatter(jsonValue, formatter.getDateTimeFormatter());
        }
        return parseDefault(jsonValue, formatter.getLocale());
    }

    private JsonbDateFormatter getDateFormatter(Customization customization) {
        return customization != null ? customization.getDateTimeFormatter() : JsonbDateFormatter.getDefault();
    }

    @Override
    public String toJson(T object, Customization customization) {
        JsonbDateFormatter formatter = getDateFormatter(customization);
        if (JsonbDateFormat.TIME_IN_MILLIS.equals(formatter.getFormat())) {
            return String.valueOf(toInstant(object).toEpochMilli());
        } else if (formatter.getDateTimeFormatter() != null) {
            return formatter.getDateTimeFormatter().format(object);
        }
        return formatDefault(object, formatter.getLocale());
    }

    /**
     * Convert java.time object to epoch milliseconds instant. Discards zone offset and zone id information.
     *
     * @param value date object to convert
     * @return instant
     */
    protected abstract Instant toInstant(T value);

    /**
     * Construct date object from an instant containing epoch millisecond.
     * If date object supports zone offset / zone id, system default is used and warning is logged.
     *
     * @param instant instant to construct from
     * @return date object
     */
    protected abstract T fromInstant(Instant instant);

    /**
     * Format with default formatter for a given {@link java.time} date object.
     * Different default formatter for each date object type is used.
     *
     * @param value date object
     * @param locale locale from annotation / default not null
     * @return formatted date obj as string
     */
    protected abstract String formatDefault(T value, Locale locale);

    /**
     * Parse {@link java.time} date object with default formatter.
     * Different default formatter for each date object type is used.
     *
     * @param jsonValue string value to parse from
     * @param locale annotated locale or default
     * @return parsed date object
     */
    protected abstract T parseDefault(String jsonValue, Locale locale);

    /**
     * Parse {@link java.time} date object with provided formatter.
     *
     * @param jsonValue string value to parse from
     * @param formatter a formatter to use
     * @return parsed date object
     */
    protected abstract T parseWithFormatter(String jsonValue, DateTimeFormatter formatter);

    private String format(String format, Locale locale, T dateObj) {
        if (JsonbDateFormat.DEFAULT_FORMAT.equals(format)) {
            return formatDefault(dateObj, locale);
        }
        return DateTimeFormatter.ofPattern(format, locale).format(dateObj);
    }
}
