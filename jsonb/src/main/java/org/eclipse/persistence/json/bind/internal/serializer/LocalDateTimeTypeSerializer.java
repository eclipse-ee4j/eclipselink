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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author David Král
 */
public class LocalDateTimeTypeSerializer extends AbstractDateTimeSerializer<LocalDateTime> {

    public LocalDateTimeTypeSerializer(JsonBindingModel model) {
        super(LocalDateTime.class, model);
    }

    @Override
    protected Instant toInstant(LocalDateTime value) {
        return value.atZone(ZoneId.systemDefault()).toInstant();
    }


    @Override
    protected String formatDefault(LocalDateTime value, Locale locale) {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.withLocale(locale).format(value);
    }

}