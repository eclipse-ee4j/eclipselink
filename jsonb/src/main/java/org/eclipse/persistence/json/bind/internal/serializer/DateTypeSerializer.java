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
import org.eclipse.persistence.json.bind.internal.Marshaller;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;
import org.eclipse.persistence.json.bind.model.JsonContext;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author David Král
 */
public class DateTypeSerializer extends AbstractValueTypeSerializer<Date> {


    public DateTypeSerializer(JsonBindingModel model) {
        super(model);
    }

    private String toJson(Date object, JsonbDateFormatter formatter, JsonbContext jsonbContext) {
        if (JsonbDateFormat.TIME_IN_MILLIS.equals(formatter.getFormat())) {
            return String.valueOf(object.getTime());
        }
        Locale locale = jsonbContext.getLocale(formatter.getLocale());
        return getDateFormat(formatter, locale).format(object);
    }

    private DateFormat getDateFormat(JsonbDateFormatter formatter, Locale locale) {
        if (JsonbDateFormat.DEFAULT_FORMAT.equals(formatter.getFormat())) {
            return new SimpleDateFormat(JsonbDateFormatter.ISO_8601_DATE_TIME_FORMAT, locale);
        }
        return new SimpleDateFormat(formatter.getFormat(), locale);
    }

    @Override
    public void serialize(Date obj, JsonGenerator generator, SerializationContext ctx) {
        final JsonbContext jsonbContext = ((Marshaller) ctx).getJsonbContext();
        final JsonbDateFormatter formatter = jsonbContext.getComponentMatcher().getDateFormatter(model);
        if (model.getContext() == JsonContext.JSON_OBJECT) {
            generator.write(model.getWriteName(), toJson(obj, formatter, jsonbContext));
        } else {
            generator.write(toJson(obj, formatter, jsonbContext));
        }
    }

    @Override
    protected void serialize(Date obj, JsonGenerator generator, String key, Marshaller marshaller) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void serialize(Date obj, JsonGenerator generator, Marshaller marshaller) {
        throw new UnsupportedOperationException();
    }
}
