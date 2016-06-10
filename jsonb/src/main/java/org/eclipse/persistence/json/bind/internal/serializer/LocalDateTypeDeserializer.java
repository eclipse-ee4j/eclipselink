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

import org.eclipse.persistence.json.bind.model.JsonBindingModel;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author David Král
 */
public class LocalDateTypeDeserializer extends AbstractDateTimeDeserializer<LocalDate> {


    public LocalDateTypeDeserializer(JsonBindingModel model) {
        super(LocalDate.class, model);
    }

    @Override
    protected LocalDate fromInstant(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Override
    protected LocalDate parseDefault(String jsonValue, Locale locale) {
        return LocalDate.parse(jsonValue, DateTimeFormatter.ISO_LOCAL_DATE.withLocale(locale));
    }

    @Override
    protected LocalDate parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return LocalDate.parse(jsonValue, formatter);
    }
}
