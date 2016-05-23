/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * <p>
 * Contributors:
 * Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.json.bind.defaultmapping.typeConvertors;

import org.eclipse.persistence.json.bind.internal.JsonBindingBuilder;
import org.eclipse.persistence.json.bind.defaultmapping.typeConvertors.model.BigDecimalWrapper;
import org.eclipse.persistence.json.bind.defaultmapping.typeConvertors.model.BigIntegerWrapper;
import org.eclipse.persistence.json.bind.defaultmapping.typeConvertors.model.ByteArrayWrapper;
import org.eclipse.persistence.json.bind.defaultmapping.typeConvertors.model.CalendarWrapper;
import org.eclipse.persistence.json.bind.defaultmapping.typeConvertors.model.StringWrapper;
import org.eclipse.persistence.json.bind.internal.conversion.BooleanTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.CharacterTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.ConvertersMapTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.DateTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.DoubleTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.EnumTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.FloatTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.InstantTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.IntegerTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.JsonbDateFormatter;
import org.eclipse.persistence.json.bind.internal.conversion.LocalDateTimeTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.LocalDateTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.LocalTimeTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.LongTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.NumberTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.OffsetDateTimeTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.OffsetTimeTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.OptionalDoubleTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.OptionalIntTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.OptionalLongTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.ShortTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.TimeZoneTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.TypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.URITypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.URLTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.ZonedDateTimeTypeConverter;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.model.CustomizationBuilder;
import org.eclipse.persistence.json.bind.model.PropertyCustomization;
import org.junit.Assert;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.config.BinaryDataStrategy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.TimeZone;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * This class contains Convertor tests
 *
 * @author David Král
 */
public class ConvertorsTest {

    private TypeConverter converter = ConvertersMapTypeConverter.getInstance();

    @Test
    public void testCharacterConvetor() throws NoSuchFieldException {
        CharacterTypeConverter characterTypeConverter = new CharacterTypeConverter();
        assertEquals("\uFFFF", characterTypeConverter.toJson('\uFFFF', null));
        assertEquals('\uFFFF', characterTypeConverter.fromJson("\uFFFF", Character.class, null), 0);
    }

    @Test
    public void testBooleanConvetor() throws NoSuchFieldException {
        BooleanTypeConverter booleanTypeConverter = new BooleanTypeConverter();
        assertEquals("true", booleanTypeConverter.toJson(true, null));
        assertEquals(true, booleanTypeConverter.fromJson("true", Boolean.class, null));
    }

    @Test
    public void testCalendarConvetor() throws NoSuchFieldException {
        Jsonb jsonb = (new JsonBindingBuilder()).build();
        final Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.clear();
        dateCalendar.set(2015, Calendar.APRIL, 3);
        CalendarWrapper calendarWrapper = new CalendarWrapper();
        calendarWrapper.calendar = dateCalendar;

        // marshal to ISO_DATE
        assertEquals("{\"calendar\":\"2015-04-03\"}", jsonb.toJson(calendarWrapper));
        assertEquals(dateCalendar, jsonb.fromJson("{\"calendar\":\"2015-04-03\"}", CalendarWrapper.class).calendar);

        // marshal to ISO_DATE_TIME
        Calendar dateTimeCalendar = new Calendar.Builder().setDate(2015, 3, 3).build();
        calendarWrapper.calendar = dateTimeCalendar;

        assertEquals("{\"calendar\":\"2015-04-03T00:00:00+02:00[Europe/Prague]\"}", jsonb.toJson(calendarWrapper));
        assertEquals(dateTimeCalendar, jsonb.fromJson("{\"calendar\":\"2015-04-03T00:00:00\"}", CalendarWrapper.class).calendar);

        jsonb = (new JsonBindingBuilder()).withConfig(new JsonbConfig().withStrictIJSON(true)).build();
        calendarWrapper.calendar = dateCalendar;
        assertEquals("{\"calendar\":\"2015-04-03T00:00:00+02:00[Europe/Prague]\"}", jsonb.toJson(calendarWrapper));
        assertEquals(dateTimeCalendar, jsonb.fromJson("{\"calendar\":\"2015-04-03T00:00:00\"}", CalendarWrapper.class).calendar);
    }

