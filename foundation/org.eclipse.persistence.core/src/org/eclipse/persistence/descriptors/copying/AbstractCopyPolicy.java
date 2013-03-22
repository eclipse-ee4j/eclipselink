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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.descriptors.copying;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.ObjectBuildingQuery;

/**
 * <p><b>Purpose</b>: Allows customization of how an object is cloned.
 * This class defines common behavior that allows a subclass to be used
 * and set on a descriptor to provide a special cloning routine for how an object
 * is cloned in a unit of work.
 */
public abstract class AbstractCopyPolicy implements CopyPolicy {
    protected ClassDescriptor descriptor;

    public AbstractCopyPolicy() {
        super();
    }

    public abstract Object buildClone(Object domainObject, Session session) throws DescriptorException;

    /**
     * By default use the buildClone.
     */
    public Object buildWorkingCopyClone(Object domainObject, Session session) throws DescriptorException {
        return buildClone(domainObject, session);
    }
    
    /**
     * By default create a new instance.
     */
    public Object buildWorkingCopyCloneFromRow(Record row, ObjectBuildingQuery query, Object primaryKey, UnitOfWork uow) throws DescriptorException {
        return this.descriptor.getObjectBuilder().buildNewInstance();
    }

    /**
     * INTERNAL:
     * Clones the CopyPolicy
     */
    public Object clone() {
        try {
            // clones itself
            return super.clone();
        } catch (Exception exception) {
        }
        return null;
    }

    /**
     * Return the descriptor.
     */
    protected ClassDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * Do nothing by default.
     */
    public void initialize(Session session) throws DescriptorException {
        // Do nothing by default.
    }

    /**
     * Set the descriptor.
     */
    public void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
    }

}
