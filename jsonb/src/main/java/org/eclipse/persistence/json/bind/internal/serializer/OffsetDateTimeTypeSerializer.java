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
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * @author David Král
 */
public class OffsetDateTimeTypeSerializer extends AbstractDateTimeSerializer<OffsetDateTime> {

    private static final Logger log = Logger.getLogger(OffsetDateTimeTypeSerializer.class.getName());


    public OffsetDateTimeTypeSerializer(JsonBindingModel model) {
        super(OffsetDateTime.class, model);
    }

    @Override
    protected Instant toInstant(OffsetDateTime value) {
        return value.toInstant();
    }

    @Override
    protected String formatDefault(OffsetDateTime value, Locale locale) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.withLocale(locale).format(value);
    }

}