    @Test
    public void testDateConvetor() throws NoSuchFieldException, ParseException {
        DateTypeConverter dateTypeConverter = new DateTypeConverter();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date parsedDate = sdf.parse("04.03.2015 12:10:20");

        final PropertyCustomization defaultCustomization = buildDefaultDateFormatterCustomization();

        assertEquals("2015-03-04T12:10:20", dateTypeConverter.toJson(parsedDate, defaultCustomization));
        assertEquals(parsedDate, dateTypeConverter.fromJson("2015-03-04T12:10:20", null, defaultCustomization));

        sdf = new SimpleDateFormat("dd.MM.yyyy");
        parsedDate = sdf.parse("04.03.2015");

        assertEquals("2015-03-04T00:00:00", dateTypeConverter.toJson(parsedDate, defaultCustomization));
        assertEquals(parsedDate, dateTypeConverter.fromJson("2015-03-04T00:00:00", null, defaultCustomization));
    }

    @Test
    public void testDoubleConvetor() {
        DoubleTypeConverter doubleTypeConverter = new DoubleTypeConverter();
        assertEquals("1.2", doubleTypeConverter.toJson(1.2, null));
        assertEquals(1.2, doubleTypeConverter.fromJson("1.2", Double.class, null), 0);

        // Double.NEGATIVE_INFINITY
        assertEquals("NEGATIVE_INFINITY", doubleTypeConverter.toJson(Double.NEGATIVE_INFINITY, null));
        assertEquals(Double.NEGATIVE_INFINITY, doubleTypeConverter.fromJson("NEGATIVE_INFINITY", Double.class, null), 0);

        // Double.POSITIVE_INFINITY
        assertEquals("POSITIVE_INFINITY", doubleTypeConverter.toJson(Double.POSITIVE_INFINITY, null));
        assertEquals(Double.POSITIVE_INFINITY, doubleTypeConverter.fromJson("POSITIVE_INFINITY", Double.class, null), 0);

        // Double.NaN
        assertEquals("NaN", doubleTypeConverter.toJson(Double.NaN, null));
        assertEquals(Double.NaN, doubleTypeConverter.fromJson("NaN", Double.class, null), 0);
    }

    @Test
    public void testFloatConvetor() {
        FloatTypeConverter floatTypeConverter = new FloatTypeConverter();
        assertEquals("1.2", floatTypeConverter.toJson(1.2f, null));
        assertEquals(1.2f, floatTypeConverter.fromJson("1.2", Float.class, null), 0);
    }

    @Test
    public void testInstantConvetor() {
        InstantTypeConverter instantTypeConverter = new InstantTypeConverter();
        assertEquals("2015-03-03T23:00:00Z", instantTypeConverter.toJson(Instant.parse("2015-03-03T23:00:00Z"), null));
        assertEquals(Instant.parse("2015-03-03T23:00:00Z"), instantTypeConverter.fromJson("2015-03-03T23:00:00Z", Instant.class, null));
    }

    @Test
    public void testIntegerConvetor() {

        IntegerTypeConverter integerTypeConverter = new IntegerTypeConverter();
        assertEquals("1", integerTypeConverter.toJson(1, null));
        assertEquals(1, integerTypeConverter.fromJson("1", Integer.class, null), 0);
        assertEquals(1, integerTypeConverter.fromJson("1", int.class, null), 0);
        Integer actual = converter.fromJson("1", int.class, null);
        assertEquals(1, actual, 0);
    }

    @Test
    public void testLocalDateTimeConvetor() {
        LocalDateTimeTypeConverter localDateTimeTypeConverter = new LocalDateTimeTypeConverter();

        PropertyCustomization customization = buildDefaultDateFormatterCustomization();
        assertEquals("2015-02-16T13:21:00", localDateTimeTypeConverter.toJson(LocalDateTime.of(2015, 2, 16, 13, 21), customization));
        assertEquals(LocalDateTime.of(2015, 2, 16, 13, 21), localDateTimeTypeConverter.fromJson("2015-02-16T13:21:00", LocalDateTime.class, customization));
    }

