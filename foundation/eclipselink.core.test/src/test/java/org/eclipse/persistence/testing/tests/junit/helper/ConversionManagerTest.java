/*
 * Copyright (c) 2018, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.junit.helper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

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
}
