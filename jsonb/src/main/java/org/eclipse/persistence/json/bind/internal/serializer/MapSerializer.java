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
import org.eclipse.persistence.json.bind.internal.Marshaller;
import org.eclipse.persistence.json.bind.internal.ReflectionUtils;
import org.eclipse.persistence.json.bind.internal.unmarshaller.ContainerModel;
import org.eclipse.persistence.json.bind.internal.unmarshaller.EmbeddedItem;
import org.eclipse.persistence.json.bind.model.JsonContext;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Serializer for maps.
 * @author Roman Grigoriadi
 */
public class MapSerializer<T extends Map> extends AbstractContainerSerializer<T> implements EmbeddedItem {

    private Type mapValueRuntimeType;

    protected MapSerializer(SerializerBuilder builder) {
        super(builder);
        mapValueRuntimeType = getRuntimeType() instanceof ParameterizedType ?
                ReflectionUtils.resolveType(this, ((ParameterizedType) getRuntimeType()).getActualTypeArguments()[1])
                : Object.class;
    }

    @Override
    protected void serializeInternal(T obj, JsonGenerator generator, SerializationContext ctx) {
        final Marshaller marshaller = (Marshaller) ctx;
        obj.keySet().stream().forEach((key) -> {
            final String keysString = String.valueOf(key);
            final Object value = obj.get(key);
            if (value == null) {
                generator.writeNull(keysString);
                return;
            }
            final JsonbSerializer<?> serializer = new SerializerBuilder(marshaller.getJsonbContext()).withObjectClass(value.getClass())
                    .withModel(new ContainerModel(mapValueRuntimeType,
                            resolveContainerModelCustomization(mapValueRuntimeType, marshaller.getJsonbContext()), JsonContext.JSON_OBJECT, keysString)).build();
            serializerCaptor(serializer, value, generator, ctx);
        });
    }

    @Override
    protected void writeStart(JsonGenerator generator) {
        generator.writeStartObject();
    }

    @Override
    protected void writeStart(String key, JsonGenerator generator) {
        generator.writeStartObject(key);
    }
}