    @Test
    public void testLocalDateConvetor() {
        LocalDateTypeConverter localDateTypeConverter = new LocalDateTypeConverter();

        final PropertyCustomization defaultCustomization = buildDefaultDateFormatterCustomization();

        assertEquals("2013-08-10", localDateTypeConverter.toJson(LocalDate.of(2013, Month.AUGUST, 10), defaultCustomization));
        assertEquals(LocalDate.of(2013, Month.AUGUST, 10), localDateTypeConverter.fromJson("2013-08-10", LocalDate.class, defaultCustomization));
    }

    private PropertyCustomization buildDefaultDateFormatterCustomization() {
        JsonbDateFormatter formatter = new JsonbDateFormatter(JsonbDateFormat.DEFAULT_FORMAT, Locale.getDefault());
        return new CustomizationBuilder()
                .setDateFormatter(formatter).buildPropertyCustomization();
    }

    @Test
    public void testLocalTimeConvetor() {
        LocalTimeTypeConverter localTimeTypeConverter = new LocalTimeTypeConverter();

        PropertyCustomization customization = buildDefaultDateFormatterCustomization();
        assertEquals("22:33:00", localTimeTypeConverter.toJson(LocalTime.of(22, 33), customization));
        assertEquals(LocalTime.of(22, 33), localTimeTypeConverter.fromJson("22:33:00", LocalTime.class, customization));
    }

    @Test
    public void testLongConvetor() {
        LongTypeConverter longTypeConverter = new LongTypeConverter();

        assertEquals("10", longTypeConverter.toJson(10L, null));
        assertEquals(10L, longTypeConverter.fromJson("10", Long.class, null), 0);
    }

    @Test
    public void testNumberConvetor() {
        NumberTypeConverter numberTypeConverter = new NumberTypeConverter();

        assertEquals("10.0", numberTypeConverter.toJson(10L, null));
        assertEquals("10.1", numberTypeConverter.toJson(10.1, null));
        assertEquals(new BigDecimal("10.2"), numberTypeConverter.fromJson("10.2", null, null));
    }

    @Test
    public void testOffsetDateTimeConvetor() {
        OffsetDateTimeTypeConverter offsetDateTimeTypeConverter = new OffsetDateTimeTypeConverter();

        PropertyCustomization customization = buildDefaultDateFormatterCustomization();
        assertEquals("2015-02-16T13:21:00+02:00",
                offsetDateTimeTypeConverter.toJson(OffsetDateTime.of(2015, 2, 16, 13, 21, 0, 0, ZoneOffset.of("+02:00")), customization));
        assertEquals(OffsetDateTime.of(2015, 2, 16, 13, 21, 0, 0, ZoneOffset.of("+02:00")), offsetDateTimeTypeConverter.fromJson("2015-02-16T13:21:00+02:00", null, customization));
    }

    @Test
    public void testOffsetTimeConvetor() {
        OffsetTimeTypeConverter offsetTimeTypeConverter = new OffsetTimeTypeConverter();

        PropertyCustomization customization = buildDefaultDateFormatterCustomization();
        assertEquals("13:21:15.000000016+02:00", offsetTimeTypeConverter.toJson(OffsetTime.of(13, 21, 15, 16, ZoneOffset.of("+02:00")), customization));
        assertEquals(OffsetTime.of(13, 21, 15, 16, ZoneOffset.of("+02:00")), offsetTimeTypeConverter.fromJson("13:21:15.000000016+02:00", null, customization));
    }

    @Test
    public void testOptionalIntConvetor() {
        OptionalIntTypeConverter optionalIntTypeConverter = new OptionalIntTypeConverter();

        assertEquals("null", optionalIntTypeConverter.toJson(OptionalInt.empty(), null));
        assertEquals("10", optionalIntTypeConverter.toJson(OptionalInt.of(10), null));
        assertEquals(OptionalInt.empty(), optionalIntTypeConverter.fromJson("null", null, null));
        assertEquals(OptionalInt.of(10), optionalIntTypeConverter.fromJson("10", null, null));
    }

