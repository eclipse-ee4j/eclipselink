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

    private final Map<Class<?>, SerializerProviderWrapper> serializers;

    private final SerializerProviderWrapper enumProvider;

    private DefaultSerializers() {
        this.serializers = initSerializers();
        enumProvider = new SerializerProviderWrapper(EnumTypeSerializer::new, EnumTypeDeserializer::new);
    }

    private Map<Class<?>, SerializerProviderWrapper> initSerializers() {
        final Map<Class<?>, SerializerProviderWrapper> serializers = new HashMap<>();

        serializers.put(Boolean.class, new SerializerProviderWrapper(BooleanTypeSerializer::new, BooleanTypeDeserializer::new));
        serializers.put(Boolean.TYPE, new SerializerProviderWrapper(BooleanTypeSerializer::new, BooleanTypeDeserializer::new));
        serializers.put(Byte.class, new SerializerProviderWrapper(ByteTypeSerializer::new, ByteTypeDeserializer::new));
        serializers.put(Byte.TYPE, new SerializerProviderWrapper(ByteTypeSerializer::new, ByteTypeDeserializer::new));
        serializers.put(Calendar.class, new SerializerProviderWrapper(CalendarTypeSerializer::new, CalendarTypeDeserializer::new));
        serializers.put(GregorianCalendar.class, new SerializerProviderWrapper(CalendarTypeSerializer::new, CalendarTypeDeserializer::new));
        serializers.put(Character.class, new SerializerProviderWrapper(CharacterTypeSerializer::new, CharacterTypeDeserializer::new));
        serializers.put(Character.TYPE, new SerializerProviderWrapper(CharacterTypeSerializer::new, CharacterTypeDeserializer::new));
        serializers.put(Date.class, new SerializerProviderWrapper(DateTypeSerializer::new, DateTypeDeserializer::new));
        serializers.put(Double.class, new SerializerProviderWrapper(DoubleTypeSerializer::new, DoubleTypeDeserializer::new));
        serializers.put(Double.TYPE, new SerializerProviderWrapper(DoubleTypeSerializer::new, DoubleTypeDeserializer::new));
        serializers.put(Float.class, new SerializerProviderWrapper(FloatTypeSerializer::new, FloatTypeDeserializer::new));
        serializers.put(Float.TYPE, new SerializerProviderWrapper(FloatTypeSerializer::new, FloatTypeDeserializer::new));
        serializers.put(Instant.class, new SerializerProviderWrapper(InstantTypeSerializer::new, InstantTypeDeserializer::new));
        serializers.put(Integer.class, new SerializerProviderWrapper(IntegerTypeSerializer::new, IntegerTypeDeserializer::new));
        serializers.put(Integer.TYPE, new SerializerProviderWrapper(IntegerTypeSerializer::new, IntegerTypeDeserializer::new));
        serializers.put(JsonNumber.class, new SerializerProviderWrapper(JsonValueSerializer::new, JsonNumberTypeDeserializer::new));
        serializers.put(JsonString.class, new SerializerProviderWrapper(JsonValueSerializer::new, JsonStringTypeDeserializer::new));
        serializers.put(JsonValue.class, new SerializerProviderWrapper(JsonValueSerializer::new, JsonValueDeserializer::new));
        serializers.put(LocalDateTime.class, new SerializerProviderWrapper(LocalDateTimeTypeSerializer::new, LocalDateTimeTypeDeserializer::new));
        serializers.put(LocalDate.class, new SerializerProviderWrapper(LocalDateTypeSerializer::new, LocalDateTypeDeserializer::new));
        serializers.put(LocalTime.class, new SerializerProviderWrapper(LocalTimeTypeSerializer::new, LocalTimeTypeDeserializer::new));
        serializers.put(Long.class, new SerializerProviderWrapper(LongTypeSerializer::new, LongTypeDeserializer::new));
        serializers.put(Long.TYPE, new SerializerProviderWrapper(LongTypeSerializer::new, LongTypeDeserializer::new));
        serializers.put(Number.class, new SerializerProviderWrapper(NumberTypeSerializer::new, NumberTypeDeserializer::new));
        serializers.put(OffsetDateTime.class, new SerializerProviderWrapper(OffsetDateTimeTypeSerializer::new, OffsetDateTimeTypeDeserializer::new));
        serializers.put(OffsetTime.class, new SerializerProviderWrapper(OffsetTimeTypeSerializer::new, OffsetTimeTypeDeserializer::new));
        serializers.put(OptionalDouble.class, new SerializerProviderWrapper(OptionalDoubleTypeSerializer::new, OptionalDoubleTypeDeserializer::new));
        serializers.put(OptionalInt.class, new SerializerProviderWrapper(OptionalIntTypeSerializer::new, OptionalIntTypeDeserializer::new));
        serializers.put(OptionalLong.class, new SerializerProviderWrapper(OptionalLongTypeSerializer::new, OptionalLongTypeDeserializer::new));
        serializers.put(Short.class, new SerializerProviderWrapper(ShortTypeSerializer::new, ShortTypeDeserializer::new));
        serializers.put(Short.TYPE, new SerializerProviderWrapper(ShortTypeSerializer::new, ShortTypeDeserializer::new));
        serializers.put(String.class, new SerializerProviderWrapper(StringTypeSerializer::new, StringTypeDeserializer::new));
        serializers.put(TimeZone.class, new SerializerProviderWrapper(TimeZoneTypeSerializer::new, TimeZoneTypeDeserializer::new));
        serializers.put(URI.class, new SerializerProviderWrapper(URITypeSerializer::new, URITypeDeserializer::new));
        serializers.put(URL.class, new SerializerProviderWrapper(URLTypeSerializer::new, URLTypeDeserializer::new));
        serializers.put(ZonedDateTime.class, new SerializerProviderWrapper(ZonedDateTimeTypeSerializer::new, ZonedDateTimeTypeDeserializer::new));
        serializers.put(Duration.class, new SerializerProviderWrapper(DurationTypeSerializer::new, DurationTypeDeserializer::new));
        serializers.put(Period.class, new SerializerProviderWrapper(PeriodTypeSerializer::new, PeriodTypeDeserializer::new));
        serializers.put(ZoneId.class, new SerializerProviderWrapper(ZoneIdTypeSerializer::new, ZoneIdTypeDeserializer::new));
        serializers.put(BigInteger.class, new SerializerProviderWrapper(BigIntegerTypeSerializer::new, BigIntegerTypeDeserializer::new));
        serializers.put(BigDecimal.class, new SerializerProviderWrapper(BigDecimalTypeSerializer::new, BigDecimalTypeDeserializer::new));
        serializers.put(ZoneOffset.class, new SerializerProviderWrapper(ZoneOffsetTypeSerializer::new, ZoneOffsetTypeDeserializer::new));

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
    public <T> Optional<SerializerProviderWrapper> findValueSerializerProvider(Class<T> clazz) {
        Class<?> candidate = clazz;
        do {
            final SerializerProviderWrapper provider = serializers.get(candidate);
            if (provider != null) {
                return Optional.of(provider);
            }
            candidate = candidate.getSuperclass();
        } while (candidate != null);

        return findByCondition(clazz);
    }

    private <T> Optional<SerializerProviderWrapper> findByCondition(Class<T> clazz) {
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
