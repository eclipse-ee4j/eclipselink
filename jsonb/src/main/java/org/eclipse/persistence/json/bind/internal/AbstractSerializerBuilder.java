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

package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.internal.unmarshaller.CurrentItem;
import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;

import java.lang.reflect.Type;

/**
 * @author Roman Grigoriadi
 */
public class AbstractSerializerBuilder<T extends AbstractSerializerBuilder, B extends JsonBindingModel> {
    /**
     * Not null with an exception of a root item.
     */
    protected CurrentItem<?> wrapper;
    /**
     * Model of jsonb binding.
     */
    protected B model;
    /**
     * In case of unknown object genericType.
     * Null for embedded objects such as collections, or known conversion types.
     */
    protected ClassModel classModel;
    /**
     * Runtime type resolved after expanding type variables and wildcards.
     */
    protected Type runtimeType;
    /**
     * Type is used when field model is not present.
     * In case of root, or embedded objects such as collections.
     */
    protected Type genericType;

    /**
     * Wrapper item for this item.
     * @param wrapper not null
     * @return builder instance for call chaining
     */
    public T withWrapper(CurrentItem<?> wrapper) {
        this.wrapper = wrapper;
        return (T) this;
    }

    /**
     * Model of a field for underlying instance. In case model is present, instance type is inferred from it.
     * @param model model of a field, not null
     * @return builder instance for call chaining
     */
    public T withModel(B model) {
        this.model = model;
        return (T) this;
    }

    /***
     * Gets or load class model for a class an its superclasses.
     *
     * @param rawType Class to get model for
     * @return Class model
     */
    protected ClassModel getClassModel(Class<?> rawType) {
        ClassModel classModel = ProcessingContext.getMappingContext().getClassModel(rawType);
        if (classModel == null) {
            classModel = ProcessingContext.getMappingContext().getOrCreateClassModel(rawType);
        }
        return classModel;
    }

    /**
     * Wrapper item for this item.
     * @return wrapper item
     */
    public CurrentItem<?> getWrapper() {
        return wrapper;
    }

    /**
     * Model of a for underlying instance. In case model is present, instance type is inferred from it.
     * @return model of a field
     */
    public B getModel() {
        return model;
    }

    /**
     * Model of a class representing current item and instance (if any).
     * Known collection classes doesn't need such a model.
     * @return model of a class
     */
    public ClassModel getClassModel() {
        return classModel;
    }

    /**
     * Resolved runtime type for instance in case of {@link java.lang.reflect.TypeVariable} or {@link java.lang.reflect.WildcardType}
     * Otherwise provided type in type field, or type of field model.
     * @return runtime type
     */
    public Type getRuntimeType() {
        return runtimeType;
    }

    /**
     * Type for underlying instance to be created from.
     * In case of type variable or wildcard, will be resolved recursively from parent items.
     * @param type type of instance not null
     * @return builder instance for call chaining
     */
    public T withType(Type type) {
        this.genericType = unwrapAnonymous(type);
        return (T) this;
    }

    private Type unwrapAnonymous(Type type) {
        if (type instanceof Class<?> && ((Class)type).isAnonymousClass()) {
            return ((Class) type).getGenericSuperclass();
        }
        return type;
    }
}
