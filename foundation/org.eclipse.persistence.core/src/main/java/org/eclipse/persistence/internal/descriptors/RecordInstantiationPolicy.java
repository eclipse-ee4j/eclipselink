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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.queries.ReadObjectQuery;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <b>Purpose</b>: Allows customization of how an {@code java.lang.Record} is created/instantiated.<p>
 */
public class RecordInstantiationPolicy extends InstantiationPolicy {


    /**
     * Default constructor
     */
    public RecordInstantiationPolicy() {
        super();
    }

    /**
     * Build and return a new instance, using the appropriate mechanism.
     */
    @Override
    public Object buildNewInstance() throws DescriptorException {
        return null;
    }

    public Object buildNewRecordInstance(Class<Record> clazz, List<DatabaseMapping> mappings, AbstractRecord databaseRow, AbstractSession session) {
        class TypeValue {
            public TypeValue(Class<?> type) {
                this.type = type;
            }
            Class<?> type;
            Object value;
        }
        Map<String, TypeValue> typeValueMap = new LinkedHashMap<>();
        ReadObjectQuery query = new ReadObjectQuery();
        query.setSession(session);
        for (RecordComponent component : clazz.getRecordComponents()) {
            typeValueMap.put(component.getName(), new TypeValue(component.getType()));
        }
        for (DatabaseMapping mapping: mappings) {
            Object value = null;
            if (mapping instanceof DirectToFieldMapping) {
                value = mapping.valueFromRow(databaseRow, null, query, true);
            } if (mapping instanceof AggregateObjectMapping aggregateObjectMapping) {
                ClassDescriptor descriptor = session.getClassDescriptor(aggregateObjectMapping.getReferenceClass());
                value = buildNewRecordInstance((Class<Record>) aggregateObjectMapping.getReferenceClass(), descriptor.getMappings(), databaseRow, session);
            }
            TypeValue typeValue = typeValueMap.get(mapping.getAttributeName());
            typeValue.value = value;
        }
        List values = new ArrayList<>();
        for (TypeValue typeValue: typeValueMap.values()) {
            values.add(typeValue.value);
        }
        return newRecord(clazz, values);
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

    public <R extends Record> R newRecord(Class<R> clazz, List values) {
        return newRecordInternal(clazz, new ArrayList<>(values));
    }

    private <R extends Record> R newRecordInternal(Class<R> clazz, List values) {
        List<Class<?>> types = new ArrayList<>();
        RecordComponent[] recordComponents = clazz.getRecordComponents();
        for (int i = 0; i < recordComponents.length; i++) {
            RecordComponent component = recordComponents[i];
            if (component.getType().isRecord() && !(values.get(i) instanceof Record)) {
                List nestedValues = new ArrayList<>(values.subList(i, i + component.getType().getRecordComponents().length));
                R value = newRecordInternal((Class<? extends R>) component.getType(), nestedValues);
                values.add(i, value);
                values.removeAll(nestedValues);
            }
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
}
