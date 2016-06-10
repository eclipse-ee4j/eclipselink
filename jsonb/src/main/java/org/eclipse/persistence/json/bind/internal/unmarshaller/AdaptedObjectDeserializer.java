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

package org.eclipse.persistence.json.bind.internal.unmarshaller;

import org.eclipse.persistence.json.bind.internal.Unmarshaller;
import org.eclipse.persistence.json.bind.internal.adapter.AdapterBinding;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;

import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;

/**
 * Decorator for an item which builds adapted type instance by a {@link JsonbAdapter}.
 * After adapted item is finished building its instance is converted to field type object by calling adapter.
 *
 * @param <A> adapted type, type to unmarshall JSOn into
 * @param <T> required type, typically type of the field, which is adapted to another type
 */
public class AdaptedObjectDeserializer<A, T> implements CurrentItem<T>, JsonbDeserializer<T> {

    private JsonbDeserializer<A> adaptedItem;

    private final AdapterBinding adapterInfo;

    private final AbstractContainerDeserializer<?> wrapperItem;

    /**
     * Creates decoration instance wrapping real adapted object item.
     * @param adapterInfo adapter type info
     * @param wrapperItem wrapper item to get instance from
     */
    public AdaptedObjectDeserializer(AdapterBinding adapterInfo, AbstractContainerDeserializer<?> wrapperItem) {
        this.adapterInfo = adapterInfo;
        this.wrapperItem = wrapperItem;
    }

    @Override
    public ClassModel getClassModel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CurrentItem<?> getWrapper() {
        return wrapperItem;
    }

    @Override
    public JsonBindingModel getWrapperModel() {
        return ((AbstractContainerDeserializer) adaptedItem).getWrapperModel();
    }

    @Override
    public Type getRuntimeType() {
        if (adaptedItem instanceof AbstractContainerDeserializer) {
            return ((AbstractContainerDeserializer) adaptedItem).getRuntimeType();
        }
        throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Deserialization propagation is not allowed for:" + adaptedItem));
    }

    public void setAdaptedItem(JsonbDeserializer<A> adaptedItem) {
        this.adaptedItem = adaptedItem;
    }

    @Override
    public T deserialize(JsonParser parser, DeserializationContext context, Type rtType) {
        Unmarshaller unmarshaller = (Unmarshaller) context;
        unmarshaller.setCurrent(this);
        try {
            final A result =  adaptedItem.deserialize(parser, context, rtType);
            final T adapted = ((JsonbAdapter<T, A>) adapterInfo.getAdapter()).adaptFromJson(result);
            unmarshaller.setCurrent(wrapperItem);
            return adapted;
        } catch (Exception e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.ADAPTER_EXCEPTION, adapterInfo.getBindingType(), adapterInfo.getToType(), adapterInfo.getAdapter().getClass()), e);
        }
    }

}
