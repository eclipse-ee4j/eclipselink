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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;

import org.eclipse.persistence.internal.databaseaccess.PlatformWrapper;

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

    public Timestamp getTimestamp() {
        return Timestamp.valueOf(zonedDateTime.toLocalDateTime());
    }

    public TimeZone getTimeZone() {
        return TimeZone.getTimeZone(zonedDateTime.getZone());
    }

    public ZoneId getZoneId() {
        return zonedDateTime.getZone();
    }

    public boolean isLtzTimestampInGmt() {
        return isLtzTimestampInGmt;
    }

    private final Map<Class<? extends Object>, Function<TIMESTAMPLTZWrapper,? extends Object>> UNWRAP = initUnwrappers();

    Map<Class<? extends Object>, Function<TIMESTAMPLTZWrapper, ? extends Object>> initUnwrappers() {
        Map<Class<? extends Object>, Function<TIMESTAMPLTZWrapper, ? extends Object>> unwrappers = new HashMap<>();
        unwrappers.put(TIMESTAMPLTZWrapper.class, (wrapper) -> wrapper);
        unwrappers.put(LocalDate.class, (wrapper) -> wrapper.toLocalDate());
        unwrappers.put(LocalTime.class, (wrapper) -> wrapper.toLocalTime());
        unwrappers.put(LocalDateTime.class, (wrapper) -> wrapper.toLocalDateTime());
        unwrappers.put(OffsetTime.class, (wrapper) -> wrapper.toOffsetTime());
        unwrappers.put(OffsetDateTime.class, (wrapper) -> wrapper.toOffsetDateTime());
        unwrappers.put(Timestamp.class, (wrapper) -> wrapper.getTimestamp());
        unwrappers.put(TimeZone.class, (wrapper) -> wrapper.getTimeZone());
        unwrappers.put(ZoneId.class, (wrapper) -> wrapper.getZoneId());
        return unwrappers;
    }

    public <T> T unwrap(final Class<T> type) {
        Function<TIMESTAMPLTZWrapper,? extends Object> unwrapper = UNWRAP.get(type);
        if (unwrapper != null) {
            return type.cast(unwrapper.apply(this));
        }
        throw new IllegalArgumentException("Unwrapping of TIMESTAMPLTZWrapper as " + type.getName() + " is not supported");
    }

}
