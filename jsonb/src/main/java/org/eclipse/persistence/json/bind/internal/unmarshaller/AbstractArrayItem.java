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

import org.eclipse.persistence.json.bind.internal.ProcessingContext;
import org.eclipse.persistence.json.bind.internal.ReflectionUtils;
import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.Customization;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Common array unmarshalling item implementation.
 *
 * @author Roman Grigoriadi
 */
public abstract class AbstractArrayItem<T> extends AbstractUnmarshallerItem<T> implements UnmarshallerItem<T>, EmbeddedItem {

    /**
     * Runtime type class of an array.
     */
    protected final Class<?> componentClass;

    protected AbstractArrayItem(UnmarshallerItemBuilder builder) {
        super(builder);
        if (getRuntimeType() instanceof GenericArrayType) {
            componentClass = ReflectionUtils.resolveRawType(this, ((GenericArrayType) getRuntimeType()).getGenericComponentType());
        } else {
            componentClass = ReflectionUtils.getRawType(getRuntimeType()).getComponentType();
        }
    }

    @Override
    public void appendItem(UnmarshallerItem<?> valueItem) {
        appendCaptor(valueItem.getInstance());
    }

    @Override
    public void appendValue(String key, String value, JsonValueType jsonValueType) {
        if (jsonValueType == JsonValueType.NULL) {
            appendCaptor(null);
            return;
        }

        Object converted = getTypeConverter().fromJson(value,
                ReflectionUtils.getRawType(resolveValueType(componentClass, jsonValueType)), getCustomization());
        appendCaptor(converted);
    }

    @SuppressWarnings("unchecked")
    private <X> void appendCaptor(X value) {
        ((List<X>) getItems()).add(value);
    }

    @Override
    public UnmarshallerItem<?> newItem(String fieldName, JsonValueType jsonValueType) {
        Type actualValueType = componentClass;
        return newUnmarshallerItemBuilder().withType(actualValueType).withJsonValueType(jsonValueType).build();
    }

    protected abstract List<?> getItems();

    private Customization getCustomization() {
        /* TODO (marshaller refactoring) consider honoring JsonbAnnotation on array after MR.
        if (getWrapper() != null) {
            return getWrapperPropertyModel().getCustomization();
        }*/
        ClassModel componentClassModel = ProcessingContext.getMappingContext().getClassModel(componentClass);
        return componentClassModel != null ? componentClassModel.getClassCustomization() : null;
    }
}