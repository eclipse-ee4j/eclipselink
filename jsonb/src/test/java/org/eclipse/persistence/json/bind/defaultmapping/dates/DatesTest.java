/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.json.bind.defaultmapping.dates;

import org.eclipse.persistence.json.bind.JsonBindingBuilder;
import org.junit.Test;

import javax.json.bind.Jsonb;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

/**
 * This class contains tests for marshalling/unmarshalling dates.
 *
 * @author Dmitry Kornilov
 */
public class DatesTest {
    @Test
    public void testMarshalDate() throws ParseException {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        final Date parsedDate = sdf.parse("04.03.2015");

        // marshal to ISO format
        assertEquals("\"2015-03-04T00:00:00\"", jsonb.toJson(parsedDate));
    }

    @Test
    public void testMarshalDateTime() throws ParseException {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        final Date parsedDate = sdf.parse("04.03.2015 12:10:20");

        // marshal to ISO format
        assertEquals("\"2015-03-04T12:10:20\"", jsonb.toJson(parsedDate));
    }

    @Test
    public void testMarshalCalendar() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.clear();
        dateCalendar.set(2015, Calendar.APRIL, 3);

        // marshal to ISO_DATE
        assertEquals("\"2015-04-03\"", jsonb.toJson(dateCalendar));

        // marshal to ISO_DATE_TIME
        Calendar dateTimeCalendar = new Calendar.Builder().setDate(2015, 3, 3).build();
        assertEquals("\"2015-04-03T00:00:00\"", jsonb.toJson(dateTimeCalendar));
    }

    @Test
    public void testMarshalGregorianCalendar() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final Calendar dateGregorianCalendar = GregorianCalendar.getInstance();
        dateGregorianCalendar.clear();
        dateGregorianCalendar.set(2015, Calendar.APRIL, 3);

        // marshal to ISO_DATE
        assertEquals("\"2015-04-03\"", jsonb.toJson(dateGregorianCalendar));

        // marshal to ISO_DATE_TIME
        final Calendar dateTimeGregorianCalendar = new Calendar.Builder().setDate(2015, 3, 3).build();
        assertEquals("\"2015-04-03T00:00:00\"", jsonb.toJson(dateTimeGregorianCalendar));
    }

    @Test
    public void testMarshalTimeZone() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("\"Europe/Prague\"", jsonb.toJson(TimeZone.getTimeZone("Europe/Prague")));
        assertEquals("\"Europe/Prague\"", jsonb.toJson(SimpleTimeZone.getTimeZone("Europe/Prague")));
    }

    @Test
    public void testMarshalInstant() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("\"2015-03-03T23:00:00Z\"", jsonb.toJson(Instant.parse("2015-03-03T23:00:00Z")));
    }

    @Test
    public void testMarshalDuration() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("\"PT5H4M\"", jsonb.toJson(Duration.ofHours(5).plusMinutes(4)));
    }

    @Test
    public void testMarshalPeriod() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("\"P10Y\"", jsonb.toJson(Period.between(LocalDate.of(1960, Month.JANUARY, 1), LocalDate.of(1970, Month.JANUARY, 1))));
    }

    @Test
    public void testMarshalLocalDate() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("\"2013-08-10\"", jsonb.toJson(LocalDate.of(2013, Month.AUGUST, 10)));
    }

    @Test
    public void testMarshalLocalTime() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("\"22:33:00\"", jsonb.toJson(LocalTime.of(22, 33)));
    }

    @Test
    public void testMarshalLocalDateTime() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("\"2015-02-16T13:21:00\"", jsonb.toJson(LocalDateTime.of(2015, 2, 16, 13, 21)));
    }

    @Test
    public void testMarshalZonedDateTime() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("\"2015-02-16T13:21:00+01:00[Europe/Prague]\"",
                jsonb.toJson(ZonedDateTime.of(2015, 2, 16, 13, 21, 0, 0, ZoneId.of("Europe/Prague"))));
    }

    @Test
    public void testMarshalZoneId() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("\"Europe/Prague\"", jsonb.toJson(ZoneId.of("Europe/Prague")));
    }

    @Test
    public void testMarshalZoneOffset() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("\"+02:00\"", jsonb.toJson(ZoneOffset.of("+02:00")));
    }

    @Test
    public void testMarshalOffsetDateTime() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("\"2015-02-16T13:21:00+02:00\"",
                jsonb.toJson(OffsetDateTime.of(2015, 2, 16, 13, 21, 0, 0, ZoneOffset.of("+02:00"))));
    }

    @Test
    public void testMarshalOffsetTime() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("\"13:21:15.000000016+02:00\"", jsonb.toJson(OffsetTime.of(13, 21, 15, 16, ZoneOffset.of("+02:00"))));
    }
}
