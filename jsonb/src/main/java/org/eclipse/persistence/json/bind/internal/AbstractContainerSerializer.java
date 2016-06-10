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

package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.internal.serializer.SerializerBuilder;
import org.eclipse.persistence.json.bind.internal.unmarshaller.AbstractDeserializer;
import org.eclipse.persistence.json.bind.model.SerializerBindingModel;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * @author Roman Grigoriadi
 */
public abstract class AbstractContainerSerializer<T> extends AbstractDeserializer<T> implements JsonbSerializer<T> {
    /**
     * Create instance of current item with its builder.
     *
     * @param builder
     */
    protected AbstractContainerSerializer(SerializerBuilder builder) {
        super(builder);
    }

    @Override
    public final void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
        Marshaller marshaller = (Marshaller) ctx;
        marshaller.setCurrent(this);
        if (((SerializerBindingModel) getWrapperModel()).getContext() == SerializerBindingModel.Context.JSON_OBJECT) {
            writeStart(((SerializerBindingModel) getWrapperModel()).getJsonWriteName(), generator);
        } else {
            writeStart(generator);
        }
        serializeInternal(obj, generator, ctx);
        generator.writeEnd();
        marshaller.setCurrent(getWrapper());
    }

    protected abstract void serializeInternal(T obj, JsonGenerator generator, SerializationContext ctx);

    /**
     * Write start object or start array without a key.
     */
    protected abstract void writeStart(JsonGenerator generator);

    /**
     * Write start object or start array with key.
     * @param key json key name
     */
    protected abstract void writeStart(String key, JsonGenerator generator);

    /**
     * True if object is instance of Optional and is empty.
     * @param value value to check
     * @return true if optional and empty
     */
    protected boolean isEmptyOptional(Object value) {
        return value instanceof Optional<?> && !((Optional<?>) value).isPresent()
                || value instanceof OptionalInt && !((OptionalInt) value).isPresent()
                || value instanceof OptionalLong && !((OptionalLong) value).isPresent()
                || value instanceof OptionalDouble && !((OptionalDouble) value).isPresent();
    }

    protected <X> void serializerCaptor(JsonbSerializer<?> serializer, X object, JsonGenerator generator, SerializationContext ctx) {
        ((JsonbSerializer<X>) serializer).serialize(object, generator, ctx);
    }
}
