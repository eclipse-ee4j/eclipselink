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

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.TimeZone;

/**
 * @author Roman Grigoriadi
 */
public class DefaultSerializers {

    private static final DefaultSerializers instance = new DefaultSerializers();

    private final Map<Class<?>, SerializerProvider> serializers;

    private final SerializerProvider enumProvider;

    private DefaultSerializers() {
        this.serializers = initSerializers();
        enumProvider = new SerializerProvider(EnumTypeSerializer.class, EnumTypeDeserializer.class);
    }

    private Map<Class<?>, SerializerProvider> initSerializers() {
        final Map<Class<?>, SerializerProvider> serializers = new HashMap<>();
        serializers.put(Boolean.class, new SerializerProvider(BooleanTypeSerializer.class, BooleanTypeDeserializer.class));
        serializers.put(Boolean.TYPE, new SerializerProvider(BooleanTypeSerializer.class, BooleanTypeDeserializer.class));
        serializers.put(Byte.class, new SerializerProvider(ByteTypeSerializer.class, ByteTypeDeserializer.class));
        serializers.put(Byte.TYPE, new SerializerProvider(ByteTypeSerializer.class, ByteTypeDeserializer.class));
        serializers.put(Calendar.class, new SerializerProvider(CalendarTypeSerializer.class, CalendarTypeDeserializer.class));
        serializers.put(GregorianCalendar.class, new SerializerProvider(CalendarTypeSerializer.class, CalendarTypeDeserializer.class));
        serializers.put(Character.class, new SerializerProvider(CharacterTypeSerializer.class, CharacterTypeDeserializer.class));
        serializers.put(Character.TYPE, new SerializerProvider(CharacterTypeSerializer.class, CharacterTypeDeserializer.class));
        serializers.put(Date.class, new SerializerProvider(DateTypeSerializer.class, DateTypeDeserializer.class));
        serializers.put(Double.class, new SerializerProvider(DoubleTypeSerializer.class, DoubleTypeDeserializer.class));
        serializers.put(Double.TYPE, new SerializerProvider(DoubleTypeSerializer.class, DoubleTypeDeserializer.class));
        serializers.put(Float.class, new SerializerProvider(FloatTypeSerializer.class, FloatTypeDeserializer.class));
        serializers.put(Float.TYPE, new SerializerProvider(FloatTypeSerializer.class, FloatTypeDeserializer.class));
        serializers.put(Instant.class, new SerializerProvider(InstantTypeSerializer.class, InstantTypeDeserializer.class));
        serializers.put(Integer.class, new SerializerProvider(IntegerTypeSerializer.class, IntegerTypeDeserializer.class));
        serializers.put(Integer.TYPE, new SerializerProvider(IntegerTypeSerializer.class, IntegerTypeDeserializer.class));
        serializers.put(JsonNumber.class, new SerializerProvider(JsonValueSerializer.class, JsonNumberTypeDeserializer.class));
        serializers.put(JsonString.class, new SerializerProvider(JsonValueSerializer.class, JsonStringTypeDeserializer.class));
        serializers.put(JsonValue.class, new SerializerProvider(JsonValueSerializer.class, JsonValueDeserializer.class));
        serializers.put(LocalDateTime.class, new SerializerProvider(LocalDateTimeTypeSerializer.class, LocalDateTimeTypeDeserializer.class));
        serializers.put(LocalDate.class, new SerializerProvider(LocalDateTypeSerializer.class, LocalDateTypeDeserializer.class));
        serializers.put(LocalTime.class, new SerializerProvider(LocalTimeTypeSerializer.class, LocalTimeTypeDeserializer.class));
        serializers.put(Long.class, new SerializerProvider(LongTypeSerializer.class, LongTypeDeserializer.class));
        serializers.put(Long.TYPE, new SerializerProvider(LongTypeSerializer.class, LongTypeDeserializer.class));
        serializers.put(Number.class, new SerializerProvider(NumberTypeSerializer.class, NumberTypeDeserializer.class));
        serializers.put(OffsetDateTime.class, new SerializerProvider(OffsetDateTimeTypeSerializer.class, OffsetDateTimeTypeDeserializer.class));
        serializers.put(OffsetTime.class, new SerializerProvider(OffsetTimeTypeSerializer.class, OffsetTimeTypeDeserializer.class));
        serializers.put(OptionalDouble.class, new SerializerProvider(OptionalDoubleTypeSerializer.class, OptionalDoubleTypeDeserializer.class));
        serializers.put(OptionalInt.class, new SerializerProvider(OptionalIntTypeSerializer.class, OptionalIntTypeDeserializer.class));
        serializers.put(OptionalLong.class, new SerializerProvider(OptionalLongTypeSerializer.class, OptionalLongTypeDeserializer.class));
        serializers.put(Short.class, new SerializerProvider(ShortTypeSerializer.class, ShortTypeDeserializer.class));
        serializers.put(Short.TYPE, new SerializerProvider(ShortTypeSerializer.class, ShortTypeDeserializer.class));
        serializers.put(String.class, new SerializerProvider(StringTypeSerializer.class, StringTypeDeserializer.class));
        serializers.put(TimeZone.class, new SerializerProvider(TimeZoneTypeSerializer.class, TimeZoneTypeDeserializer.class));
        serializers.put(URI.class, new SerializerProvider(URITypeSerializer.class, URITypeDeserializer.class));
        serializers.put(URL.class, new SerializerProvider(URLTypeSerializer.class, URLTypeDeserializer.class));
        serializers.put(ZonedDateTime.class, new SerializerProvider(ZonedDateTimeTypeSerializer.class, ZonedDateTimeTypeDeserializer.class));
        serializers.put(Duration.class, new SerializerProvider(DurationTypeSerializer.class, DurationTypeDeserializer.class));
        serializers.put(Period.class, new SerializerProvider(PeriodTypeSerializer.class, PeriodTypeDeserializer.class));
        serializers.put(ZoneId.class, new SerializerProvider(ZoneIdTypeSerializer.class, ZoneIdTypeDeserializer.class));
        serializers.put(BigInteger.class, new SerializerProvider(BigIntegerTypeSerializer.class, BigIntegerTypeDeserializer.class));
        serializers.put(BigDecimal.class, new SerializerProvider(BigDecimalTypeSerializer.class, BigDecimalTypeDeserializer.class));
        serializers.put(ZoneOffset.class, new SerializerProvider(ZoneOffsetTypeSerializer.class, ZoneOffsetTypeDeserializer.class));

        return Collections.unmodifiableMap(serializers);
    }

