package org.eclipse.persistence.json.bind.internal.serializer;

import org.eclipse.persistence.json.bind.model.JsonBindingModel;

/**
 * Creates instance of deserializer.
 *
 * @author Roman Grigoriadi
 */
public interface IDeserializerProvider {

    /**
     * Provides new instance of deserializer.
     * @param model model to use
     * @return deserializer
     */
    AbstractValueTypeDeserializer<?> provideDeserializer(JsonBindingModel model);
}
