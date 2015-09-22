package org.eclipse.persistence.json.bind.internal.conversion;

/**
 * Interface for known type conversion strategy.
 *
 * @author Roman Grigoriadi
 */
public interface TypeConverter {

    <T> T fromJson(String value, Class<T> clazz);

    <T> String toJson(T object);

    boolean supportsToJson(Class<?> clazz);

    boolean supportsFromJson(Class<?> clazz);
}
