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
import org.eclipse.persistence.json.bind.defaultmapping.dates.model.*;
import org.eclipse.persistence.json.bind.defaultmapping.generics.model.ScalarValueWrapper;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * This class contains tests for marshalling/unmarshalling dates.
 *
 * @author Dmitry Kornilov
 */
public class DatesTest {

    private final Jsonb jsonb = (new JsonBindingBuilder()).build();

    @Test
    public void testDate() throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        final Date parsedDate = sdf.parse("04.03.2015");

        DatePojo pojo = new DatePojo();
        pojo.customDate = parsedDate;
        pojo.defaultFormatted = parsedDate;
        pojo.millisFormatted = parsedDate;

        // marshal to ISO format
        final String expected = "{\"defaultFormatted\":\"2015-03-04T00:00:00\",\"millisFormatted\":\"1425423600000\",\"customDate\":\"00:00:00 | 04-03-2015\"}";
        assertEquals(expected, jsonb.toJson(pojo));
        DatePojo result = jsonb.fromJson(expected, DatePojo.class);
        assertEquals(parsedDate, result.customDate);
        assertEquals(parsedDate, result.defaultFormatted);
        assertEquals(parsedDate, result.millisFormatted);
    }

    @Test
    public void testCalendar() {
        final Calendar timeCalendar = new Calendar.Builder().setDate(2015, Calendar.APRIL, 3).setTimeOfDay(11, 11, 10).build();

        CalendarPojo calendarPojo = new CalendarPojo();
        calendarPojo.customCalendar = timeCalendar;
        calendarPojo.defaultFormatted = timeCalendar;
        calendarPojo.millisFormatted = timeCalendar;

        // marshal to ISO_DATE
        final String expected = "{\"defaultFormatted\":\"2015-04-03T11:11:10+02:00[Europe/Prague]\",\"millisFormatted\":\"1428052270000\",\"customCalendar\":\"11:11:10 | 03-04-2015, +0200\"}";
        assertEquals(expected, jsonb.toJson(calendarPojo));

        // marshal to ISO_DATE_TIME
        CalendarPojo result = jsonb.fromJson(expected, CalendarPojo.class);
        assertEquals(timeCalendar.getTime(), result.customCalendar.getTime());
        assertEquals(timeCalendar.getTime(), result.millisFormatted.getTime());
        assertEquals(timeCalendar.getTime(), result.defaultFormatted.getTime());
    }

    @Test
    public void testCalendarWithNonDefaultTimeZone() {
        ZoneId zoneId = ZoneId.of("Australia/Sydney");
        final Calendar timeCalendar = new Calendar.Builder().setDate(2015, Calendar.APRIL, 3).setTimeOfDay(10, 10, 10)
                .setTimeZone(TimeZone.getTimeZone(zoneId)).build();

        CalendarPojo calendarPojo = new CalendarPojo();
        calendarPojo.customCalendar = timeCalendar;
        calendarPojo.defaultFormatted = timeCalendar;
        calendarPojo.millisFormatted = timeCalendar;

        // marshal to ISO_DATE
        final String expected = "{\"defaultFormatted\":\"2015-04-03T10:10:10+11:00[Australia/Sydney]\",\"millisFormatted\":\"1428016210000\",\"customCalendar\":\"10:10:10 | 03-04-2015, +1100\"}";
        assertEquals(expected, jsonb.toJson(calendarPojo));

        // marshal to ISO_DATE_TIME
        CalendarPojo result = jsonb.fromJson(expected, CalendarPojo.class);
        assertEquals(timeCalendar.getTime(), result.customCalendar.getTime());
        assertEquals(timeCalendar.getTime(), result.millisFormatted.getTime());
        assertEquals(timeCalendar.getTime(), result.defaultFormatted.getTime());
    }

    @Test
    public void testCalendarWithoutTime() {
        final Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.clear();
        dateCalendar.set(2015, Calendar.APRIL, 3);

        CalendarPojo calendarPojo = new CalendarPojo();
        calendarPojo.customCalendar = dateCalendar;
        calendarPojo.defaultFormatted = dateCalendar;
        calendarPojo.millisFormatted = dateCalendar;

        // marshal to ISO_DATE
        final String expected = "{\"defaultFormatted\":\"2015-04-03\",\"millisFormatted\":\"1428012000000\",\"customCalendar\":\"00:00:00 | 03-04-2015, +0200\"}";
        assertEquals(expected, jsonb.toJson(calendarPojo));

        // marshal to ISO_DATE_TIME
        CalendarPojo result = jsonb.fromJson(expected, CalendarPojo.class);
        assertEquals(dateCalendar.getTime(), result.customCalendar.getTime());
        assertEquals(dateCalendar.getTime(), result.millisFormatted.getTime());
        assertEquals(dateCalendar.getTime(), result.defaultFormatted.getTime());
    }

    @Test
    public void testMarshalGregorianCalendar() {
        final Calendar dateGregorianCalendar = GregorianCalendar.getInstance();
        dateGregorianCalendar.clear();
        dateGregorianCalendar.set(2015, Calendar.APRIL, 3);

        // marshal to ISO_DATE
        assertEquals("{\"value\":\"2015-04-03\"}", jsonb.toJson(new ScalarValueWrapper<>(dateGregorianCalendar)));

        // marshal to ISO_DATE_TIME
        final Calendar dateTimeGregorianCalendar = new Calendar.Builder().setDate(2015, 3, 3).build();
        assertEquals("{\"value\":\"2015-04-03T00:00:00+02:00[Europe/Prague]\"}", jsonb.toJson(new ScalarValueWrapper<>(dateTimeGregorianCalendar)));
    }

    @Test
    public void testMarshalTimeZone() {
        assertEquals("{\"value\":\"Europe/Prague\"}", jsonb.toJson(new ScalarValueWrapper<>(TimeZone.getTimeZone("Europe/Prague"))));
        assertEquals("{\"value\":\"Europe/Prague\"}", jsonb.toJson(new ScalarValueWrapper<>(SimpleTimeZone.getTimeZone("Europe/Prague"))));
    }

    @Test
    public void testMarshalInstant() {

        final Instant instant = Instant.parse("2015-03-03T23:00:00Z");
        InstantPojo instantPojo = new InstantPojo(instant);

        final String expected = "{\"defaultFormatted\":\"2015-03-03T23:00:00Z\",\"millisFormatted\":\"2015-03-03T23:00:00Z\",\"instant\":\"2015-03-03T23:00:00Z\"}";
        assertEquals(expected, jsonb.toJson(instantPojo));

        InstantPojo result = jsonb.fromJson(expected, InstantPojo.class);
        assertEquals(instant, result.defaultFormatted);
        assertEquals(instant, result.millisFormatted);
        assertEquals(instant, result.instant);
    }

    @Test
    public void testMarshalDuration() {
        assertEquals("{\"value\":\"PT5H4M\"}", jsonb.toJson(new ScalarValueWrapper<>(Duration.ofHours(5).plusMinutes(4))));
    }

    @Test
    public void testMarshalPeriod() {
        assertEquals("{\"value\":\"P10Y\"}", jsonb.toJson(new ScalarValueWrapper<>(Period.between(LocalDate.of(1960, Month.JANUARY, 1), LocalDate.of(1970, Month.JANUARY, 1)))));
    }

    @Test
    public void testLocalDate() {
        LocalDate localDate = LocalDate.of(2015, Month.APRIL, 10);

        LocalDatePojo pojo = new LocalDatePojo(localDate);
        String expected = "{\"defaultFormatted\":\"2015-04-10\",\"millisFormatted\":\"1428616800000\",\"customLocalDate\":\"10-04-2015\"}";
        assertEquals(expected, jsonb.toJson(pojo));

        LocalDatePojo result = jsonb.fromJson(expected, LocalDatePojo.class);
        assertEquals(localDate, result.customLocalDate);
        assertEquals(localDate, result.millisFormatted);
        assertEquals(localDate, result.defaultFormatted);
    }

    @Test
    public void testlLocalTime() {
        Jsonb jsonb = getJsonbWithMillisIgnored();
        final LocalTime localTime = LocalTime.of(22, 33);
        LocalTimePojo localTimePojo = new LocalTimePojo(localTime);
        String expected = "{\"defaultFormatted\":\"22:33:00\",\"localTime\":\"22:33:00\"}";
        assertEquals(expected, jsonb.toJson(localTimePojo));

        LocalTimePojo result = jsonb.fromJson(expected, LocalTimePojo.class);
        assertEquals(localTime, result.defaultFormatted);
        assertEquals(localTime, result.localTime);
    }

    private Jsonb getJsonbWithMillisIgnored() {
        JsonbConfig config = new JsonbConfig();
        config.withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {
            @Override
            public boolean isVisible(Field field) {
                return !field.getName().startsWith("millis");
            }

            @Override
            public boolean isVisible(Method method) {
                return false;
            }
        });
        return JsonbBuilder.create(config);
    }

    @Test
    public void testMarshalLocalDateTime() {
        final LocalDateTime dateTime = LocalDateTime.of(2015, 2, 16, 13, 21);
        LocalDateTimePojo pojo = new LocalDateTimePojo(dateTime);

        String expected = "{\"defaultFormatted\":\"2015-02-16T13:21:00\",\"millisFormatted\":\"1424089260000\",\"customLocalDate\":\"16-02-2015--00:21:13\"}";
        assertEquals(expected, jsonb.toJson(pojo));

        LocalDateTimePojo result = jsonb.fromJson(expected, LocalDateTimePojo.class);
        assertEquals(dateTime, result.defaultFormatted);
        assertEquals(dateTime, result.millisFormatted);
        assertEquals(dateTime, result.customLocalDate);
    }

    @Test
    public void testZonedDateTime() {
        final ZonedDateTime dateTime = ZonedDateTime.of(2015, 2, 16, 13, 21, 0, 0, ZoneId.of("Asia/Almaty"));
        ZonedDateTimePojo pojo = new ZonedDateTimePojo(dateTime);

        String expected = "{\"defaultFormatted\":\"2015-02-16T13:21:00+06:00[Asia/Almaty]\",\"millisFormatted\":\"1424071260000\",\"customZonedDate\":\"+06Asia/Almaty | 16-02-2015--00:21:13\"}";
        assertEquals(expected, jsonb.toJson(pojo));

        ZonedDateTimePojo result = jsonb.fromJson(expected, ZonedDateTimePojo.class);
        assertEquals(dateTime, result.defaultFormatted);
//        assertEquals(dateTime, result.millisFormatted);
        assertEquals(dateTime, result.customZonedDate);

        //time zone and seconds omitted
        ZonedDateTimePojo result1 = jsonb.fromJson("{\"defaultFormatted\":\"2015-02-16T13:21+06:00\"}", ZonedDateTimePojo.class);
        assertEquals(dateTime.getHour(), result1.defaultFormatted.getHour());
        assertEquals(dateTime.getOffset(), result1.defaultFormatted.getOffset());

    }

    @Test
    public void testMarshalZoneId() {
        assertEquals("{\"value\":\"Europe/Prague\"}", jsonb.toJson(new ScalarValueWrapper<>(ZoneId.of("Europe/Prague"))));
    }

    @Test
    public void testMarshalZoneOffset() {
        assertEquals("{\"value\":\"+02:00\"}", jsonb.toJson(new ScalarValueWrapper<>(ZoneOffset.of("+02:00"))));
    }

    @Test
    public void testMarshalOffsetDateTime() {
        final OffsetDateTime dateTime = OffsetDateTime.of(2015, 2, 16, 13, 21, 0, 0, ZoneOffset.of("+05:00"));
        OffsetDateTimePojo pojo = new OffsetDateTimePojo(dateTime);

        String expected = "{\"defaultFormatted\":\"2015-02-16T13:21:00+05:00\",\"millisFormatted\":\"1424074860000\",\"offsetDateTime\":\"+0500 16-02-2015--00:21:13\"}";
        assertEquals(expected, jsonb.toJson(pojo));

        OffsetDateTimePojo result = jsonb.fromJson(expected, OffsetDateTimePojo.class);
        assertEquals(dateTime, result.defaultFormatted);
        //assertEquals(dateTime, result.millisFormatted); can not parse zone offset from millis other than default
        assertEquals(dateTime, result.offsetDateTime);

    }

    @Test
    public void testMarshalOffsetTime() {
        Jsonb jsonb = getJsonbWithMillisIgnored();
        final OffsetTime dateTime = OffsetTime.of(13, 21, 15, 0, ZoneOffset.of("+05:00"));
        OffsetTimePojo pojo = new OffsetTimePojo(dateTime);

        String expected = "{\"defaultFormatted\":\"13:21:15+05:00\",\"offsetTime\":\"13:21:15+0500\"}";
        assertEquals(expected, jsonb.toJson(pojo));

        OffsetTimePojo result = jsonb.fromJson(expected, OffsetTimePojo.class);
        assertEquals(dateTime, result.defaultFormatted);
        assertEquals(dateTime, result.offsetTime);
    }

    @Test
    public void testClassLevel() throws ParseException {
        ClassLevelDateAnnotation pojo = new ClassLevelDateAnnotation();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        pojo.date = sdf.parse("04.03.2015");
        final ZoneId zone = ZoneId.of("Asia/Almaty");
        pojo.calendar = new Calendar.Builder().setDate(2015, Calendar.APRIL, 3).setTimeOfDay(11, 11, 10).setTimeZone(TimeZone.getTimeZone(zone)).build();
        pojo.zonedDateTime = ZonedDateTime.of(2015, 4, 3, 13, 21, 0, 0, zone);
        pojo.defaultZoned = pojo.zonedDateTime;
        pojo.localDateTime  = LocalDateTime.of(2015, 4, 3, 13, 21, 0, 0);

        String expected = "{\"date\":\"04-03-2015 00:00:00\",\"localDateTime\":\"03-04-2015 13:21:00\",\"calendar\":\"+06 ALMT ven. avril 03-04-2015 11:11:10\",\"defaultZoned\":\"2015-04-03T13:21:00+06:00[Asia/Almaty]\",\"zonedDateTime\":\"+06 ALMT ven. avril 03-04-2015 13:21:00\"}";
        assertEquals(expected, jsonb.toJson(pojo));

        ClassLevelDateAnnotation result = jsonb.fromJson(expected, ClassLevelDateAnnotation.class);
        assertEquals(pojo.date, result.date);
        assertEquals(pojo.localDateTime, result.localDateTime);
        assertEquals(pojo.calendar.getTime(), result.calendar.getTime());
        assertEquals(pojo.zonedDateTime, result.zonedDateTime);
    }

    @Test
    public void testGlobalConfigDateFormat() {
        JsonbConfig config = new JsonbConfig();
        config.withDateFormat("X z E MMMM dd-MM-yyyy HH:mm:ss", Locale.FRENCH);
        Jsonb jsonb = JsonbBuilder.create(config);

        ZonedDateTime dateTime = ZonedDateTime.of(2015, 4, 3, 13, 21, 0, 0, ZoneId.of("Asia/Almaty"));
        String expected = "{\"value\":\"+06 ALMT ven. avril 03-04-2015 13:21:00\"}";
        assertEquals(expected, jsonb.toJson(new ScalarValueWrapper<>(dateTime)));

        ScalarValueWrapper<ZonedDateTime> result = jsonb.fromJson(expected, new ScalarValueWrapper<ZonedDateTime>() {
        }.getClass());

        assertEquals(dateTime, result.getValue());

    }
}
