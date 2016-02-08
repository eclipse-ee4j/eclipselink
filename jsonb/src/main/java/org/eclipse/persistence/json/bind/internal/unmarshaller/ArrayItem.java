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

import org.eclipse.persistence.json.bind.internal.ReflectionUtils;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Item for handling arrays.
 *
 * @author Roman Grigoriadi
 */
public class ArrayItem extends AbstractItem<Object[]> implements EmbeddedItem {

    /**
     * Runtime type class of an array.
     */
    private final Class<?> componentClass;

    private final List<?> items = new ArrayList<>();

    private Object[] arrayInstance;

    protected ArrayItem(CurrentItemBuilder builder) {
        super(builder);
        if (getRuntimeType() instanceof GenericArrayType) {
            componentClass = ReflectionUtils.resolveRawType(this, ((GenericArrayType) getRuntimeType()).getGenericComponentType());
        } else {
            componentClass = ReflectionUtils.getRawType(getRuntimeType()).getComponentType();
        }
    }

    @Override
    public void appendItem(CurrentItem<?> valueItem) {
        appendCaptor(valueItem.getInstance());
    }

    @Override
    public void appendValue(String key, String value, JsonValueType jsonValueType) {
        if (jsonValueType == JsonValueType.NULL) {
            appendCaptor(null);
            return;
        }
        Object converted = getTypeConverter().fromJson(value, ReflectionUtils.getRawType(resolveValueType(componentClass, jsonValueType)));
        appendCaptor(converted);
    }

    @SuppressWarnings("unchecked")
    private <T> void appendCaptor(T value) {
        ((List<T>) items).add(value);
    }

    @Override
    public CurrentItem<?> newItem(String fieldName, JsonValueType jsonValueType) {
        Type actualValueType = componentClass;
        return new CurrentItemBuilder().withWrapper(this).withType(actualValueType).withJsonValueType(jsonValueType).build();
    }

    @Override
    public Object[] getInstance() {
        if (arrayInstance == null || arrayInstance.length != items.size()) {
            arrayInstance = (Object[]) Array.newInstance(componentClass, items.size());
        }
        return items.toArray(arrayInstance);
    }
}
