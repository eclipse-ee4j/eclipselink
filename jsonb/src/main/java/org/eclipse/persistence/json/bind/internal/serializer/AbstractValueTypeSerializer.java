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

import org.eclipse.persistence.json.bind.internal.Marshaller;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;
import org.eclipse.persistence.json.bind.model.JsonContext;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

/**
 * Common type for all supported type serializers.
 * @author Roman Grigoriadi
 */
public abstract class AbstractValueTypeSerializer<T> implements JsonbSerializer<T> {

    protected final JsonBindingModel model;

    /**
     * New instance.
     * @param model
     */
    public AbstractValueTypeSerializer(JsonBindingModel model) {
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
        Marshaller marshaller = (Marshaller) ctx;
        if (model.getContext() == JsonContext.JSON_OBJECT) {
            serialize(obj, generator, model.getWriteName(), marshaller);
        } else {
            serialize(obj, generator, marshaller);
        }
    }

    protected abstract void serialize(T obj, JsonGenerator generator, String key, Marshaller marshaller);

    protected abstract void serialize(T obj, JsonGenerator generator, Marshaller marshaller);
}
