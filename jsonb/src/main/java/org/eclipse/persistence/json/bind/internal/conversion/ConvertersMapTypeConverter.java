package org.eclipse.persistence.json.bind.internal.conversion;

import javax.json.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.time.*;
import java.util.*;

/**
 * Type converter, with map of known type converters.
 *
 * @author Roman Grigoriadi
 */
public class ConvertersMapTypeConverter implements TypeConverter {

    private static volatile TypeConverter instance;

    private static final Object lock = new Object();

    /**
     * Supported type converters.
     */
    private Map<Class<?>, SupportedTypeConverter<?>> converters = new HashMap<>();

    private ConvertersMapTypeConverter() {
        initialize();
    }

    public static TypeConverter getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ConvertersMapTypeConverter();
                }
            }

        }
        return instance;
    }

    private void initialize() {
        converters.put(Boolean.class, new BooleanTypeConverter());
        converters.put(Byte.class, new ByteTypeConverter());
        converters.put(Calendar.class, new CalendarTypeConverter());
        converters.put(GregorianCalendar.class, new CalendarTypeConverter());
        converters.put(Character.class, new CharacterTypeConverter());
        converters.put(Date.class, new DateTypeConverter());
        converters.put(Double.class, new DoubleTypeConverter());
        converters.put(Float.class, new FloatTypeConverter());
        converters.put(Instant.class, new InstantTypeConverter());
        converters.put(Integer.class, new IntegerTypeConverter());
        converters.put(JsonNumber.class, new JsonNumberTypeConverter());
        converters.put(JsonString.class, new JsonStringTypeConverter());
        converters.put(JsonObject.class, new JsonObjectTypeConverter());
        converters.put(JsonStructure.class, new JsonStructureTypeConverter());
        converters.put(JsonValue.class, new JsonValueTypeConverter());
        converters.put(LocalDateTime.class, new LocalDateTimeTypeConverter());
        converters.put(LocalDate.class, new LocalDateTypeConverter());
        converters.put(LocalTime.class, new LocalTimeTypeConverter());
        converters.put(Long.class, new LongTypeConverter());
        converters.put(Number.class, new NumberTypeConverter());
        converters.put(OffsetDateTime.class, new OffsetDateTimeTypeConverter());
        converters.put(OffsetTime.class, new OffsetTimeTypeConverter());
        converters.put(OptionalDouble.class, new OptionalDoubleTypeConverter());
        converters.put(OptionalInt.class, new OptionalIntTypeConverter());
        converters.put(OptionalLong.class, new OptionalLongTypeConverter());
        converters.put(Short.class, new ShortTypeConverter());
        converters.put(String.class, new StringTypeConverter());
        converters.put(TimeZone.class, new TimeZoneTypeConverter());
        converters.put(URI.class, new URITypeConverter());
        converters.put(URL.class, new URLTypeConverter());
        converters.put(ZonedDateTime.class, new ZonedDateTimeTypeConverter());
        converters.put(Duration.class, new DurationTypeConverter());
        converters.put(Period.class, new PeriodTypeConverter());
        converters.put(ZoneId.class, new ZoneIdTypeConverter());
        converters.put(BigInteger.class, new BigIntegerTypeConverter());
        converters.put(BigDecimal.class, new BigDecimalTypeConverter());
        converters.put(ZoneOffset.class, new ZoneOffsetTypeConverter());
        converters.put(Enum.class, new EnumTypeConverter());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T fromJson(String value, Class<T> clazz) {
        return (T) findConvertorFromJson(clazz).fromJson(value, clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> String toJson(T object) {
        return ((SupportedTypeConverter<T>)findConvertorToJson(object.getClass())).toJson(object);
    }

    private <T> SupportedTypeConverter<?> findConvertorToJson(Class<T> clazz) {
        SupportedTypeConverter<?> convertor = converters.get(clazz);
        if (convertor == null) {
            for (SupportedTypeConverter<?> conv : converters.values()) {
                if (conv.supportsToJson(clazz)){
                    return conv;
                }
            }
        }
        return convertor;
    }

    private <T> SupportedTypeConverter<?> findConvertorFromJson(Class<T> clazz) {
        SupportedTypeConverter<?> convertor = converters.get(clazz);
        if (convertor == null) {
            for (SupportedTypeConverter<?> conv : converters.values()) {
                if (conv.supportsFromJson(clazz)){
                    return conv;
                }
            }
        }
        return convertor;
    }

    @Override
    public boolean supportsFromJson(Class<?> clazz) {
        return findConvertorFromJson(clazz) != null;
    }

    @Override
    public boolean supportsToJson(Class<?> clazz) {
        return findConvertorToJson(clazz) != null;
    }
}
