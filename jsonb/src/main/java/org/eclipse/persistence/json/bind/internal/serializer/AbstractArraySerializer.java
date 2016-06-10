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
import org.eclipse.persistence.json.bind.internal.ReflectionUtils;
import org.eclipse.persistence.json.bind.model.SerializerBindingModel;

import javax.json.stream.JsonGenerator;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Common serializer for arrays.
 *
 * @author Roman Grigoriadi
 */
public abstract class AbstractArraySerializer<T> extends AbstractContainerSerializer<T> {

    protected final SerializerBindingModel containerModel;

    protected final Type arrayValType;

    protected AbstractArraySerializer(SerializerBuilder builder) {
        super(builder);
        arrayValType = getRuntimeType() instanceof ParameterizedType ?
                ReflectionUtils.resolveType(this, ((ParameterizedType) getRuntimeType()).getActualTypeArguments()[0])
                : Object.class;
        containerModel = new SerializerContainerModel(arrayValType, resolveContainerModelCustomization(arrayValType),
                SerializerBindingModel.Context.JSON_ARRAY, null);
    }


    @Override
    protected void writeStart(JsonGenerator generator) {
        generator.writeStartArray();
    }

    @Override
    protected void writeStart(String key, JsonGenerator generator) {
        generator.writeStartArray(key);
    }
}
