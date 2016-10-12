package org.eclipse.persistence.json.bind.internal.serializer;

import org.eclipse.persistence.json.bind.model.JsonBindingModel;

/**
 * Create instance of a serializer.
 *
 * @author Roman Grigoriadi
 */
public interface ISerializerProvider {

    /**
     * Provides new instance of serializer.
     * @param model model to use
     * @return deserializer
     */
    AbstractValueTypeSerializer<?> provideSerializer(JsonBindingModel model);
}
