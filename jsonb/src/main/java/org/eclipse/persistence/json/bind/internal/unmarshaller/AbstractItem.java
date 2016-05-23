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

import org.eclipse.persistence.json.bind.internal.ReflectionUtils;
import org.eclipse.persistence.json.bind.internal.conversion.ConvertersMapTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.TypeConverter;
import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.PropertyModel;

import java.lang.reflect.Type;

/**
 * Metadata wrapper for currently processed object.
 * References mapping models of an unmarshalled item,
 * creates instances of it, sets finished unmarshalled objects into object tree.
 *
 * @param <T> Instantiated object type
 * @author Roman Grigoriadi
 */
public abstract class AbstractItem<T> implements CurrentItem<T> {

    /**
     * Item containing instance of wrapping object and its metadata.
     * Null in case of a root object.
     */
    private final CurrentItem<?> wrapper;

    private final Type runtimeType;

    /**
     * Cached reference to mapping model of an item.
     */
    private final ClassModel classModel;

    /**
     * Cached reference of a field model of this item in wrapper class (if any).
     */
    private final PropertyModel wrapperPropertyModel;

    private final TypeConverter typeConverter;

    /**
     * Key name in JSON document prepending processed object parentheses.
     */
    private final String jsonKeyName;

    /**
     * Create instance of current item with its builder.
     */
    @SuppressWarnings("unchecked")
    protected AbstractItem(UnmarshallerItemBuilder builder) {
        this.wrapper = builder.getWrapper();
        this.wrapperPropertyModel = builder.getPropertyModel();
        this.classModel = builder.getClassModel();
        this.runtimeType = builder.getRuntimeType();
        this.jsonKeyName = builder.getJsonKeyName();
        this.typeConverter = ConvertersMapTypeConverter.getInstance();
    }


    @Override
    public ClassModel getClassModel() {
        return classModel;
    }

    @Override
    public PropertyModel getWrapperPropertyModel() {
        return wrapperPropertyModel;
    }

    protected TypeConverter getTypeConverter() {
        return typeConverter;
    }

    @Override
    public String getJsonKeyName() {
        return jsonKeyName;
    }

    @Override
    public CurrentItem<?> getWrapper() {
        return wrapper;
    }

    @Override
    public Type getRuntimeType() {
        return runtimeType;
    }

    protected Type resolveValueType(Type runtimeType, JsonValueType jsonValueType) {
        final Type actualType = ReflectionUtils.resolveType(this, runtimeType);
        return actualType != Object.class ? actualType : jsonValueType.getConversionType();
    }


}
