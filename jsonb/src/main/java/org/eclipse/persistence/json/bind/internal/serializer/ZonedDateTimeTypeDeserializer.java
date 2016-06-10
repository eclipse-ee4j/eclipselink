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

import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * @author David Král
 */
public class ZonedDateTimeTypeDeserializer extends AbstractDateTimeDeserializer<ZonedDateTime> {

    private static final Logger log = Logger.getLogger(ZonedDateTimeTypeDeserializer.class.getName());


    public ZonedDateTimeTypeDeserializer(JsonBindingModel model) {
        super(ZonedDateTime.class, model);
    }

    /**
     * fromInstant is called only in case {@link javax.json.bind.annotation.JsonbDateFormat} is TIME_IN_MILLIS,
     * which doesn't make much sense for usage with ZonedDateTime.
     */
    @Override
    protected ZonedDateTime fromInstant(Instant instant) {
        final ZoneId zone = ZoneId.systemDefault();
        log.warning(Messages.getMessage(MessageKeys.OFFSET_DATE_TIME_FROM_MILLIS, ZonedDateTime.class.getSimpleName(), zone));
        return ZonedDateTime.ofInstant(instant, zone);
    }

    @Override
    protected ZonedDateTime parseDefault(String jsonValue, Locale locale) {
        return ZonedDateTime.parse(jsonValue, DateTimeFormatter.ISO_ZONED_DATE_TIME.withLocale(locale));
    }

    @Override
    protected ZonedDateTime parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return ZonedDateTime.parse(jsonValue, formatter);
    }
}
