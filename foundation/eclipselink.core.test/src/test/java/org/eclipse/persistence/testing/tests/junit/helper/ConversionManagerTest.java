/*
 * Copyright (c) 2018, 2026 Oracle and/or its affiliates. All rights reserved.
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
//     IBM - Fix for issue 2905: ConversionException when entity @Version field is LocalDateTime
//           and the database returns an OffsetDateTime
// Additional boundary and compatibility tests generated with assistance from OpenAI Codex.
package org.eclipse.persistence.testing.tests.junit.helper;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link ConversionManager} covering the fix for issue 2905:
 * EclipseLink failed with a ConversionException when a Jakarta Persistence
 * entity had a {@code @Version} attribute of type {@link LocalDateTime} and
 * the JDBC driver returned an {@link OffsetDateTime} for the TIMESTAMP column.
 */
public class ConversionManagerTest {

    private final ConversionManager cm = ConversionManager.getDefaultManager();

    /**
     * Reproduces the exact scenario from issue 2905:
     * an OffsetDateTime value coming from the database must be converted to
     * LocalDateTime without throwing a ConversionException.
     */
    @Test
    public void testConvertOffsetDateTimeToLocalDateTime() {
        // OffsetDateTime as reported in the bug: 2026-05-14T15:58:09.122371-05:00
        OffsetDateTime odt = OffsetDateTime.of(2026, 5, 14, 15, 58, 9, 122371000, ZoneOffset.ofHours(-5));

        LocalDateTime result = cm.convertObject(odt, LocalDateTime.class);

        // toLocalDateTime() strips the offset – the local part must be preserved as-is
        Assert.assertEquals(
                "OffsetDateTime should be converted to its local date-time part without applying the offset",
                odt.toLocalDateTime(),
                result);
    }

    /**
     * OffsetDateTime with UTC offset converts correctly.
     */
    @Test
    public void testConvertOffsetDateTimeUtcToLocalDateTime() {
        OffsetDateTime odt = OffsetDateTime.of(2024, 1, 15, 10, 30, 0, 0, ZoneOffset.UTC);

        LocalDateTime result = cm.convertObject(odt, LocalDateTime.class);

        Assert.assertEquals(
                "OffsetDateTime with UTC offset should convert to its local date-time part",
                odt.toLocalDateTime(),
                result);
    }

    /**
     * OffsetDateTime with a positive offset converts correctly.
     */
    @Test
    public void testConvertOffsetDateTimePositiveOffsetToLocalDateTime() {
        OffsetDateTime odt = OffsetDateTime.of(2024, 6, 21, 8, 0, 0, 0, ZoneOffset.ofHours(5));

        LocalDateTime result = cm.convertObject(odt, LocalDateTime.class);

        Assert.assertEquals(
                "OffsetDateTime with positive offset should convert to its local date-time part",
                odt.toLocalDateTime(),
                result);
    }

    /**
     * An OffsetDateTime converted to Instant must produce the same instant
     * as calling {@link OffsetDateTime#toInstant()} directly.
     */
    @Test
    public void testConvertOffsetDateTimeToInstant() {
        OffsetDateTime odt = OffsetDateTime.of(2026, 5, 14, 15, 58, 9, 122371000, ZoneOffset.ofHours(-5));

        Instant result = cm.convertObject(odt, Instant.class);

        Assert.assertEquals(
                "OffsetDateTime should be converted to the equivalent Instant",
                odt.toInstant(),
                result);
    }

    @Test
    public void testConvertPositiveOffsetAcrossPreviousYearWithNanoseconds() {
        OffsetDateTime source = OffsetDateTime.of(2024, 1, 1, 0, 15, 30, 123456789, ZoneOffset.ofHours(14));

        Assert.assertEquals(Instant.parse("2023-12-31T10:15:30.123456789Z"), cm.convertObject(source, Instant.class));
        Assert.assertEquals(LocalDateTime.of(2024, 1, 1, 0, 15, 30, 123456789),
                cm.convertObject(source, LocalDateTime.class));
    }

    @Test
    public void testConvertNegativeOffsetAcrossNextYearWithNanoseconds() {
        OffsetDateTime source = OffsetDateTime.of(2024, 12, 31, 23, 45, 59, 987654321, ZoneOffset.ofHours(-12));

        Assert.assertEquals(Instant.parse("2025-01-01T11:45:59.987654321Z"), cm.convertObject(source, Instant.class));
        Assert.assertEquals(LocalDateTime.of(2024, 12, 31, 23, 45, 59, 987654321),
                cm.convertObject(source, LocalDateTime.class));
    }

    @Test
    public void testConvertTimestampPreservesNanoseconds() {
        Instant expected = Instant.parse("2024-06-21T08:00:00.123456789Z");
        Timestamp source = Timestamp.from(expected);

        Assert.assertEquals(expected, cm.convertObject(source, Instant.class));
        Assert.assertEquals(LocalDateTime.ofInstant(expected, ZoneId.systemDefault()),
                cm.convertObject(source, LocalDateTime.class));
    }

    @Test
    public void testConvertStringsPreservesNanoseconds() {
        Assert.assertEquals(Instant.parse("2024-06-21T08:00:00.123456789Z"),
                cm.convertObject("2024-06-21T08:00:00.123456789Z", Instant.class));
        Assert.assertEquals(LocalDateTime.of(2024, 6, 21, 8, 0, 0, 123456789),
                cm.convertObject("2024-06-21 08:00:00.123456789", LocalDateTime.class));
    }

    @Test
    public void testConvertCalendarPreservesInstantAndLocalFields() {
        Calendar source = new GregorianCalendar(TimeZone.getTimeZone("GMT+14:00"));
        source.clear();
        source.set(2024, Calendar.JANUARY, 1, 0, 15, 30);

        Assert.assertEquals(Instant.parse("2023-12-31T10:15:30Z"), cm.convertObject(source, Instant.class));
        Assert.assertEquals(LocalDateTime.of(2024, 1, 1, 0, 15, 30),
                cm.convertObject(source, LocalDateTime.class));
    }

    @Test
    public void testConvertUtilDatePreservesInstantAndLocalFields() {
        Instant expected = Instant.parse("2024-06-21T08:00:00Z");
        Date source = Date.from(expected);

        Assert.assertEquals(expected, cm.convertObject(source, Instant.class));
        Assert.assertEquals(LocalDateTime.ofInstant(expected, ZoneId.systemDefault()),
                cm.convertObject(source, LocalDateTime.class));
    }

    @Test
    public void testConvertNullTemporalValues() {
        Assert.assertNull(cm.convertObject(null, Instant.class));
        Assert.assertNull(cm.convertObject(null, LocalDateTime.class));
    }

    @Test
    public void testConvertInvalidTemporalStringsThrowsConversionException() {
        Assert.assertThrows(ConversionException.class, () -> cm.convertObject("not-a-date", Instant.class));
        Assert.assertThrows(ConversionException.class, () -> cm.convertObject("not-a-date", LocalDateTime.class));
    }

    @Test
    public void testConvertUnsupportedTemporalSourceThrowsConversionException() {
        Object source = new Object();

        Assert.assertThrows(ConversionException.class, () -> cm.convertObject(source, Instant.class));
        Assert.assertThrows(ConversionException.class, () -> cm.convertObject(source, LocalDateTime.class));
    }
}
