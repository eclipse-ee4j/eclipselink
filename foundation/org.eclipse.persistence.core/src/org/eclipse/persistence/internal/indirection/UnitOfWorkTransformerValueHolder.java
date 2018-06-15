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
package org.eclipse.persistence.internal.indirection;

import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;

/**
 * UnitOfWorkTransformerValueHolder wraps a database-stored object and
 * implements behavior to access it. The object is read from
 * the database by invoking a user-specified transformer object.
 * This value holder is used only in the unit of work.
 *
 * Most of the methods ignore the actual attribute values and are
 * simply used to trigger instantiation.
 *
 * @author    Sati
 */
public class UnitOfWorkTransformerValueHolder extends UnitOfWorkValueHolder {
    protected transient Object cloneOfObject;
    protected transient Object object;

    public UnitOfWorkTransformerValueHolder(ValueHolderInterface attributeValue, Object original, Object clone, AbstractTransformationMapping mapping, UnitOfWorkImpl unitOfWork) {
        this(attributeValue, clone, mapping, unitOfWork);
        this.object = original;
        this.cloneOfObject = clone;
    }

    protected UnitOfWorkTransformerValueHolder(ValueHolderInterface attributeValue, Object clone, DatabaseMapping mapping, UnitOfWorkImpl unitOfWork) {
        super(attributeValue, clone, mapping, unitOfWork);
    }

    /**
     * Backup the clone attribute value.
     */
    @Override
    protected Object buildBackupCloneFor(Object cloneAttributeValue) {
        return buildCloneFor(cloneAttributeValue);
    }

    /**
     * Clone the original attribute value.
     */
    @Override
    public Object buildCloneFor(Object originalAttributeValue) {
        return getMapping().buildCloneForPartObject(originalAttributeValue, getObject(), null, getCloneOfObject(), getUnitOfWork(), null, true, true);
    }

    protected Object getCloneOfObject() {
        return cloneOfObject;
    }

    protected Object getObject() {
        return object;
    }

    /**
     * Ensure that the backup value holder is populated.
     */
    @Override
    public void setValue(Object theValue) {
        // Must force instantiation to be able to compare with the old value.
        if (!this.isInstantiated) {
            instantiate();
        }
        super.setValue(theValue);
    }
}