    /**
     * Look for a provider for a supported value type. These serializers are basically singleton stateless shared instances.
     *
     * @param clazz supported type class
     * @param <T> Type of serializer
     * @return serializer if found
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<SerializerProvider> findValueSerializerProvider(Class<T> clazz) {
        Class<?> candidate = clazz;
        do {
            final SerializerProvider provider = serializers.get(candidate);
            if (provider != null) {
                return Optional.of(provider);
            }
            candidate = candidate.getSuperclass();
        } while (candidate != null);

        return findByCondition(clazz);
    }

    private <T> Optional<SerializerProvider> findByCondition(Class<T> clazz) {
        if (clazz.isEnum()) {
            return Optional.of(enumProvider);
        } else if (JsonString.class.isAssignableFrom(clazz)) {
            return Optional.of(serializers.get(JsonString.class));
        } else if (JsonNumber.class.isAssignableFrom(clazz)) {
            return Optional.of(serializers.get(JsonNumber.class));
        } else if (JsonValue.class.isAssignableFrom(clazz) && !(JsonObject.class.isAssignableFrom(clazz) || JsonArray.class.isAssignableFrom(clazz))) {
            return Optional.of(serializers.get(JsonValue.class));
        }
        return Optional.empty();
    }


    /**
     * Singleton instance.
     * @return instance
     */
    public static DefaultSerializers getInstance() {
        return instance;
    }
}
