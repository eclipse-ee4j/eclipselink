package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;

/**
 * Interface for conversion of all known supported types defined by json specification.
 *
 * @author Roman Grigoriadi
 */
public interface SupportedTypeConverter<T> {

    /**
     * This method unmarshalls String value to corresponding Java object
     * @param jsonValue Value to unmarshall
     * @param type Type of the class
     * @return Unmarshalled object
     */
    T fromJson(String jsonValue, Type type);

    /**
     * This method marshalls object into the String
     * @param object Object to marshall
     * @return Marshalled object to String
     */
    String toJson(T object);

    boolean supportsToJson(Class<?> type);

    boolean supportsFromJson(Class<?> type);
}
