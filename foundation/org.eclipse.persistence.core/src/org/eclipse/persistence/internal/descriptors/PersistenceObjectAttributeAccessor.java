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
//     James Sutherland - initial API and implementation
package org.eclipse.persistence.internal.descriptors;

import org.eclipse.persistence.exceptions.DescriptorException;

/**
 * Used with weaving to access attributes without using reflection.
 */
public class PersistenceObjectAttributeAccessor extends InstanceVariableAttributeAccessor {

    public PersistenceObjectAttributeAccessor(String attributeName) {
        this.attributeName = attributeName.intern();
    }

    /**
     * Returns the value of the attribute on the specified object.
     */
    @Override
    public Object getAttributeValueFromObject(Object object) {
        return ((PersistenceObject)object)._persistence_get(this.attributeName);
    }

    /**
     * Allow any initialization to be performed with the descriptor class.
     */
    @Override
    public void initializeAttributes(Class descriptorClass) throws DescriptorException {
        this.attributeName = attributeName.intern();
        super.initializeAttributes(descriptorClass);
    }


    /**
     * Sets the value of the instance variable in the object to the value.
     */
    @Override
    public void setAttributeValueInObject(Object object, Object value) {
        ((PersistenceObject)object)._persistence_set(this.attributeName, value);
    }
}