    @Test
    public void testOptionalDoubleConvetor() {
        OptionalDoubleTypeConverter optionalDoubleTypeConverter = new OptionalDoubleTypeConverter();

        assertEquals("null", optionalDoubleTypeConverter.toJson(OptionalDouble.empty(), null));
        assertEquals("10.0", optionalDoubleTypeConverter.toJson(OptionalDouble.of(10), null));
        assertEquals("10.1", optionalDoubleTypeConverter.toJson(OptionalDouble.of(10.1), null));
        assertEquals("POSITIVE_INFINITY", optionalDoubleTypeConverter.toJson(OptionalDouble.of(Double.POSITIVE_INFINITY), null));
        assertEquals("NEGATIVE_INFINITY", optionalDoubleTypeConverter.toJson(OptionalDouble.of(Double.NEGATIVE_INFINITY), null));
        assertEquals("NaN", optionalDoubleTypeConverter.toJson(OptionalDouble.of(Double.NaN), null));
        assertEquals(OptionalDouble.empty(), optionalDoubleTypeConverter.fromJson("null", null, null));
        assertEquals(OptionalDouble.of(10), optionalDoubleTypeConverter.fromJson("10", null, null));
        assertEquals(OptionalDouble.of(10.1), optionalDoubleTypeConverter.fromJson("10.1", null, null));
        assertEquals(OptionalDouble.of(Double.POSITIVE_INFINITY), optionalDoubleTypeConverter.fromJson("POSITIVE_INFINITY", null, null));
        assertEquals(OptionalDouble.of(Double.NEGATIVE_INFINITY), optionalDoubleTypeConverter.fromJson("NEGATIVE_INFINITY", null, null));
        assertEquals(OptionalDouble.of(Double.NaN), optionalDoubleTypeConverter.fromJson("NaN", null, null));
    }

    @Test
    public void testOptionalLongConvetor() {
        OptionalLongTypeConverter optionalLongTypeConverter = new OptionalLongTypeConverter();

        assertEquals("null", optionalLongTypeConverter.toJson(OptionalLong.empty(), null));
        assertEquals("10", optionalLongTypeConverter.toJson(OptionalLong.of(10L), null));
        assertEquals(OptionalLong.empty(), optionalLongTypeConverter.fromJson("null", null, null));
        assertEquals(OptionalLong.of(10L), optionalLongTypeConverter.fromJson("10", null, null));
    }

    @Test
    public void testShortConvetor() {
        ShortTypeConverter shortTypeConverter = new ShortTypeConverter();

        assertEquals("10", shortTypeConverter.toJson((short) 10, null));
        assertEquals((short) 10, shortTypeConverter.fromJson("10", null, null), 0);
    }

