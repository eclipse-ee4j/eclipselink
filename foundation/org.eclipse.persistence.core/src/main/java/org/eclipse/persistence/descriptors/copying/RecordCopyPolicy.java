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
package org.eclipse.persistence.descriptors.copying;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.sessions.Session;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;

/**
 * <p><b>Purpose</b>: This is the copy policy for {@code java.lang.Record}.
 * <p>
 * It creates a copy by creating a new instance. As {@code java.lang.Record} is immutable
 * all cloned attributes must be collected and passed to the constructor.
 * <p>
 * Relationship attributes ({@code @ManyToOne}/{@code @OneToOne}/{@code @OneToMany}/{@code @ManyToMany})
 * are cloned through their mapping so that the referenced entities are registered as UnitOfWork
 * working clones; otherwise the clone would keep the shared-cache originals and a later
 * {@code persist()} on such an entity would re-INSERT it (EclipseLink issue #2929).
 */
public class RecordCopyPolicy extends AbstractCopyPolicy {
    public RecordCopyPolicy() {
        super();
    }

    @Override
    public Object buildClone(Object domainObject, Session session) throws DescriptorException {
        return cloneRecord((Record) domainObject, session);
    }

    @Override
    public boolean buildsNewInstance() {
        return true;
    }

    //There is limited functionality some complex nested objects shouldn't be cloned.
    private <R extends Record> R cloneRecord(R template, Session session) {
        try {
            ArrayList<Class<?>> types = new ArrayList<>();
            ArrayList<Object> values = new ArrayList<>();
            ClassDescriptor descriptor = (session == null) ? null : session.getDescriptor(template.getClass());
            for (RecordComponent component : template.getClass().getRecordComponents()) {
                types.add(component.getType());
                Object value = component.getAccessor().invoke(template);
                if (value instanceof Record) {
                    value = cloneRecord((Record) value, session);
                } else if (session instanceof UnitOfWorkImpl && descriptor != null) {
                    DatabaseMapping mapping = descriptor.getMappingForAttributeName(component.getName());
                    if (mapping instanceof ObjectReferenceMapping
                            || mapping instanceof OneToManyMapping
                            || mapping instanceof ManyToManyMapping) {
                        // Clone the relationship so that its entities are registered as working
                        // clones in the UnitOfWork, not left as shared-cache originals.
                        value = ((ForeignReferenceMapping) mapping).buildCloneForPartObject(
                                value, template, null, null, (UnitOfWorkImpl) session, null, false, false);
                    }
                }
                values.add(value);
            }
            Constructor<? extends Record> canonical = template.getClass().getDeclaredConstructor(types.toArray(Class[]::new));
            @SuppressWarnings("unchecked")
            var result = (R) canonical.newInstance(values.toArray(Object[]::new));
            return result;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Record clone failed: " + e, e);
        }
    }
}
