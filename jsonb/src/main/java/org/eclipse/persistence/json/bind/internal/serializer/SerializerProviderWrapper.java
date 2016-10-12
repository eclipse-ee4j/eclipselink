package org.eclipse.persistence.json.bind.internal.serializer;

/**
 * Wraps serializer and deserializer providers.
 *
 * @author Roman Grigoriadi
 */
public class SerializerProviderWrapper {

    private ISerializerProvider serializerProvider;
    private IDeserializerProvider deserializerProvider;

    public SerializerProviderWrapper(ISerializerProvider serializerProvider, IDeserializerProvider deserializerProvider) {
        this.serializerProvider = serializerProvider;
        this.deserializerProvider = deserializerProvider;
    }

    public ISerializerProvider getSerializerProvider() {
        return serializerProvider;
    }

    public IDeserializerProvider getDeserializerProvider() {
        return deserializerProvider;
    }
}
