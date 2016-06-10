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

import javax.json.bind.annotation.JsonbDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Formatter wrapper for different types of dates.
 *
 * @author Roman Grigoriadi
 */
public class JsonbDateFormatter {

    public static final String ISO_8601_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String ISO_8601_DATE_FORMAT = "yyyy-MM-dd";

    //Java 8 date formatter is thread safe, cache it if possible
    private final DateTimeFormatter dateTimeFormatter;

    private final String format;

    private final Locale locale;

    /**
     * Construct with cached {@link DateTimeFormatter}.
     * @param dateTimeFormatter
     */
    public JsonbDateFormatter(DateTimeFormatter dateTimeFormatter, String format, Locale locale) {
        this.dateTimeFormatter = dateTimeFormatter;
        this.format = format;
        this.locale = locale;
    }

    /**
     * Construct with format string and locale. Formatter will be created on every formatting / parsing operation.
     * @param format formatter format
     * @param locale locale
     */
    public JsonbDateFormatter(String format, Locale locale) {
        this.format = format;
        this.locale = locale;
        this.dateTimeFormatter = null;
    }

    /**
     * Cached instance of {@link DateTimeFormatter} to be used.
     * @return formatter instance
     */
    public DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    /**
     * Format string to be used either by formatter.
     * Needed for formatting {@link java.util.Date} with {@link java.text.SimpleDateFormat},
     * which is not threadsafe.
     * @return format
     */
    public String getFormat() {
        return format;
    }

    /**
     * Locale to use with formatter.
     * @return locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Default date formatter if none is provided.
     *
     * @return default date formatter
     */
    public static JsonbDateFormatter getDefault() {
        return new JsonbDateFormatter(JsonbDateFormat.DEFAULT_FORMAT, Locale.getDefault());
    }
}
