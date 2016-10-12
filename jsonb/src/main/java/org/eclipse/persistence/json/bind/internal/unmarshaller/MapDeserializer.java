/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.json.bind.internal.unmarshaller;

import org.eclipse.persistence.json.bind.internal.JsonbParser;
import org.eclipse.persistence.json.bind.internal.JsonbRiParser;
import org.eclipse.persistence.json.bind.internal.ReflectionUtils;
import org.eclipse.persistence.json.bind.internal.Unmarshaller;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;

import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Item implementation for {@link java.util.Map} fields.
 * According to JSON specification object can have only string keys, given that maps could only be parsed
 * from JSON objects, implementation is bound to String type.
 *
 * @author Roman Grigoriadi
 */
public class MapDeserializer<T extends Map<?,?>> extends AbstractContainerDeserializer<T> implements EmbeddedItem {

    /**
     * Type of value in the map.
     * (Keys must always be Strings, because of JSON spec)
     */
    private final Type mapValueRuntimeType;

    private final T instance;

    private final JsonBindingModel model;

    /**
     * @param builder
     */
    protected MapDeserializer(DeserializerBuilder builder) {
        super(builder);
        mapValueRuntimeType = getRuntimeType() instanceof ParameterizedType ?
                ReflectionUtils.resolveType(this, ((ParameterizedType) getRuntimeType()).getActualTypeArguments()[1])
                : Object.class;

        this.instance = createInstance();
        this.model = new ContainerModel(mapValueRuntimeType, resolveContainerModelCustomization(mapValueRuntimeType, builder.getJsonbContext()));
    }

    @SuppressWarnings("unchecked")
    private T createInstance() {
        Class<T> rawType = (Class<T>) ReflectionUtils.getRawType(getRuntimeType());
        return rawType.isInterface() ? (T) new HashMap<>() : ReflectionUtils.createNoArgConstructorInstance(rawType);
    }

    @Override
    protected JsonBindingModel getModel() {
        return model;
    }

    @Override
    public T getInstance(Unmarshaller unmarshaller) {
        return instance;
    }

    @Override
    public void appendResult(Object result) {
        appendCaptor(parserContext.getLastKeyName(), result);
    }

    @SuppressWarnings("unchecked")
    private <V> void appendCaptor(String key, V value) {
        ((Map<String, V>) getInstance(null)).put(key, value);
    }

    @Override
    protected void deserializeNext(JsonParser parser, Unmarshaller context) {
        final JsonbDeserializer<?> deserializer = newCollectionOrMapItem(mapValueRuntimeType, context.getJsonbContext());
        appendResult(deserializer.deserialize(parser, context, mapValueRuntimeType));
    }

    @Override
    protected JsonbRiParser.LevelContext moveToFirst(JsonbParser parser) {
        parser.moveTo(JsonParser.Event.START_OBJECT);
        return parser.getCurrentLevel();
    }
}
