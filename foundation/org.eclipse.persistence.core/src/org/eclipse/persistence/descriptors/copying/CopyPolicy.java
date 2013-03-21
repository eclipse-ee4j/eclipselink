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

import java.io.*;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.sessions.*;

/**
 * <p><b>Purpose</b>: Allows customization of how an object is cloned.
 * An implementer of CopyPolicy can be set on a descriptor to provide
 * special cloning routine for how an object is cloned in a unit of work.
 * 
 * By default the InstantiationCopyPolicy is used which creates a new instance of
 * the class to be copied into.
 * 
 * The CloneCopyPolicy can also be used that uses a clone method in the object
 * to clone the object.  When a clone method is used it avoid the requirement of having to
 * copy over each of the direct attributes.
 * 
 * @see org.eclipse.persistence.descriptors.copying.CloneCopyPolicy
 * @see org.eclipse.persistence.descriptors.copying.InstantiationCopyPolicy
 */
public interface CopyPolicy extends Cloneable, Serializable {

    /**
     * Return a shallow clone of the object for usage with object copying, or unit of work backup cloning.
     */
    Object buildClone(Object object, Session session) throws DescriptorException;

    /**
     * Return a shallow clone of the object for usage with the unit of work working copy.
     */
    Object buildWorkingCopyClone(Object object, Session session) throws DescriptorException;
            
    /**
     * Return an instance with the primary key set from the row, used for building a working copy during a unit of work transactional read.
     */
    Object buildWorkingCopyCloneFromRow(Record row, ObjectBuildingQuery query, Object primaryKey, UnitOfWork uow) throws DescriptorException;
           
    /**
     * Clone the CopyPolicy.
     */
    Object clone();

    /**
     * Allow for any initialization or validation required.
     */
    void initialize(Session session) throws DescriptorException;

    /**
     * Set the descriptor.
     */
    void setDescriptor(ClassDescriptor descriptor);

    /**
     * Return if this copy policy creates a new instance, vs a clone.
     */
    boolean buildsNewInstance();
}
