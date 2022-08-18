/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     dminsky - removed Timestamp & TimeZone retrieval code from constructor
package org.eclipse.persistence.internal.platform.database.oracle;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;

import org.eclipse.persistence.internal.databaseaccess.PlatformWrapper;

/**
 * This class is used as a wrapper for TIMESTAMPTZ.  It stores a Timestamp and a TimeZone.
 * Bug#4364359  This wrapper is added to overcome TIMESTAMPTZ not serializable as of jdbc 9.2.0.5 and 10.1.0.2.
 * It has been fixed in the next version for both streams
 */
public class TIMESTAMPTZWrapper implements Serializable, PlatformWrapper {

    private final ZonedDateTime zonedDateTime;

    public TIMESTAMPTZWrapper(final ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }

    public LocalTime toLocalTime() {
        return zonedDateTime.toLocalTime();
    }

    public LocalDate toLocalDate() {
        return zonedDateTime.toLocalDate();
    }

    public LocalDateTime toLocalDateTime() {
        return zonedDateTime.toLocalDateTime();
    }

    public OffsetTime toOffsetTime() {
        return zonedDateTime.toOffsetDateTime().toOffsetTime();
    }

    public OffsetDateTime toOffsetDateTime() {
        return zonedDateTime.toOffsetDateTime();
    }

    public Calendar toCalendar() {
        final TimeZone tz = getTimeZone();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getTimestamp());
        calendar.getTimeZone().setID(tz.getID());
        calendar.getTimeZone().setRawOffset(tz.getRawOffset());
        return calendar;
    }

    public Timestamp getTimestamp() {
        return Timestamp.valueOf(zonedDateTime.toLocalDateTime());
    }

    public TimeZone getTimeZone() {
        return TimeZone.getTimeZone(zonedDateTime.getZone());
    }

    public ZoneId getZoneId() {
        return zonedDateTime.getZone();
    }

    private final Map<Class<?>, Function<TIMESTAMPTZWrapper, ?>> UNWRAP = initUnwrappers();

    Map<Class<?>, Function<TIMESTAMPTZWrapper, ?>> initUnwrappers() {
        Map<Class<?>, Function<TIMESTAMPTZWrapper, ?>> unwrappers = new HashMap<>();
        unwrappers.put(TIMESTAMPTZWrapper.class, (wrapper) -> wrapper);
        unwrappers.put(LocalDate.class, TIMESTAMPTZWrapper::toLocalDate);
        unwrappers.put(LocalTime.class, TIMESTAMPTZWrapper::toLocalTime);
        unwrappers.put(LocalDateTime.class, TIMESTAMPTZWrapper::toLocalDateTime);
        unwrappers.put(OffsetTime.class, TIMESTAMPTZWrapper::toOffsetTime);
        unwrappers.put(OffsetDateTime.class, TIMESTAMPTZWrapper::toOffsetDateTime);
        unwrappers.put(Timestamp.class, TIMESTAMPTZWrapper::getTimestamp);
        unwrappers.put(TimeZone.class, TIMESTAMPTZWrapper::getTimeZone);
        unwrappers.put(ZoneId.class, TIMESTAMPTZWrapper::getZoneId);
        unwrappers.put(Calendar.class, TIMESTAMPTZWrapper::toCalendar);
        return unwrappers;
    }

    public <T> T unwrap(final Class<T> type) {
        Function<TIMESTAMPTZWrapper, ?> unwrapper = UNWRAP.get(type);
        if (unwrapper != null) {
            return type.cast(unwrapper.apply(this));
        }
        throw new IllegalArgumentException("Unwrapping of TIMESTAMPTZWrapper as " + type.getName() + " is not supported");
    }

}
