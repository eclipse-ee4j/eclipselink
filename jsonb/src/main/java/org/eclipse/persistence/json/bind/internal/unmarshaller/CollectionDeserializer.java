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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Item implementation for {@link java.util.List} fields
 *
 * @author Roman Grigoriadi
 */
class CollectionDeserializer<T extends Collection<?>> extends AbstractContainerDeserializer<T> implements EmbeddedItem {


    private final ContainerModel containerModel;

    /**
     * Generic bound parameter of List.
     */
    private final Type collectionValueType;

    private T instance;

    /**
     * @param builder
     */
    protected CollectionDeserializer(DeserializerBuilder builder) {
        super(builder);
        collectionValueType = getRuntimeType() instanceof ParameterizedType ?
                ReflectionUtils.resolveType(this, ((ParameterizedType) getRuntimeType()).getActualTypeArguments()[0])
                : Object.class;

        instance = createInstance();
        this.containerModel = new ContainerModel(collectionValueType, resolveContainerModelCustomization(collectionValueType, builder.getJsonbContext()));
    }

    @Override
    protected JsonBindingModel getModel() {
        return containerModel;
    }

    @SuppressWarnings("unchecked")
    private T createInstance() {
        Class<T> rawType = (Class<T>) ReflectionUtils.getRawType(getRuntimeType());
        assert Collection.class.isAssignableFrom(rawType);

        if (rawType.isInterface()) {
            if (List.class.isAssignableFrom(rawType)) {
                return (T) new ArrayList<>();
            }
            if (Set.class.isAssignableFrom(rawType)) {
                return (T) new HashSet<>();
            }
            if (Queue.class.isAssignableFrom(rawType)) {
                return (T) new ArrayDeque<>();
            }
        }
        return ReflectionUtils.createNoArgConstructorInstance(rawType);
    }

    /**
     * Instance of an item. Unmarshalling sets values to such instance.
     *
     * @return instance
     * @param unmarshaller
     */
    @Override
    public T getInstance(Unmarshaller unmarshaller) {
        return instance;
    }

    @Override
    public void appendResult(Object result) {
        appendCaptor(result);
    }

    @SuppressWarnings("unchecked")
    private <T> void appendCaptor(T object) {
        ((Collection<T>) instance).add(object);
    }


    /**
     * Determine class mappings and create an instance of a new deserializer.
     * Currently processed deserializer is pushed to stack, for waiting till new object is finished.
     *
     * @param parser
     * @param context
     */
    @Override
    protected void deserializeNext(JsonParser parser, Unmarshaller context) {
        final JsonbDeserializer<?> deserializer = newCollectionOrMapItem(collectionValueType, context.getJsonbContext());
        appendResult(deserializer.deserialize(parser, context, collectionValueType));
    }

    @Override
    protected JsonbRiParser.LevelContext moveToFirst(JsonbParser parser) {
        parser.moveTo(JsonParser.Event.START_ARRAY);
        return parser.getCurrentLevel();
    }
}
