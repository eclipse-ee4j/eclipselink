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

import org.eclipse.persistence.json.bind.internal.ProcessingContext;
import org.eclipse.persistence.json.bind.internal.ReflectionUtils;
import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.Customization;

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
public class MapItem<T extends Map<?,?>> extends AbstractUnmarshallerItem<T> implements UnmarshallerItem<T>, EmbeddedItem {

    /**
     * Type of value in the map.
     * (Keys must always be Strings, because of JSON spec)
     */
    private final Type mapValueRuntimeType;

    private final T instance;

    /**
     * @param builder
     */
    protected MapItem(UnmarshallerItemBuilder builder) {
        super(builder);
        mapValueRuntimeType = getRuntimeType() instanceof ParameterizedType ?
                ReflectionUtils.resolveType(this, ((ParameterizedType) getRuntimeType()).getActualTypeArguments()[1])
                : Object.class;

        this.instance = createInstance();
    }

    @SuppressWarnings("unchecked")
    private T createInstance() {
        Class<T> rawType = (Class<T>) ReflectionUtils.getRawType(getRuntimeType());
        return rawType.isInterface() ? (T) new HashMap<>() : ReflectionUtils.createNoArgConstructorInstance(rawType);
    }

    @Override
    public T getInstance() {
        return instance;
    }

    @Override
    public void appendItem(UnmarshallerItem<?> valueItem) {
        appendCaptor(valueItem.getJsonKeyName(), valueItem.getInstance());
    }

    @Override
    public void appendValue(String key, String value, JsonValueType jsonValueType) {
        if (jsonValueType == JsonValueType.NULL) {
            appendCaptor(key, null);
            return;
        }
        Object convertedValue = getTypeConverter().fromJson(value, ReflectionUtils.getRawType(resolveValueType(mapValueRuntimeType, jsonValueType)), getCustomization());
        appendCaptor(key, convertedValue);
    }

    @SuppressWarnings("unchecked")
    private <V> void appendCaptor(String key, V value) {
        ((Map<String, V>) getInstance()).put(key, value);
    }

    @Override
    public UnmarshallerItem<?> newItem(String fieldName, JsonValueType jsonValueType) {
        return newCollectionOrMapItem(fieldName, mapValueRuntimeType, jsonValueType);
    }

    private Customization getCustomization() {
        /* TODO (marshaller refactoring) consider honoring JsonbAnnotation on Map fields after MR.
        if (getWrapper() != null) {
            return getWrapperPropertyModel().getCustomization();
        }*/
        ClassModel componentClassModel = ProcessingContext.getMappingContext()
                .getClassModel(ReflectionUtils.getRawType(mapValueRuntimeType));
        return componentClassModel != null ? componentClassModel.getClassCustomization() : null;
    }

}
