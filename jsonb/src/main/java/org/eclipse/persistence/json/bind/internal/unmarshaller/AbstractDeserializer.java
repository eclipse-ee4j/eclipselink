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

import org.eclipse.persistence.json.bind.internal.AbstractSerializerBuilder;
import org.eclipse.persistence.json.bind.internal.ProcessingContext;
import org.eclipse.persistence.json.bind.internal.ReflectionUtils;
import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.Customization;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;

import java.lang.reflect.Type;

/**
 * Metadata wrapper for currently processed object.
 * References mapping models of an unmarshalled item,
 * creates instances of it, sets finished unmarshalled objects into object tree.
 *
 * @param <T> Instantiated object type
 * @author Roman Grigoriadi
 */
public abstract class AbstractDeserializer<T> implements CurrentItem<T> {

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
     * Cached reference of a model of this item in wrapper class (if any).
     */
    private final JsonBindingModel wrapperModel;

    /**
     * Create instance of current item with its builder.
     */
    protected AbstractDeserializer(AbstractSerializerBuilder builder) {
        this.wrapper = builder.getWrapper();
        this.wrapperModel = builder.getModel();
        this.classModel = builder.getClassModel();
        this.runtimeType = builder.getRuntimeType();
    }


    @Override
    public ClassModel getClassModel() {
        return classModel;
    }

    @Override
    public CurrentItem<?> getWrapper() {
        return wrapper;
    }

    /**
     * A wrapper model for this item. May represent a JavaBean property or a container like collection.
     *
     * @return wrapper model.
     */
    @Override
    public JsonBindingModel getWrapperModel() {
        return wrapperModel;
    }

    @Override
    public Type getRuntimeType() {
        return runtimeType;
    }

    protected Customization resolveContainerModelCustomization(Type componentType) {
        Class<?> valueRawType = ReflectionUtils.resolveRawType(this, componentType);
        ClassModel classModel = ProcessingContext.getMappingContext().getClassModel(valueRawType);
        if (classModel != null) {
            return classModel.getCustomization();
        }
        return new DefaultCustomization();
    }
}
