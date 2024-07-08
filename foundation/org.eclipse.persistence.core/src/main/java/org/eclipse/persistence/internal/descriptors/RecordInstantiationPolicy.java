/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.descriptors;

import org.eclipse.persistence.exceptions.DescriptorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>Purpose</b>: Allows customization of how an {@code java.lang.Record} is created/instantiated.<p>
 */
public class RecordInstantiationPolicy<T extends Record> extends InstantiationPolicy {

    /**
     * Record class
     */
    private Class<T> clazz;

    /**
     * Values passed to the record constructor
     */
    private List<?> values;

    /**
     * Default constructor
     */
    public RecordInstantiationPolicy() {
        super();
    }

    /**
     * Constructor
     *
     * @param clazz Record class
     */
    public RecordInstantiationPolicy(Class<T> clazz) {
        this();
        this.clazz = clazz;
    }

    /**
     * Build and return a new instance, using the appropriate mechanism.
     */
    @Override
    public Object buildNewInstance() throws DescriptorException {
        try {
            //Handle ClassDecriptor validation/postInitialization
            if (values == null) {
                return null;
            } else {
                return newRecord(clazz, values);
            }
        } finally {
            values = null;
        }
    }

    @Override
    public void useFactoryInstantiationPolicy(String factoryClassName, String methodName) {
        throw new UnsupportedOperationException();
    }

    private T newRecord(Class<T> clazz, List<?> values) {
        List<Class<?>> types = new ArrayList<>();
        RecordComponent[] recordComponents = clazz.getRecordComponents();
        for (RecordComponent recordComponent: recordComponents) {
            types.add(recordComponent.getType());
        }
        try {
            Constructor<? extends Record> canonical = clazz.getDeclaredConstructor(types.toArray(Class[]::new));
            @SuppressWarnings("unchecked")
            var result = (T) canonical.newInstance(values.toArray(Object[]::new));
            return result;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("New Record creation failed: " + e, e);
        }
    }

    /**
     * Values passed to the record constructor
     *
     * @param values values which are passed to the new record
     */
    public void setValues(List<?> values) {
        this.values = values;
    }

    /**
     * INTERNAL:
     * Clones the InstantiationPolicy
     */
    @Override
    public Object clone() {
        try {
            // clones itself
            return super.clone();
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "()";
    }
}
