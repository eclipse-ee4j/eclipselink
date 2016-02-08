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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Item implementation for {@link java.util.Map} fields.
 * According to JSON specification object can have only string keys, given that maps could only be parsed
 * from JSON objects, implementation is bound to String type.
 *
 * @author Roman Grigoriadi
 */
public class MapItem extends AbstractItem<Map<?, ?>> implements EmbeddedItem {

    /**
     * Type of value in the map.
     * (Keys must always be Strings, because of JSON spec)
     */
    private final Type mapValueRuntimeType;

    /**
     * @param builder
     */
    protected MapItem(CurrentItemBuilder builder) {
        super(builder);
        mapValueRuntimeType = getRuntimeType() instanceof ParameterizedType ?
                ReflectionUtils.resolveType(this, ((ParameterizedType) getRuntimeType()).getActualTypeArguments()[1])
                : Object.class;
    }


    @Override
    public void appendItem(CurrentItem<?> valueItem) {
        appendCaptor(valueItem.getJsonKeyName(), valueItem.getInstance());
    }

    @Override
    public void appendValue(String key, String value, JsonValueType jsonValueType) {
        if (jsonValueType == JsonValueType.NULL) {
            appendCaptor(key, null);
            return;
        }
        Object convertedValue = getTypeConverter().fromJson(value, ReflectionUtils.getRawType(resolveValueType(mapValueRuntimeType, jsonValueType)));
        appendCaptor(key, convertedValue);
    }

    @SuppressWarnings("unchecked")
    private <V> void appendCaptor(String key, V value) {
        ((Map<String, V>) getInstance()).put(key, value);
    }

    @Override
    public CurrentItem<?> newItem(String fieldName, JsonValueType jsonValueType) {
        return newCollectionOrMapItem(fieldName, mapValueRuntimeType, jsonValueType);
    }

}
