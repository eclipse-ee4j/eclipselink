/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial API and implementation
 ******************************************************************************/  
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
    public Object getAttributeValueFromObject(Object object) {
        return ((PersistenceObject)object)._persistence_get(this.attributeName);
    }
    
    /**
     * Allow any initialization to be performed with the descriptor class.
     */
    public void initializeAttributes(Class descriptorClass) throws DescriptorException {
        this.attributeName = attributeName.intern();
        super.initializeAttributes(descriptorClass);
    }

    
    /**
     * Sets the value of the instance variable in the object to the value.
     */
    public void setAttributeValueInObject(Object object, Object value) {
        ((PersistenceObject)object)._persistence_set(this.attributeName, value);
    }
}
