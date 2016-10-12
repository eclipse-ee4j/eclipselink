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
import org.eclipse.persistence.json.bind.internal.unmarshaller.AbstractItem;
import org.eclipse.persistence.json.bind.internal.unmarshaller.CurrentItem;
import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;
import org.eclipse.persistence.json.bind.model.JsonContext;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.lang.reflect.Type;

/**
 * @author Roman Grigoriadi
 */
public abstract class AbstractContainerSerializer<T> extends AbstractItem<T> implements JsonbSerializer<T> {
    /**
     * Create instance of current item with its builder.
     *
     * @param builder
     */
    protected AbstractContainerSerializer(SerializerBuilder builder) {
        super(builder);
    }

    public AbstractContainerSerializer(CurrentItem<?> wrapper, Type runtimeType, ClassModel classModel, JsonBindingModel wrapperModel) {
        super(wrapper, runtimeType, classModel, wrapperModel);
    }

    @Override
    public final void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
        if (getWrapperModel().getContext() == JsonContext.JSON_OBJECT) {
            writeStart(getWrapperModel().getWriteName(), generator);
        } else {
            writeStart(generator);
        }
        serializeInternal(obj, generator, ctx);
        writeEnd(generator);
    }

    protected abstract void serializeInternal(T obj, JsonGenerator generator, SerializationContext ctx);

    /**
     * Write start object or start array without a key.
     */
    protected abstract void writeStart(JsonGenerator generator);

    /**
     * Writes end for object or array.
     */
    protected void writeEnd(JsonGenerator generator) {
        generator.writeEnd();
    }

    /**
     * Write start object or start array with key.
     * @param key json key name
     */
    protected abstract void writeStart(String key, JsonGenerator generator);

    protected <X> void serializerCaptor(JsonbSerializer<?> serializer, X object, JsonGenerator generator, SerializationContext ctx) {
        ((JsonbSerializer<X>) serializer).serialize(object, generator, ctx);
    }
}
