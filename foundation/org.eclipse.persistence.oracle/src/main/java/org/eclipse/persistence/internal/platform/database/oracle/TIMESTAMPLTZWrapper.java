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
package org.eclipse.persistence.internal.platform.database.oracle;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
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

import oracle.sql.TIMESTAMPLTZ;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.databaseaccess.PlatformWrapper;
import org.eclipse.persistence.internal.helper.Helper;

/**
 * This class is used as a wrapper for TIMESTAMPLTZ.  It stores a Timestamp and a timezone id.
 * Bug#4364359  This wrapper is added to overcome TIMESTAMPLTZ not serializable as of jdbc 9.2.0.5 and 10.1.0.2.
 * It has been fixed in the next version for both streams
 */
public class TIMESTAMPLTZWrapper implements Serializable, PlatformWrapper {

    final ZonedDateTime zonedDateTime;
    final boolean isLtzTimestampInGmt;

    public TIMESTAMPLTZWrapper(final ZonedDateTime zonedDateTime, final boolean isLtzTimestampInGmt) throws SQLException {
        this.zonedDateTime = zonedDateTime;
        this.isLtzTimestampInGmt = isLtzTimestampInGmt;
    }

    // Calendar to TIMESTAMPLTZ uses wrapper to delay TIMESTAMPLTZ constructor call until session is available
    // in setParameterValueInDatabaseCall.
    public TIMESTAMPLTZ builtTimestampLtz(Connection connection) {
        try {
            return new TIMESTAMPLTZ(connection, zonedDateTime);
        } catch (SQLException exception) {
            throw DatabaseException.sqlException(exception);
        }
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
        final Calendar calendar;
        if (getZoneId() != null) {
            calendar = Calendar.getInstance(TimeZone.getTimeZone(getZoneId()));
        } else {
            calendar = Calendar.getInstance();
        }

        // This is the only way to set time in Calendar.  Passing Timestamp directly to the new
        // calendar does not work because the GMT time is wrong.
        if (isLtzTimestampInGmt) {
            calendar.setTimeInMillis(getTimestamp().getTime());
        } else {
            final Calendar localCalendar = Helper.allocateCalendar();
            localCalendar.setTime(getTimestamp());
            calendar.set(
                    localCalendar.get(Calendar.YEAR), localCalendar.get(Calendar.MONTH), localCalendar.get(Calendar.DATE),
                    localCalendar.get(Calendar.HOUR_OF_DAY), localCalendar.get(Calendar.MINUTE), localCalendar.get(Calendar.SECOND));
            Helper.releaseCalendar(localCalendar);
        }
        calendar.set(Calendar.MILLISECOND, getTimestamp().getNanos() / 1000000);

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

    private final Map<Class<?>, Function<TIMESTAMPLTZWrapper,?>> UNWRAP = initUnwrappers();

    Map<Class<?>, Function<TIMESTAMPLTZWrapper, ?>> initUnwrappers() {
        Map<Class<?>, Function<TIMESTAMPLTZWrapper, ?>> unwrappers = new HashMap<>();
        unwrappers.put(TIMESTAMPLTZWrapper.class, (wrapper) -> wrapper);
        unwrappers.put(LocalDate.class, TIMESTAMPLTZWrapper::toLocalDate);
        unwrappers.put(LocalTime.class, TIMESTAMPLTZWrapper::toLocalTime);
        unwrappers.put(LocalDateTime.class, TIMESTAMPLTZWrapper::toLocalDateTime);
        unwrappers.put(OffsetTime.class, TIMESTAMPLTZWrapper::toOffsetTime);
        unwrappers.put(OffsetDateTime.class, TIMESTAMPLTZWrapper::toOffsetDateTime);
        unwrappers.put(Timestamp.class, TIMESTAMPLTZWrapper::getTimestamp);
        unwrappers.put(TimeZone.class, TIMESTAMPLTZWrapper::getTimeZone);
        unwrappers.put(ZoneId.class, TIMESTAMPLTZWrapper::getZoneId);
        unwrappers.put(Calendar.class, TIMESTAMPLTZWrapper::toCalendar);
        return unwrappers;
    }

    public <T> T unwrap(final Class<T> type) {
        Function<TIMESTAMPLTZWrapper, ?> unwrapper = UNWRAP.get(type);
        if (unwrapper != null) {
            return type.cast(unwrapper.apply(this));
        }
        throw new IllegalArgumentException("Unwrapping of TIMESTAMPLTZWrapper as " + type.getName() + " is not supported");
    }

}
