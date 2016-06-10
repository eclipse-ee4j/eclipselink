/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal.serializer;

import org.eclipse.persistence.json.bind.model.JsonBindingModel;
import org.eclipse.persistence.json.bind.model.SerializerBindingModel;

import javax.json.bind.JsonbException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Creates a deserializer instance, injects a JsonBindingModel inside.
 *
 * @author Roman Grigoriadi
 */
public class SerializerProvider {

    private final Class<?> serializerClass;

    private final Class<?> deserializerClass;

    public SerializerProvider(Class<?> serializerClass, Class<?> deserializerClass) {
        this.serializerClass = serializerClass;
        this.deserializerClass = deserializerClass;
    }

    /**
     * Provides new instance of serializer.
     * @param model model to use
     * @return deserializer
     */
    public AbstractValueTypeSerializer<?> provideSerializer(SerializerBindingModel model) {
        try {
            final Constructor<?> constructor = serializerClass.getConstructor(SerializerBindingModel.class);
            return (AbstractValueTypeSerializer<?>) constructor.newInstance(model);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new JsonbException("Problem instantiating serializer:", e);
        }
    }

    /**
     * Provides new instance of serializer.
     * @return deserializer
     */
    public AbstractValueTypeSerializer<?> provideSerializer() {
        return provideSerializer(null);
    }

    /**
     * Provides new instance of deserializer.
     * @param model model to use
     * @return deserializer
     */
    public AbstractValueTypeDeserializer<?> provideDeserializer(JsonBindingModel model) {
        try {
            final Constructor<?> constructor = deserializerClass.getDeclaredConstructor(JsonBindingModel.class);
            return (AbstractValueTypeDeserializer<?>) constructor.newInstance(model);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new JsonbException("Problem instantiating serializer:", e);
        }
    }

    /**
     * Provides new instance of deserializer.
     * @return deserializer
     */
    public AbstractValueTypeDeserializer<?> provideDeserializer() {
        return provideDeserializer(null);
    }
}