    @Test
    public void testStringConvetor() {
        Jsonb jsonb = (new JsonBindingBuilder()).withConfig(new JsonbConfig().withStrictIJSON(true)).build();
        StringWrapper stringWrapper = new StringWrapper();
        stringWrapper.string = "test";

        assertEquals("{\"string\":\"test\"}", jsonb.toJson(stringWrapper));
        try {
            stringWrapper.string = "\uDEAD";
            assertEquals("{\"string\":\"?\"}", jsonb.toJson(stringWrapper));
            Assert.fail();
        } catch (JsonbException exception) {
            assertEquals(Messages.getMessage(MessageKeys.UNPAIRED_SURROGATE), exception.getMessage());
        }
        try {
            stringWrapper.string = "\uD800\uDEAD";
            jsonb.toJson(stringWrapper);
        } catch (JsonbException exception) {
            exception.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testTimeZoneConvetor() {
        TimeZoneTypeConverter timeZoneTypeConverter = new TimeZoneTypeConverter();

        assertEquals("Europe/Prague", timeZoneTypeConverter.toJson(TimeZone.getTimeZone("Europe/Prague"), null));
        assertEquals(TimeZone.getTimeZone("Europe/Prague"), timeZoneTypeConverter.fromJson("Europe/Prague", null, null));

        try {
            timeZoneTypeConverter.fromJson("PST", null, null);
            Assert.fail();
        } catch (JsonbException ex) {
            assertEquals("Unsupported TimeZone: PST", ex.getMessage());
        }
    }

    @Test
    public void testUriConvertor() throws URISyntaxException {
        URITypeConverter uriTypeConverter = new URITypeConverter();

        assertEquals("http://www.oracle.com", uriTypeConverter.toJson(new URI("http://www.oracle.com"), null));
        assertEquals(new URI("http://www.oracle.com"), uriTypeConverter.fromJson("http://www.oracle.com", null, null));
    }

    @Test
    public void testUrlConvertor() throws MalformedURLException {
        URLTypeConverter urlTypeConverter = new URLTypeConverter();

        assertEquals("http://www.oracle.com", urlTypeConverter.toJson(new URL("http://www.oracle.com"), null));
        assertEquals(new URL("http://www.oracle.com"), urlTypeConverter.fromJson("http://www.oracle.com", null, null));
    }

    @Test
    public void testZonedDateTimeConvertor() {
        ZonedDateTimeTypeConverter zonedDateTimeTypeConverter = new ZonedDateTimeTypeConverter();

        PropertyCustomization customization = buildDefaultDateFormatterCustomization();
        assertEquals("2015-02-16T13:21:00+01:00[Europe/Prague]",
                zonedDateTimeTypeConverter.toJson(ZonedDateTime.of(2015, 2, 16, 13, 21, 0, 0, ZoneId.of("Europe/Prague")), customization));
        assertEquals(ZonedDateTime.of(2015, 2, 16, 13, 21, 0, 0, ZoneId.of("Europe/Prague")),
                zonedDateTimeTypeConverter.fromJson("2015-02-16T13:21:00+01:00[Europe/Prague]", null, customization));
    }

    @Test
    public void testEnum() {
        EnumTypeConverter enumTypeConverter = new EnumTypeConverter();

        assertEquals("HIGH", enumTypeConverter.toJson(Level.HIGH, null));
        assertEquals(Level.HIGH, enumTypeConverter.fromJson("HIGH", Level.class, null));
    }

    public enum Level {
        HIGH,
        MEDIUM,
        LOW
    }

    @Test
    public void testBigDecimal() {
        final Jsonb jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withStrictIJSON(true))).build();

        BigDecimalWrapper bigDecimalWrapper = new BigDecimalWrapper();
        bigDecimalWrapper.bigDecimal = new BigDecimal("-9007199254740991.0123");

        assertEquals("{\"bigDecimal\":\"-9007199254740991.0123\"}", jsonb.toJson(bigDecimalWrapper));
        assertEquals(bigDecimalWrapper.bigDecimal, jsonb.fromJson("{\"bigDecimal\":\"-9007199254740991.0123\"}", BigDecimalWrapper.class).bigDecimal);

        bigDecimalWrapper.bigDecimal = new BigDecimal("3.141592653589793238462643383279");
        assertEquals("{\"bigDecimal\":\"3.141592653589793238462643383279\"}", jsonb.toJson(bigDecimalWrapper));
        assertEquals(bigDecimalWrapper.bigDecimal, jsonb.fromJson("{\"bigDecimal\":\"3.141592653589793238462643383279\"}", BigDecimalWrapper.class).bigDecimal);

        bigDecimalWrapper.bigDecimal = new BigDecimal(new BigInteger("1"), -400);
        assertEquals("{\"bigDecimal\":\"1E+400\"}", jsonb.toJson(bigDecimalWrapper));
        assertEquals(bigDecimalWrapper.bigDecimal, jsonb.fromJson("{\"bigDecimal\":\"1E+400\"}", BigDecimalWrapper.class).bigDecimal);

        bigDecimalWrapper.bigDecimal = new BigDecimal("9007199254740991");
        assertEquals("{\"bigDecimal\":9007199254740991}", jsonb.toJson(bigDecimalWrapper));
        assertEquals(bigDecimalWrapper.bigDecimal, jsonb.fromJson("{\"bigDecimal\":9007199254740991}", BigDecimalWrapper.class).bigDecimal);
    }

