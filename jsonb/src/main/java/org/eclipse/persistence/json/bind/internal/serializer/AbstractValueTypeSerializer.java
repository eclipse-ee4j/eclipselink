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

import org.eclipse.persistence.json.bind.model.SerializerBindingModel;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

/**
 * Common type for all supported type serializers.
 * @author Roman Grigoriadi
 */
public abstract class AbstractValueTypeSerializer<T> implements JsonbSerializer<T> {

    private final Class<T> clazz;

    protected final SerializerBindingModel model;

    /**
     * New instance.
     * @param clazz clazz to work with
     * @param model
     */
    public AbstractValueTypeSerializer(Class<T> clazz, SerializerBindingModel model) {
        this.clazz = clazz;
        this.model = model;
    }

    /**
     * Serializes an object to JSON.
     *
     * @param obj       object to serialize
     * @param generator JSON generator to use
     * @param ctx       JSONB mapper context
     */
    @Override
    public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
        if (model.getContext() == SerializerBindingModel.Context.JSON_OBJECT) {
            serialize(obj, generator, model.getJsonWriteName());
        } else {
            serialize(obj, generator);
        }
    }

    protected abstract void serialize(T obj, JsonGenerator generator, String key);

    protected abstract void serialize(T obj, JsonGenerator generator);
}
