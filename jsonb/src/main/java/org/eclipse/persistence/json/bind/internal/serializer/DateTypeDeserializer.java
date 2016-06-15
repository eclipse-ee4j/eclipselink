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
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;

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
public class DateTypeDeserializer extends AbstractValueTypeDeserializer<Date> {


    public DateTypeDeserializer(JsonBindingModel model) {
        super(Date.class, model);
    }

    private DateFormat getDateFormat(JsonbDateFormatter formatter) {
        if (JsonbDateFormat.DEFAULT_FORMAT.equals(formatter.getFormat())) {
            return new SimpleDateFormat(JsonbDateFormatter.ISO_8601_DATE_TIME_FORMAT, ProcessingContext.getJsonbContext().getLocale(formatter.getLocale()));
        }
        return new SimpleDateFormat(formatter.getFormat(), ProcessingContext.getJsonbContext().getLocale(formatter.getLocale()));
    }


    @Override
    protected Date deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {

        final JsonbDateFormatter dateFormatter = ProcessingContext.getJsonbContext().getComponentMatcher().getDateFormatter(model);
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
}
