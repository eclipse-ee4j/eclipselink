package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.model.Customization;

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
     * @param customization
     * @return Unmarshalled object
     */
    T fromJson(String jsonValue, Type type, Customization customization);

    /**
     * This method marshalls object into the String
     * @param object Object to marshall
     * @param customization
     * @return Marshalled object to String
     */
    String toJson(T object, Customization customization);

    boolean supportsToJson(Class<?> type);

    boolean supportsFromJson(Class<?> type);
}
