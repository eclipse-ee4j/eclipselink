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

import org.eclipse.persistence.json.bind.internal.adapter.JsonbAdapterInfo;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.PropertyModel;

import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
import java.lang.reflect.Type;

/**
 * Decorator for an item which builds adapted type instance by a {@link JsonbAdapter}.
 * After adapted item is finished building its instance is converted to field type object by calling adapter.
 *
 * @param <A> adapted type, type to unmarshall JSOn into
 * @param <T> required type, typically type of the field, which is adapted to another type
 */
public class AdaptedObjectItemDecorator<A, T> implements UnmarshallerItem<T>, DecoratorItem<A> {

    private UnmarshallerItem<A> adaptedItem;

    private final JsonbAdapterInfo adapterInfo;

    private final UnmarshallerItem<?> wrapperItem;

    /**
     * Creates decoration instance wrapping real adapted object item.
     * @param adapterInfo adapter type info
     * @param wrapperItem wrapper item to get instance from
     */
    public AdaptedObjectItemDecorator(JsonbAdapterInfo adapterInfo, UnmarshallerItem<?> wrapperItem) {
        this.adapterInfo = adapterInfo;
        this.wrapperItem = wrapperItem;
    }

    @Override
    public void appendItem(UnmarshallerItem<?> valueItem) {
        wrapperItem.appendItem(this);
    }

    @Override
    public void appendValue(String key, String value, JsonValueType jsonValueType) {
        adaptedItem.appendValue(key, value, jsonValueType);
    }

    @Override
    public UnmarshallerItem<?> newItem(String fieldName, JsonValueType jsonValueType) {
        return adaptedItem.newItem(fieldName, jsonValueType);
    }

    @Override
    public ClassModel getClassModel() {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getInstance() {
        A a = adaptedItem.getInstance();
        try {
            return ((JsonbAdapter<T, A>) adapterInfo.getAdapter()).adaptFromJson(a);
        } catch (Exception e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.ADAPTER_EXCEPTION, adapterInfo.getFromType(), adapterInfo.getToType(), adapterInfo.getAdapter().getClass()), e);
        }
    }

    @Override
    public PropertyModel getWrapperPropertyModel() {
        return adaptedItem.getWrapperPropertyModel();
    }

    @Override
    public String getJsonKeyName() {
        return adaptedItem.getJsonKeyName();
    }

    @Override
    public CurrentItem<?> getWrapper() {
        return wrapperItem;
    }

    @Override
    public Type getRuntimeType() {
        return adaptedItem.getRuntimeType();
    }

    public void setAdaptedItem(UnmarshallerItem<A> adaptedItem) {
        this.adaptedItem = adaptedItem;
    }

    @Override
    public UnmarshallerItem<A> getDecoratedItem() {
        return adaptedItem;
    }

    @Override
    public UnmarshallerItem<?> getWrapperItem() {
        return wrapperItem;
    }
}
