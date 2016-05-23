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
import java.util.Collection;

/**
 * Item implementation for {@link java.util.List} fields
 *
 * @author Roman Grigoriadi
 */
class CollectionItem<T extends Collection<?>> extends AbstractUnmarshallerItem<T> implements UnmarshallerItem<T>, EmbeddedItem {

    /**
     * Generic bound parameter of List.
     */
    private final Type collectionValueType;

    /**
     * @param builder
     */
    protected CollectionItem(UnmarshallerItemBuilder builder) {
        super(builder);
        collectionValueType = getRuntimeType() instanceof ParameterizedType ?
                ReflectionUtils.resolveType(this, ((ParameterizedType) getRuntimeType()).getActualTypeArguments()[0])
                : Object.class;
    }

    @Override
    public void appendItem(UnmarshallerItem<?> abstractItem) {
        appendCaptor(abstractItem.getInstance());
    }

    @Override
    public void appendValue(String key, String value, JsonValueType jsonValueType) {
        if (jsonValueType == JsonValueType.NULL) {
            appendCaptor(null);
            return;
        }
        Object converted = getTypeConverter().fromJson(value, ReflectionUtils.getRawType(resolveValueType(collectionValueType, jsonValueType)), getCustomization());
        appendCaptor(converted);
    }

    @SuppressWarnings("unchecked")
    private <T> void appendCaptor(T object) {
        ((Collection<T>) getInstance()).add(object);
    }

    @Override
    public UnmarshallerItem<?> newItem(String fieldName, JsonValueType jsonValueType) {
        return newCollectionOrMapItem(fieldName, collectionValueType, jsonValueType);
    }

    private Customization getCustomization() {
        /* TODO (marshaller refactoring) consider honoring JsonbAnnotation on list fields after MR.
        if (getWrapper() != null) {
            return getWrapperPropertyModel().getCustomization();
        }*/
        ClassModel componentClassModel = ProcessingContext.getMappingContext()
                .getClassModel(ReflectionUtils.getRawType(collectionValueType));
        return componentClassModel != null ? componentClassModel.getClassCustomization() : null;
    }

    @Override
    protected String getLastPropertyName() {
        return null; //no json keys for collections
    }
}
