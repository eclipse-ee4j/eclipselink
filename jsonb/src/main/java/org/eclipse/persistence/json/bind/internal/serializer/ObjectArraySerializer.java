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

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

/**
 * Serializer for arrays of arbitrary objects.
 * @author Roman Grigoriadi
 */
public class ObjectArraySerializer<T> extends AbstractArraySerializer<T[]> {

    protected ObjectArraySerializer(SerializerBuilder builder) {
        super(builder);
    }

    @Override
    protected void serializeInternal(T[] arr, JsonGenerator generator, SerializationContext ctx) {
        for (T obj : arr) {
            if (obj == null || isEmptyOptional(obj)) {
                generator.writeNull();
                continue;
            }
            final JsonbSerializer<?> serializer = new SerializerBuilder().withObjectClass(obj.getClass()).withWrapper(this).withModel(containerModel).build();
            serializerCaptor(serializer, obj, generator, ctx);
        }
    }
}
