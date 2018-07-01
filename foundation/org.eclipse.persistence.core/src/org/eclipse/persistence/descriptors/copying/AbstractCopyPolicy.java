/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;

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

    @Override
    public abstract Object buildClone(Object domainObject, Session session) throws DescriptorException;

    /**
     * By default use the buildClone.
     */
    @Override
    public Object buildWorkingCopyClone(Object domainObject, Session session) throws DescriptorException {
        return buildClone(domainObject, session);
    }

    /**
     * By default create a new instance.
     */
    @Override
    public Object buildWorkingCopyCloneFromRow(Record row, ObjectBuildingQuery query, Object primaryKey, UnitOfWork uow) throws DescriptorException {
        return this.descriptor.getObjectBuilder().buildNewInstance();
    }

    /**
     * INTERNAL:
     * Clones the CopyPolicy
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

    /**
     * Return the descriptor.
     */
    protected ClassDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * Do nothing by default.
     */
    @Override
    public void initialize(Session session) throws DescriptorException {
        // Do nothing by default.
    }

    /**
     * Set the descriptor.
     */
    @Override
    public void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
    }

}
