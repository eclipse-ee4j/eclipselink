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
public class RecordInstantiationPolicy extends InstantiationPolicy {

    /**
     * Record class
     */
    private Class clazz;

    /**
     * Values passed to the record constructor
     */
    private List values;

    /**
     * Default constructor
     */
    public RecordInstantiationPolicy() {
        super();
    }

    /**
     * Constructor
     * @param clazz Record class
     */
    public RecordInstantiationPolicy(Class clazz) {
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
        } catch (Throwable t) {
            throw t;
        }
        finally {
            values = null;
        }
    }

    private <R extends Record> R newRecord(Class<R> clazz, List values) {
        List<Class<?>> types = new ArrayList<>();
        RecordComponent[] recordComponents = clazz.getRecordComponents();
        for (int i = 0; i < recordComponents.length; i++) {
            RecordComponent component = recordComponents[i];
            types.add(component.getType());
        }
        try {
            Constructor<? extends Record> canonical = clazz.getDeclaredConstructor(types.toArray(Class[]::new));
            var result = (R) canonical.newInstance(values.toArray(Object[]::new));
            return result;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("New Record creation failed: " + e, e);
        }
    }

    /**
     * Values passed to the record constructor
     * @param values
     */
    public void setValues(List values) {
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
