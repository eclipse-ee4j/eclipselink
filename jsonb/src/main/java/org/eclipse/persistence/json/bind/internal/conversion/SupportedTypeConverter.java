package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;

/**
 * Interface for conversion of all known supported types defined by json specification.
 *
 * @author Roman Grigoriadi
 */
public interface SupportedTypeConverter<T> {

    T fromJson(String jsonValue, Type type);

    String toJson(T object);

    boolean supportsToJson(Class<?> type);

    boolean supportsFromJson(Class<?> type);
}
