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
import org.eclipse.persistence.json.bind.model.PropertyModel;
import org.eclipse.persistence.json.bind.model.SerializerBindingModel;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * Abstract class for converting date objects from {@link java.time}.
 *
 * @author Roman Grigoriadi
 */

public abstract class AbstractDateTimeSerializer<T extends TemporalAccessor> extends AbstractValueTypeSerializer<T> {


    /**
     * Construct serializer with its class.
     * @param clazz class
     */
    public AbstractDateTimeSerializer(Class<T> clazz, SerializerBindingModel model) {
        super(clazz, model);
    }

    /**
     * Serializes an object to JSON.
     *
     * @param obj       object to serialize
     * @param generator JSON generator to use
     * @param ctx       JSONB mapper context
     */
    @Override
    public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
        final JsonbDateFormatter formatter = ProcessingContext.getJsonbContext().getComponentMatcher().getDateFormatter(model);
        if (model instanceof PropertyModel) {
            generator.write(((PropertyModel)model).getPropertyName(), toJson(obj, formatter));
        } else {
            generator.write(toJson(obj, formatter));
        }
    }

    public String toJson(T object, JsonbDateFormatter formatter) {
        if (JsonbDateFormat.TIME_IN_MILLIS.equals(formatter.getFormat())) {
            return String.valueOf(toInstant(object).toEpochMilli());
        } else if (formatter.getDateTimeFormatter() != null) {
            return formatter.getDateTimeFormatter().format(object);
        }
        return formatDefault(object, ProcessingContext.getJsonbContext().getLocale(formatter.getLocale()));
    }

    /**
     * Convert java.time object to epoch milliseconds instant. Discards zone offset and zone id information.
     *
     * @param value date object to convert
     * @return instant
     */
    protected abstract Instant toInstant(T value);

    /**
     * Format with default formatter for a given {@link java.time} date object.
     * Different default formatter for each date object type is used.
     *
     * @param value date object
     * @param locale locale from annotation / default not null
     * @return formatted date obj as string
     */
    protected abstract String formatDefault(T value, Locale locale);

    @Override
    protected void serialize(T obj, JsonGenerator generator, String key) {
        throw new UnsupportedOperationException("Not supported in DateTimeSerializer");
    }

    @Override
    protected void serialize(T obj, JsonGenerator generator) {
        throw new UnsupportedOperationException("Not supported in DateTimeSerializer");
    }
}
