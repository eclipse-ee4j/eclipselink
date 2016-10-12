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

import org.eclipse.persistence.json.bind.internal.ProcessingContext;
import org.eclipse.persistence.json.bind.internal.Unmarshaller;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;

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

public abstract class AbstractDateTimeDeserializer<T extends TemporalAccessor> extends AbstractValueTypeDeserializer<T> {


    public AbstractDateTimeDeserializer(Class<T> clazz, JsonBindingModel model) {
        super(clazz, model);
    }

    @Override
    public T deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        final JsonbDateFormatter formatter = unmarshaller.getJsonbContext().getComponentMatcher().getDateFormatter(model);
        if (JsonbDateFormat.TIME_IN_MILLIS.equals(formatter.getFormat())) {
            return fromInstant(Instant.ofEpochMilli(Long.parseLong(jsonValue)));
        } else if (formatter.getDateTimeFormatter() != null) {
            return parseWithFormatter(jsonValue, formatter.getDateTimeFormatter());
        }
        return parseDefault(jsonValue, unmarshaller.getJsonbContext().getLocale(formatter.getLocale()));
    }

    /**
     * Construct date object from an instant containing epoch millisecond.
     * If date object supports zone offset / zone id, system default is used and warning is logged.
     *
     * @param instant instant to construct from
     * @return date object
     */
    protected abstract T fromInstant(Instant instant);

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
}