    @Test
    public void testBigInteger() {
        final Jsonb jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withStrictIJSON(true))).build();

        BigIntegerWrapper bigIntegerWrapper = new BigIntegerWrapper();

        bigIntegerWrapper.bigInteger = new BigInteger("9007199254740991");
        assertEquals("{\"bigInteger\":9007199254740991}", jsonb.toJson(bigIntegerWrapper));
        assertEquals(bigIntegerWrapper.bigInteger, jsonb.fromJson("{\"bigInteger\":9007199254740991}", BigIntegerWrapper.class).bigInteger);

        bigIntegerWrapper.bigInteger = new BigInteger("9007199254740992");
        assertEquals("{\"bigInteger\":\"9007199254740992\"}", jsonb.toJson(bigIntegerWrapper));
        assertEquals(bigIntegerWrapper.bigInteger, jsonb.fromJson("{\"bigInteger\":\"9007199254740992\"}", BigIntegerWrapper.class).bigInteger);

        bigIntegerWrapper.bigInteger = new BigInteger("-9007199254740992");
        assertEquals("{\"bigInteger\":\"-9007199254740992\"}", jsonb.toJson(bigIntegerWrapper));
        assertEquals(bigIntegerWrapper.bigInteger, jsonb.fromJson("{\"bigInteger\":\"-9007199254740992\"}", BigIntegerWrapper.class).bigInteger);
    }

    @Test
    public void testByteArray() {
        byte[] array = {1, 2, 3};
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        assertEquals("[1,2,3]", jsonb.toJson(array));
    }

    @Test
    public void testByteArrayWithBinaryStrategy() {
        byte[] array = {127, -128, 127};
        Jsonb jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withBinaryDataStrategy(BinaryDataStrategy.BYTE))).build();

        assertEquals("[127,-128,127]", jsonb.toJson(array));
        assertArrayEquals(array, jsonb.fromJson("[127,-128,127]", byte[].class));
    }

    @Test
    public void testByteArrayWithStrictJson() {
        byte[] array = {1, 2, 3};
        ByteArrayWrapper byteArrayWrapper = new ByteArrayWrapper();
        byteArrayWrapper.array = array;
        Jsonb jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withStrictIJSON(true))).build();

        assertEquals("{\"array\":\"" + Base64.getUrlEncoder().encodeToString(array) + "\"}", jsonb.toJson(byteArrayWrapper));

        jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withStrictIJSON(false))).build();

        assertEquals("{\"array\":[1,2,3]}", jsonb.toJson(byteArrayWrapper));
    }

    @Test
    public void testByteArrayWithStrictJsonAndBinaryStrategy() {
        byte[] array = {1, 2, 3};
        ByteArrayWrapper byteArrayWrapper = new ByteArrayWrapper();
        byteArrayWrapper.array = array;
        Jsonb jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withStrictIJSON(true).withBinaryDataStrategy(BinaryDataStrategy.BYTE))).build();
        assertEquals("{\"array\":\"" + Base64.getUrlEncoder().encodeToString(array) + "\"}", jsonb.toJson(byteArrayWrapper));

        jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withStrictIJSON(true).withBinaryDataStrategy(BinaryDataStrategy.BASE_64))).build();
        assertEquals("{\"array\":\"" + Base64.getUrlEncoder().encodeToString(array) + "\"}", jsonb.toJson(byteArrayWrapper));

        jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withStrictIJSON(true).withBinaryDataStrategy(BinaryDataStrategy.BASE_64_URL))).build();
        assertEquals("{\"array\":\"" + Base64.getUrlEncoder().encodeToString(array) + "\"}", jsonb.toJson(byteArrayWrapper));

        jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withBinaryDataStrategy(BinaryDataStrategy.BYTE))).build();
        assertEquals("[1,2,3]", jsonb.toJson(array));

        jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withBinaryDataStrategy(BinaryDataStrategy.BASE_64))).build();
        assertEquals("{\"array\":\"" + Base64.getEncoder().encodeToString(array) + "\"}", jsonb.toJson(byteArrayWrapper));

        jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withBinaryDataStrategy(BinaryDataStrategy.BASE_64_URL))).build();
        assertEquals("{\"array\":\"" + Base64.getUrlEncoder().encodeToString(array) + "\"}", jsonb.toJson(byteArrayWrapper));
    }

}
