/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.IdentityHashtable;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.AggregateMapping;


//This is an assisting class for AggregateMapping related tests
public class TestAggregateMapping extends AggregateMapping {

    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, IdentityHashtable visitedObjects) {

    }

    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, IdentityHashtable visitedObjects) {

    }

    public Object getAttributeValueFromObject(Object object) {
        return null;
    }

    public void setReferenceDescriptor(ClassDescriptor aDescriptor) {
        referenceDescriptor = aDescriptor;
    }
}
