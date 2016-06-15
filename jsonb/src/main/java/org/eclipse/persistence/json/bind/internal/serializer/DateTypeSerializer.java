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
import org.eclipse.persistence.json.bind.model.SerializerBindingModel;
import org.eclipse.persistence.json.bind.model.PropertyModel;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author David Král
 */
public class DateTypeSerializer extends AbstractValueTypeSerializer<Date> {


    public DateTypeSerializer(SerializerBindingModel model) {
        super(Date.class, model);
    }

    private String toJson(Date object, JsonbDateFormatter formatter) {
        if (JsonbDateFormat.TIME_IN_MILLIS.equals(formatter.getFormat())) {
            return String.valueOf(object.getTime());
        }
        return getDateFormat(formatter).format(object);
    }

    private DateFormat getDateFormat(JsonbDateFormatter formatter) {
        if (JsonbDateFormat.DEFAULT_FORMAT.equals(formatter.getFormat())) {
            return new SimpleDateFormat(JsonbDateFormatter.ISO_8601_DATE_TIME_FORMAT, ProcessingContext.getJsonbContext().getLocale(formatter.getLocale()));
        }
        return new SimpleDateFormat(formatter.getFormat(), ProcessingContext.getJsonbContext().getLocale(formatter.getLocale()));
    }

    @Override
    public void serialize(Date obj, JsonGenerator generator, SerializationContext ctx) {
        final JsonbDateFormatter formatter = ProcessingContext.getJsonbContext().getComponentMatcher().getDateFormatter(model);
        if (model instanceof PropertyModel) {
            generator.write(((PropertyModel)model).getPropertyName(), toJson(obj, formatter));
        } else {
            generator.write(toJson(obj, formatter));
        }
    }

    @Override
    protected void serialize(Date obj, JsonGenerator generator, String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void serialize(Date obj, JsonGenerator generator) {
        throw new UnsupportedOperationException();
    }
}
