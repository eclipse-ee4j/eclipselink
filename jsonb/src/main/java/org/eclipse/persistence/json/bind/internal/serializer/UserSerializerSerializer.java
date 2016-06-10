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

import org.eclipse.persistence.json.bind.internal.AbstractContainerSerializer;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

/**
 * Serializes an object with user defined serializer.
 *
 * @author Roman Grigoriadi
 */
public class UserSerializerSerializer<T> extends AbstractContainerSerializer<T> {

    private final JsonbSerializer<T> userSerializer;
    /**
     * Create instance of current item with its builder.
     *
     * @param builder
     */
    protected UserSerializerSerializer(SerializerBuilder builder, JsonbSerializer<T> userSerializer) {
        super(builder);
        this.userSerializer = userSerializer;
    }

    @Override
    protected void serializeInternal(T obj, JsonGenerator generator, SerializationContext ctx) {
        userSerializer.serialize(obj, generator, ctx);
    }

    @Override
    protected void writeStart(JsonGenerator generator) {
        //TODO this must be handled  by user serializer
        generator.writeStartObject();
    }

    @Override
    protected void writeStart(String key, JsonGenerator generator) {
        //TODO this must be handled  by user serializer
        generator.writeStartObject(key);
    }
}
