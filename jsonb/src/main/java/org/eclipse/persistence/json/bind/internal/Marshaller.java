/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * <p>
 * Contributors:
 *     Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.internal.serializer.ContainerSerializerProvider;
import org.eclipse.persistence.json.bind.internal.serializer.SerializerBuilder;
import org.eclipse.persistence.json.bind.internal.unmarshaller.ContainerModel;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;
import org.eclipse.persistence.json.bind.model.JsonContext;

import javax.json.bind.JsonbException;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerationException;
import javax.json.stream.JsonGenerator;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * JSONB marshaller. Created each time marshalling operation called.
 *
 * @author Dmitry Kornilov
 * @author Roman Grigoriadi
 */
public class Marshaller extends ProcessingContext implements SerializationContext {

    private static final Logger logger = Logger.getLogger(Marshaller.class.getName());

    private Type runtimeType;

    /**
     * Creates Marshaller for generation to String.
     *
     * @param jsonbContext
     * @param rootRuntimeType type of root object
     */
    public Marshaller(JsonbContext jsonbContext, Type rootRuntimeType) {
        super(jsonbContext);
        this.runtimeType = rootRuntimeType;
    }

    /**
     * Creates Marshaller for generation to String.
     *
     * @param jsonbContext
     */
    public Marshaller(JsonbContext jsonbContext) {
        super(jsonbContext);
        this.runtimeType = null;
    }

    /**
     * Marshals given object to provided Writer or OutputStream.
     *
     * @param object object to marshall
     * @param jsonGenerator generator to use
     */
    public void marshall(Object object, JsonGenerator jsonGenerator) {
        try {
            //TODO remove default customization
            final ContainerModel model = new ContainerModel(runtimeType != null ? runtimeType : object.getClass(), null, JsonContext.ROOT, null);
            serializeRoot(object, jsonGenerator, model);
        } catch (JsonbException e) {
            logger.severe(e.getMessage());
            throw e;
        } finally {
            try {
                jsonGenerator.close();
            } catch (JsonGenerationException jge) {
                logger.severe(jge.getMessage());
            }
        }
    }

            @Override
    public <T> void serialize(String key, T object, JsonGenerator generator) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(object);
        final ContainerModel model = new ContainerModel(object.getClass(), null, JsonContext.JSON_OBJECT, key);
        serializeRoot(object, generator, model);
    }

    @Override
    public <T> void serialize(T object, JsonGenerator generator) {
        Objects.requireNonNull(object);
        final ContainerModel model = new ContainerModel(object.getClass(), null, JsonContext.JSON_ARRAY, null);
        serializeRoot(object, generator, model);
    }

    @SuppressWarnings("unchecked")
    public <T> void serializeRoot(T root, JsonGenerator generator, JsonBindingModel model) {
        final JsonbSerializer<T> rootSerializer = (JsonbSerializer<T>) getRootSerializer(root.getClass(), model);
        rootSerializer.serialize(root, generator, this);
    }

    private JsonbSerializer<?> getRootSerializer(Class<?> rootClazz, JsonBindingModel model) {
        final ContainerSerializerProvider serializerProvider = getMappingContext().getSerializerProvider(rootClazz);
        if (serializerProvider != null) {
            return serializerProvider.provideSerializer(null, runtimeType, getMappingContext().getClassModel(rootClazz), model);
        }
        return new SerializerBuilder(jsonbContext).withObjectClass(rootClazz)
                .withType(model.getType()).withModel(model).build();
    }

}
