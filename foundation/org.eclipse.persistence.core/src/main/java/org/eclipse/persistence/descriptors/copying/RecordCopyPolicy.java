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

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.sessions.Session;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;

/**
 * <p><b>Purpose</b>: This is the copy policy for {@code java.lang.Record}.
 * <p>
 * It creates a copy by creating a new instance. As {@code java.lang.Record} is immutable
 * all cloned attributes must be collected and passed to the constructor.
 */
public class RecordCopyPolicy extends AbstractCopyPolicy {
    public RecordCopyPolicy() {
        super();
    }

    @Override
    public Object buildClone(Object domainObject, Session session) throws DescriptorException {
        return cloneRecord((Record) domainObject);
    }

    @Override
    public boolean buildsNewInstance() {
        return true;
    }

    //There is limited functionality some complex nested objects shouldn't be cloned.
    private <R extends Record> R cloneRecord(R template) {
        try {
            ArrayList<Class<?>> types = new ArrayList<>();
            ArrayList values = new ArrayList<>();
            for (RecordComponent component : template.getClass().getRecordComponents()) {
                types.add(component.getType());
                Object value = component.getAccessor().invoke(template);
                if (value instanceof Record) {
                    value = cloneRecord((Record) value);
                }
                values.add(value);
            }
            Constructor<? extends Record> canonical = template.getClass().getDeclaredConstructor(types.toArray(Class[]::new));
            var result = (R) canonical.newInstance(values.toArray(Object[]::new));
            return result;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Record clone failed: " + e, e);
        }
    }
}
