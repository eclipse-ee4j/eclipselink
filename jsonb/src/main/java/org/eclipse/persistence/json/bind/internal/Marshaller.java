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

import org.eclipse.persistence.json.bind.internal.serializer.SerializerBuilder;
import org.eclipse.persistence.json.bind.internal.serializer.SerializerContainerModel;
import org.eclipse.persistence.json.bind.internal.unmarshaller.CurrentItem;
import org.eclipse.persistence.json.bind.internal.unmarshaller.DefaultCustomization;
import org.eclipse.persistence.json.bind.model.SerializerBindingModel;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

/**
 * JSONB marshaller. Created each time marshalling operation called.
 *
 * @author Dmitry Kornilov
 * @author Roman Grigoriadi
 */
public class Marshaller extends ProcessingContext implements SerializationContext {

    private Optional<Type> runtimeType;

    private CurrentItem<?> current;

    /**
     * Creates Marshaller for generation to String.
     *
     * @param jsonbContext
     * @param rootRuntimeType type of root object
     */
    public Marshaller(JsonbContext jsonbContext, Type rootRuntimeType) {
        super(jsonbContext);
        this.runtimeType = Optional.of(rootRuntimeType);
    }

    /**
     * Creates Marshaller for generation to String.
     *
     * @param jsonbContext
     */
    public Marshaller(JsonbContext jsonbContext) {
        super(jsonbContext);
        this.runtimeType = Optional.empty();
    }

    /**
     * Marshals given object to provided Writer or OutputStream.
     *
     * @param object object to marshall
     * @param jsonGenerator generator to use
     */
    public void marshall(Object object, JsonGenerator jsonGenerator) {
        new JsonbContextCommand<Void>() {
            @Override
            protected Void doInProcessingContext() {
                final SerializerContainerModel model = new SerializerContainerModel(runtimeType.orElseGet(()->object.getClass()), new DefaultCustomization(), SerializerBindingModel.Context.ROOT, null);
                serializeRoot(object, jsonGenerator, model);
                jsonGenerator.close();
                return null;
    }
        }.execute(this);
    }

            @Override
    public <T> void serialize(String key, T object, JsonGenerator generator) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(object);
        final SerializerContainerModel model = new SerializerContainerModel(object.getClass(), new DefaultCustomization(), ((SerializerBindingModel) current.getWrapperModel()).getContext(), key);
        serializeRoot(object, generator, model);
    }

    @Override
    public <T> void serialize(T object, JsonGenerator generator) {
        Objects.requireNonNull(object);
        final SerializerContainerModel model = new SerializerContainerModel(object.getClass(), new DefaultCustomization(), ((SerializerBindingModel) current.getWrapperModel()).getContext(), null);
        serializeRoot(object, generator, model);
    }

    @SuppressWarnings("unchecked")
    public <T> void serializeRoot(T root, JsonGenerator generator, SerializerBindingModel model) {
        final JsonbSerializer<T> rootSerializer = (JsonbSerializer<T>) new SerializerBuilder().withObjectClass(root.getClass())
                .withType(runtimeType.orElseGet(()->root.getClass())).withModel(model).withWrapper(current).build();
        rootSerializer.serialize(root, generator, this);
    }

    public void setCurrent(CurrentItem<?> serializer) {
        this.current = serializer;
    }
}
