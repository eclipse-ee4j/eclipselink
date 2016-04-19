package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.model.Customization;

/**
 * Interface for known type conversion strategy.
 *
 * @author Roman Grigoriadi
 */
public interface TypeConverter {

    <T> T fromJson(String value, Class<T> clazz, Customization customization);

    <T> String toJson(T object, Customization customization);

    boolean supportsToJson(Class<?> clazz);

    boolean supportsFromJson(Class<?> clazz);
}
