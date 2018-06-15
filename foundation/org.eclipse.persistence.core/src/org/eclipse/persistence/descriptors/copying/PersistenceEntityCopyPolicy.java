/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.descriptors.copying;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.descriptors.PersistenceObject;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.sessions.*;

/**
 * <p><b>Purpose</b>: This is the default copy policy when weaving is used.
 *
 * It creates a copy by creating a shallow clone of the object using the weaved _persistence_shallow_clone() method.
 */
public class PersistenceEntityCopyPolicy extends AbstractCopyPolicy {
    public PersistenceEntityCopyPolicy() {
        super();
    }

    @Override
    public Object buildWorkingCopyClone(Object object, Session session) throws DescriptorException {
        return ((PersistenceObject)object)._persistence_shallow_clone();
    }

    @Override
    public Object buildClone(Object object, Session session) throws DescriptorException {
        return ((PersistenceObject)object)._persistence_shallow_clone();
    }

    @Override
    public boolean buildsNewInstance() {
        return false;
    }

    @Override
    public String toString() {
        return Helper.getShortClassName(this) + "()";
    }
}
